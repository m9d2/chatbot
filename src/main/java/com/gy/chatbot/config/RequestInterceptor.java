package com.gy.chatbot.config;

import com.gy.chatbot.common.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
 
import java.io.IOException;
import java.util.Optional;
 
@Slf4j
@Component
public class RequestInterceptor implements ClientHttpRequestInterceptor {
 
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers = httpRequest.getHeaders();
        headers.set(HttpHeaders.COOKIE, UserContext.getCookie());
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}