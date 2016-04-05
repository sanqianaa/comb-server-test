package com.netease.napm.test.controller;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.netease.napm.test.meta.HttpConfig;
import com.netease.napm.test.meta.InvokeConfig;
import com.netease.napm.test.service.DbService;
import com.netease.napm.test.service.HttpService;
import com.netease.napm.test.utils.Const;
import com.netease.napm.test.utils.WebUtil;

/**
 * @Description
 * @author hzdaiyuan
 * @date 2016-3-3 下午6:45:47
 */
@Controller
public class MockController {
    private static final Logger logger = Logger.getLogger(MockController.class);
    private static final int PATH_SIZE = 225;
    private static final int DELAY_MAX = Integer.MAX_VALUE - 1;
    private static final String PATH_PATTERN = "^[0-9a-zA-Z]+(.[/0-9a-zA-Z-_]+)?";
    private static Pattern r = Pattern.compile(PATH_PATTERN);
    private static Map<String, HttpConfig> requestMap = new HashMap<String, HttpConfig>();

    @Resource
    DbService dbService;
    @Autowired
    HttpService httpService;

    @RequestMapping(value = "/expectation", method = RequestMethod.POST)
    public String expectation(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        String requestBody = null;
        try {
            InputStream input = request.getInputStream();
            requestBody = IOUtils.toString(input, "utf-8");
            HttpConfig httpConfig = JSON.parseObject(requestBody, HttpConfig.class);
            /*
             * param vaild
             */
            String errmsg = isVaildPath(httpConfig);
            if (StringUtils.isNotBlank(errmsg)) {
                model.addAttribute("code", 400);
                model.addAttribute("errorMsg", errmsg);
                logger.error(errmsg);
                return Const.DEFAULT_VIEW;
            }

            /**
             * add or update path
             */
            requestMap.put(httpConfig.getMethod() + httpConfig.getPath(), httpConfig);
            String successLog = String.format("add path success,config:path=%s;method=%s;code=%d;delay=%d",
                    httpConfig.getPath(), httpConfig.getMethod(), httpConfig.getStatusCode(), httpConfig.getDelay());
            logger.info(successLog);

            model.addAttribute("code", 200);
            model.addAttribute("msg", successLog);
        } catch (Exception e) {
            logger.error(
                    String.format("parse body error! raw_data:%s;ip=%s", requestBody, request.getHeader("X-From-Ip")),
                    e);
            model.addAttribute("code", 400);
            model.addAttribute("errorMsg", String.format("parse body error! raw_data:%s", requestBody));
        }
        return Const.DEFAULT_VIEW;
    }

    @RequestMapping(value = "/map", method = RequestMethod.GET)
    public String pathDIY(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
        logger.info(String.format("request path map by ip=%s", request.getHeader("X-From-Ip")));
        model.addAttribute("code", 200);
        model.addAttribute("pathMap", requestMap);
        return Const.DEFAULT_VIEW;
    }

    @RequestMapping(value = "/{path}", method = RequestMethod.GET)
    public String invokePathGET(@PathVariable String path,
            @RequestParam(value = "delay", required = false, defaultValue = "0") long delay,
            @RequestParam(value = "code", required = false, defaultValue = "0") int code, HttpServletRequest request,
            @RequestParam(value = "data", required = false, defaultValue = "") String data,
            HttpServletResponse response, ModelMap model) throws Exception {
        long start = System.currentTimeMillis();
        logger.info(String.format("request path=%s by ip=%s|param: %s", path, request.getHeader("X-From-Ip"), data));
        if (StringUtils.isBlank(path)) {
            logger.error("invalid path: " + path);
            WebUtil.setErrorResponse(response, model, 404, "invalid path");
        }
        data = StringUtils.isBlank(data) ? data : URLDecoder.decode(data, Const.DEFAULT_CHARSET);
        if (requestMap.containsKey(Const.GET + path)) {
            HttpConfig httpConfig = requestMap.get(Const.GET + path);
            delay = delay == 0 ? httpConfig.getDelay() : delay;
            code = code == 0 ? httpConfig.getStatusCode() : code;
            greadingClassHandle(data, start, delay, code);
            WebUtil.setErrorResponse(response, model, start, delay, code);
        } else {
            logger.error("invalid path: " + path);
            WebUtil.setErrorResponse(response, model, 404, "invalid path");
        }
        return Const.DEFAULT_VIEW;
    }

    @RequestMapping(value = "/{path}", method = RequestMethod.POST)
    public String invokePathPOST(@PathVariable String path,
            @RequestParam(value = "delay", required = false, defaultValue = "0") long delay,
            @RequestParam(value = "code", required = false, defaultValue = "0") int code, HttpServletRequest request,
            HttpServletResponse response, ModelMap model) throws Exception {
        long start = System.currentTimeMillis();
        logger.info(String.format("request path=%s by ip=%s", path, request.getHeader("X-From-Ip")));
        if (StringUtils.isBlank(path)) {
            logger.error("invalid path: " + path);
            WebUtil.setErrorResponse(response, model, 404, "invalid path");
        }
        if (requestMap.containsKey(Const.POST + path)) {
            HttpConfig httpConfig = requestMap.get(Const.POST + path);
            delay = delay == 0 ? httpConfig.getDelay() : delay;
            code = code == 0 ? httpConfig.getStatusCode() : code;
            InputStream input = request.getInputStream();
            String body = IOUtils.toString(input, "utf-8");
            greadingClassHandle(body, start, delay, code);
            WebUtil.setErrorResponse(response, model, start, delay, code);
        } else {
            logger.error("invalid path: " + path);
            WebUtil.setErrorResponse(response, model, 404, "invalid path");
        }
        return Const.DEFAULT_VIEW;
    }

    private String isVaildPath(HttpConfig httpConfig) {
        if (null == httpConfig) {
            return "config is null";

        }
        if (httpConfig.getDelay() < 0 || httpConfig.getDelay() > DELAY_MAX || StringUtils.isBlank(httpConfig.getPath())) {
            return "delay unsupport:" + httpConfig.getDelay();
        }
        if (StringUtils.isBlank(httpConfig.getPath()) || !(r.matcher(httpConfig.getPath()).matches())
                || httpConfig.getPath().length() > PATH_SIZE) {
            return "path unsupport:" + httpConfig.getPath();
        }
        if (StringUtils.isBlank(httpConfig.getMethod()) || !Const.METHOD_LIST.contains(httpConfig.getMethod())) {
            return "method unsupport:" + httpConfig.getMethod();
        }
        return null;
    }

    private boolean greadingClassHandle(String data, long start, long delay, int code) throws Exception {
        if (StringUtils.isBlank(data)) {
            /**
             * just httpdemo
             */
            logger.info("no other request");
            return false;
        }

        /**
         * invoke other(no try-catch)
         */
        InvokeConfig cofig = JSON.parseObject(data, InvokeConfig.class);
        if (Const.JDBC.equals(cofig.getRequestType())) {
            /**
             * jdbc invoke demo
             */
            return dbService.invoke(cofig.getUrl(), cofig.getBody(), cofig.getDataType());
        } else if (Const.HTTP.equals(cofig.getRequestType())) {
            /**
             * http invoke demo
             */
            return httpService.invoke(cofig.getUrl(), cofig.getBody(), cofig.getDataType());
        } else {
            logger.warn("unsupport requestType:" + cofig.getRequestType());
            return false;
        }

    }
}
