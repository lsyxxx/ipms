package com.abt.common.util;

import com.abt.common.model.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ResponseUtil {

    private static void returnJson(HttpServletResponse response, String json) throws IOException {
        setHeader(response);
        write(response, json);
    }

    public static void returnInvalidToken(HttpServletResponse response) throws IOException {
        setHeader(response);
        String json = R.invalidToken().toJson();
        write(response, json);
    }

    public static void setHeader(HttpServletResponse response) {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }


    public static void write(HttpServletResponse response, String json) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
        writer.close();
    }

    public static void success(HttpServletResponse response) throws IOException {
        setHeader(response);
        write(response, R.success().toJson());
    }

    public static void returnFail(HttpServletResponse response, String failMsg) throws IOException {
        setHeader(response);
        write(response, JsonUtil.toJson(R.fail(failMsg)));
    }

    public static void returnSessionOutOfTime(HttpServletResponse response) throws IOException {
        setHeader(response);
        write(response, R.invalidSession().toJson());
    }
}
