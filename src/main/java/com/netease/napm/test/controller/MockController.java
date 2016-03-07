package com.netease.napm.test.controller;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.netease.napm.test.meta.HttpConfig;
import com.netease.napm.test.utils.WebUtil;

/**
 * @Description
 * @author hzdaiyuan
 * @date 2016-3-3 下午6:45:47
 */
@Controller
public class MockController {
    private static final Logger logger = Logger.getLogger(MockController.class);
    public static final String DEFAULT_VIEW = "unused_view";
    public static final int PATH_SIZE = 225;
    public static final int DELAY_MAX = Integer.MAX_VALUE - 1;

    private static Map<String, HttpConfig> requestMap = new HashMap<String, HttpConfig>();

    @RequestMapping(value = "/expectation", method = RequestMethod.POST)
    public String expectation(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        String requestBody = null;
        try {
            InputStream input = request.getInputStream();
            requestBody = IOUtils.toString(input, "utf-8");
            HttpConfig httpConfig = JSON.parseObject(requestBody, HttpConfig.class);
            if (httpConfig.getDelay() < 0 || httpConfig.getDelay() > DELAY_MAX
                    || StringUtils.isBlank(httpConfig.getPath())) {
                throw new NumberFormatException();
            }
            requestMap.put(httpConfig.getPath(), httpConfig);
            logger.info(String.format("add path success,config:path=%s;code=%d;delay=%d;ip=%s", httpConfig.getPath(),
                    httpConfig.getStatusCode(), httpConfig.getDelay(), request.getHeader("X-From-Ip")));
            model.addAttribute("code", 200);
            model.addAttribute(
                    "msg",
                    String.format("add path success,config:path=%s;code=%d;delay=%d", httpConfig.getPath(),
                            httpConfig.getStatusCode(), httpConfig.getDelay()));
        } catch (Exception e) {
            logger.error(
                    String.format("parse body error! raw_data:%s;ip=%s", requestBody, request.getHeader("X-From-Ip")),
                    e);
            model.addAttribute("code", 400);
            model.addAttribute("errorMsg", String.format("parse body error! raw_data:%s", requestBody));
        }
        return DEFAULT_VIEW;
    }

    @RequestMapping(value = "/map", method = RequestMethod.GET)
    public String pathDIY(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
        logger.info(String.format("request path map by ip=%s", request.getHeader("X-From-Ip")));
        model.addAttribute("code", 200);
        model.addAttribute("pathMap", requestMap);
        return DEFAULT_VIEW;
    }

    @RequestMapping(value = "/{path}", method = RequestMethod.GET)
    public String pathDIY(@PathVariable String path,
            @RequestParam(value = "delay", required = false, defaultValue = "0") long delay,
            @RequestParam(value="code",required=false,defaultValue="0")int code,
            HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
        long start = System.currentTimeMillis();
        logger.info(String.format("request path=%s by ip=%s", path, request.getHeader("X-From-Ip")));
        if (StringUtils.isBlank(path)) {
            logger.error("invalid path: " + path);
            WebUtil.setErrorResponse(response, model, 404, "invalid path");
        }
        path = URLDecoder.decode(path);
        if (requestMap.containsKey(path)) {
            WebUtil.setErrorResponse(response, model, requestMap.get(path), start,delay,code);
        } else {
            logger.error("invalid path: " + path);
            WebUtil.setErrorResponse(response, model, 404, "invalid path");
        }
        return DEFAULT_VIEW;
    }
}
//xx