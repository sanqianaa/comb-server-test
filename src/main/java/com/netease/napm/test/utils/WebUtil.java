package com.netease.napm.test.utils;

import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

import com.netease.napm.test.meta.HttpConfig;

/**
 * @Description
 * @author hzdaiyuan
 * @date 2016-3-3 下午7:23:12
 */

public class WebUtil {
    public static final String DEFAULT_ENCODING = "utf-8";

    public static void setErrorResponse(HttpServletResponse response, ModelMap model, HttpConfig httpConfig,
            long start, long delay, int code) throws InterruptedException {
        long threadDelay = delay == 0 ? httpConfig.getDelay() : delay;
        int statusCode = code == 0 ? httpConfig.getStatusCode() : code;
        
        model.put("code", statusCode);
        model.put("delay", threadDelay);
        response.setStatus(statusCode);
        response.setCharacterEncoding(DEFAULT_ENCODING);
        // sleep
        long sleepMillis = threadDelay - (System.currentTimeMillis() - start);
        if (sleepMillis > 0) {
            Thread.sleep(sleepMillis);
        }
    }

    public static void setErrorResponse(HttpServletResponse response, ModelMap model, int status, String errormsg)
            throws InterruptedException {
        model.put("code", status);
        model.put("msg", errormsg);
        response.setStatus(status);
        response.setCharacterEncoding(DEFAULT_ENCODING);
    }
}
