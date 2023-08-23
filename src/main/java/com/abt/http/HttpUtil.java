package com.abt.http;

import com.abt.common.util.JsonUtil;
import com.abt.http.dto.WebApiDto;
import com.abt.http.dto.WebApiToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 *
 */
@Slf4j
public class HttpUtil {

    /**
     * post请求，需要注意处理异常
     * fluent api
     * @param url: 请求url
     * @param json: json 字符串
     */
    public String postJson(String url, String json, WebApiToken token) throws IOException {
        return Request.Post(url).setHeader(token.getTokenKey(), token.getTokenValue()).bodyString(json, APPLICATION_JSON)
                .execute().returnContent().toString();
    }

    /**
     * simple get
     * 需要注意处理异常，无法接收异常时返回的json信息
     * @param url
     * @return json string
     * @throws IOException
     */
    public static String simpleGet(String url) throws IOException{
        return Request.Get(url).execute().returnContent().toString();
    }

    public static void main(String[] args) throws IOException, ParseException {
        HttpUtil http = new HttpUtil();
//        String url = "http://7386n59g91.goho.co:10008/api/check/GetUserProfile?X-Token=eb8d981c";
        String url = "http://7386n59g91.goho.co:10008/api/check/GetUserProfile?";
//        HttpResponse response = Request.Get(url).execute().returnResponse();

        String postJson = "{\"name\":\"测试测试6\",\"status\":0,\"createTime\":\"2023-08-11 09:33:01\",\"createId\":\"\",\"typeName\":\"\",\"typeId\":\"\",\"id\":\"248e02da-15e9-4026-a4a2-746020d7497c\"}";
//        http.get(url);
        String updateUrl = "http://7386n59g91.goho.co:10008/api/roles/update";
        String updateResult = http.postJson(updateUrl, postJson, new WebApiToken());
        WebApiDto dto = JsonUtil.webApiDto(updateResult);
        System.out.println(dto.getMessage());

//        String json = simpleGet(url);
//        System.out.println(json);
//        WebApiDto res = JsonUtil.ObjectMapper().readValue(json, WebApiDto.class);
//        UserView user = res.getResult();
//        System.out.println(String.format("code={}", res.getCode()));
//        System.out.println(String.format("message={}", res.getMessage()));
//        System.out.println(String.format("result={}", user.toString()));
//        WebApiDto dto = JsonUtil.ObjectMapper().readValue(new URL(url), WebApiDto.class);
//        System.out.println(res.toString());


    }
}