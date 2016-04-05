package com.netease.napm.test.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.netease.napm.test.utils.Const;

/**
 * @Description
 * @author hzdaiyuan
 * @date 2016-3-30 下午3:04:31
 */
@Service
public class HttpService implements NapmCombService {
    private static HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    private static final Logger logger = Logger.getLogger(HttpService.class);

    /**
     * 
     * @Description invoke remote http service
     * @param url
     * @return
     */
    @Override
    public boolean invoke(String url, String dataType) throws Exception {
        return httpGet(url);
    }

    public boolean invoke(String url, String param, String dataType) throws Exception {
        if (Const.GET.equals(dataType)) {
            return httpGet(url + param);
        } else if (Const.POST.equals(dataType)) {
            return httpPost(url, param);
        } else {
            /**
             * other don't support now
             */
            logger.warn("unsupport the dataType:" + dataType);
            return false;
        }
    }

    private boolean httpGet(String url) throws Exception {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = httpClientBuilder.build();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity reponseEntity = response.getEntity();
            if (reponseEntity != null) {
                // read stream
                // InputStream instream = entity.getContent();
                // read str
                // String rpStr = EntityUtils.toString(entity);
                // result = new String(result.getBytes(rescharset),
                // Const.DEFAULT_CHARSET);
                // logger.info("httpGET request result:" + rpStr);
                // read byte
                byte[] btArray = EntityUtils.toByteArray(reponseEntity);
                String result = new String(btArray, Const.DEFAULT_CHARSET);
                logger.info("httpGET request result:" + result);
            }
        } catch (Exception e) {
            logger.error("httpGET request error cause:", e);
            throw e;
        } finally {
            if (httpclient != null) {
                // HTTP connect close
                httpclient.close();
            }
        }
        return true;
    }

    private boolean httpPost(String url, String body) throws Exception {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = httpClientBuilder.build();
            StringEntity entity = new StringEntity(body, "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity reponseEntity = response.getEntity();
            if (reponseEntity != null) {
                // read stream
                // InputStream instream = entity.getContent();
                // read str
                // String rpStr = EntityUtils.toString(entity);
                // result = new String(result.getBytes(rescharset),
                // Const.DEFAULT_CHARSET);
                // logger.info("httpGET request result:" + rpStr);
                // read byte
                byte[] btArray = EntityUtils.toByteArray(reponseEntity);
                String result = new String(btArray, Const.DEFAULT_CHARSET);
                logger.info("httpPOST request result:" + result);
            }
        } catch (Exception e) {
            logger.error("httpPOST request error cause:", e);
            throw e;
        } finally {
            if (httpclient != null) {
                // HTTP connect close
                httpclient.close();
            }
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        HttpService httpService = new HttpService();
        httpService
                .invoke("http://localhost:8080/api/v1/products/dyp1/request/abnormalDetails?type=error&orderBy=responseTime&timePeriod=30&isInitiative=0",
                        Const.GET);
        JSONObject body = new JSONObject();
        body.put("productId", "dyp1");
        body.put("service", "dys2");
        body.put("node", "dyn4");
        body.put("agentType", "java");
        httpService.invoke("http://localhost:8080/manage/regist", body.toJSONString(), Const.POST);
    }
}
