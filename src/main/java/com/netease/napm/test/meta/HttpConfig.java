package com.netease.napm.test.meta;

/**
 * @Description
 * @author hzdaiyuan
 * @date 2016-3-3 下午6:56:45
 */

public class HttpConfig {

    private String path;
    private int statusCode;
    private long delay;

    public HttpConfig() {
        super();
    }

    public HttpConfig(String path, int statusCode, long delay) {
        super();
        this.path = path;
        this.statusCode = statusCode;
        this.delay = delay;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

}
