package com.netease.napm.test.utils;

import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

/**
 * @Description
 * @author hzdaiyuan
 * @date 2016-3-3 下午7:23:12
 */

public class WebUtil {
    public static final String DEFAULT_ENCODING = "utf-8";

    public static void setErrorResponse(HttpServletResponse response, ModelMap model, long start, long delay, int code)
            throws InterruptedException {

        model.put("code", code);
        model.put("delay", delay);
        response.setStatus(code);
        response.setCharacterEncoding(DEFAULT_ENCODING);
        // sleep
        long sleepMillis = delay - (System.currentTimeMillis() - start);
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
