package com.abt.http;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpUtilTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void postJson() {
    }

    @Test
    void simpleGet() {
    }

    /**
     *
     * @param url
     * @return
     */
    public String get(String url) {
        try(CloseableHttpClient httpclient = HttpClients.createDefault())  {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url)
                    .build();
            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            httpclient.execute(httpGet, response -> {
                if (response.getCode() >= 300) {
//                    log.error("Http响应异常！Http code = {} {}", response.getCode(), response.getReasonPhrase());
                }
                final HttpEntity responseEntity = response.getEntity();
                if (responseEntity == null) {
                    return null;
                }
                String entityStr = EntityUtils.toString(responseEntity);

                EntityUtils.consume(responseEntity);
                return entityStr;
            });
            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}