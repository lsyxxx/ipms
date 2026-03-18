package com.abt.http.handler;

import com.abt.http.dto.WebApiToken;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

/**
 * 处理HttpClient Response
 * @param <T>
 */
public interface IHttpClientHandler<T> {

    void setRequestHeader(ClassicHttpRequest request, WebApiToken token);

    /**
     * 非正常http code的处理
     */
    void handleHttpStatus(ClassicHttpResponse response) throws HttpResponseException;

    /**
     * 处理response
     * @param response
     * @return
     * @throws IOException
     * @throws ParseException
     */
    T handle(ClassicHttpResponse response) throws IOException, ParseException;

}
