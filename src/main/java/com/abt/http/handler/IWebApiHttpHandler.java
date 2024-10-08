package com.abt.http.handler;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

public interface IWebApiHttpHandler<T> extends IHttpClientHandler<T> {

    T getHandler(ClassicHttpResponse response) throws IOException, ParseException;
    T postHandler(ClassicHttpResponse response) throws IOException, ParseException;
}
