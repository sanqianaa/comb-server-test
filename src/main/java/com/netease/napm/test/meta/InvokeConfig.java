package com.netease.napm.test.meta;

/**
 * @Description
 * @author hzdaiyuan
 * @date 2016-4-1 下午3:48:17
 */

public class InvokeConfig {
    private String requestType;
    private String dataType;
    private String url;
    private String body;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
