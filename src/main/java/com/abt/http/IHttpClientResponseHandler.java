package com.abt.http;

import com.abt.http.dto.WebApiToken;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

/**
 * 处理HttpClient Response
 * @param <T>
 */
public interface IHttpClientResponseHandler<T> {

    void setRequestHeader(ClassicHttpRequest request, WebApiToken token);

    /**
     * 非正常http code的处理
     */
    void handleHttpStatus(ClassicHttpResponse response);

    T handle(ClassicHttpResponse response) throws IOException, ParseException;

}
