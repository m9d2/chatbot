package com.yann.autoreply.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class HttpClientUtils {

        /**
         * 发送get请求
         * @param url 发送请求的url
         * @return 响应内容
         * @throws IOException
         */
    public static String get(String url) throws IOException {
        CloseableHttpClient httpclient = getInstanceClient();
        System.setProperty ("jsse.enableSNIExtension", "false");
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("User-Agent", "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)");
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            HttpEntity httpentity = response.getEntity();
            return EntityUtils.toString(httpentity);
        } finally {
            response.close();
        }
    }

    public static InputStream getInputStream(String url) throws IOException {
        System.setProperty ("jsse.enableSNIExtension", "false");
        CloseableHttpClient httpclient = getInstanceClient();
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            HttpEntity httpentity = response.getEntity();
            return httpentity.getContent();
        } finally {
            response.close();
        }
    }

    /**
     * 发送post请求,参数是String类型
     * @param url 发送请求的url
     * @param params json格式
     * @return 响应内容
     */
    public static String post(String url, String params) throws IOException {
        CloseableHttpClient httpclient = getInstanceClient();
        HttpPost httppost = new HttpPost(url);
        StringEntity entity = new StringEntity(params);
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httppost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        try {
            HttpEntity httpentity = response.getEntity();
            return EntityUtils.toString(httpentity);
        } finally {
            response.close();
        }
    }

    /**
     * 发送post请求,参数是JSONObject类型
     * @param url 发送请求的url
     * @param params json
     * @return 响应内容
     */
    public static String post(String url, JSONObject params) throws IOException {
       return post(url, params.toString());
    }

    private static CloseableHttpClient httpclient = null;
    private static RequestConfig globalConfig = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD_STRICT)
            .build();
    private static synchronized CloseableHttpClient getInstanceClient() {
        if(httpclient == null) {
            httpclient = HttpClients.custom()
                    .setDefaultRequestConfig(globalConfig)
                    .setConnectionManagerShared(true)
                    .build();
            return httpclient;
        } else {
            return httpclient;
        }
    }
}
