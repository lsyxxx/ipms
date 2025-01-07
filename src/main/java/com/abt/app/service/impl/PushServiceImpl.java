package com.abt.app.service.impl;

import com.abt.app.entity.JPushRegister;
import com.abt.app.entity.JPushResponse;
import com.abt.app.respository.JPushRegisterRepository;
import com.abt.app.service.PushService;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.SysLog;
import com.abt.sys.repository.SysLogRepository;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.service.SysLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Service;



/**
 * 推送服务
 */
@Service
@Slf4j
public class PushServiceImpl implements PushService {

    private final JPushRegisterRepository jpushRegisterRepository;
    private final EmployeeService employeeService;
    private final SysLogRepository sysLogRepository;

    public static final String jpush_appKey = "a9bbbc141c5d16c8989ac1a8";
    public static final String jpush_masterSecret = "ef59ca1f166b2fcb658b876a";
    /**
     * 推送api
     */
    public static final String jpush_restapi_push = "https://api.jpush.cn/v3/push";

    public PushServiceImpl(JPushRegisterRepository jpushRegisterRepository, EmployeeService employeeService, SysLogRepository sysLogRepository) {
        this.jpushRegisterRepository = jpushRegisterRepository;
        this.employeeService = employeeService;
        this.sysLogRepository = sysLogRepository;
    }

    @Override
    public void register(JPushRegister pushRegister) {
        String userid = pushRegister.getUserid();
        final EmployeeInfo emp = employeeService.findUserByUserid(userid);
        if (emp == null) {
            throw new BusinessException("用户不存在(id=" + userid + ")");
        }
        jpushRegisterRepository.save(pushRegister);
    }


    @Override
    public void pushAndroid(String userid, String alert, String message, int badgeAddNum) {
        ValidateUtil.ensurePropertyNotnull(userid, "用户id");
        final JPushRegister jPushRegister = jpushRegisterRepository.findByUserid(userid);
        if (jPushRegister == null) {
            log.warn("用户{}未注册rid!", userid);
            return;
        }
        try {
            final String json = createAndroidJPushMessage(alert, message, badgeAddNum, jPushRegister.getRegisterId());
            doPush(json);
        } catch (Exception e) {
            log.error("推送消失失败!{}", e.getMessage(), e);
            SysLog sysLog = new SysLog();
            sysLog.setContent("JPush推送失败");
            sysLog.setTypeName("JPush推送");
            sysLog.setCreateId(userid);
            sysLog.setCreateTime(LocalDateTime.now());
            sysLog.setApplication(SysLogService.APPLICATION);
            sysLogRepository.save(sysLog);
        }
    }

    /**
     * 推送一条消息给指定用户
     * @param jsonBody 推送消息的json
     */
    private void doPush(String jsonBody) {
        // 设置 Authorization Header
        HttpHeaders headers = new HttpHeaders();
        String auth = jpush_appKey + ":" + jpush_masterSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        // 发送 POST 请求
        try {
            ResponseEntity<String> response = restTemplate.exchange(jpush_restapi_push, HttpMethod.POST, requestEntity, String.class);
            // 输出响应
            final HttpStatusCode statusCode = response.getStatusCode();
            if (statusCode.is2xxSuccessful()) {
                String resBody = response.getBody();
                final JPushResponse jr = JsonUtil.toObject(resBody, new TypeReference<JPushResponse>() {});
                log.info("JPush success! sendno: {}, msg_id: {}", jr.getSendno(), jr.getMsg_id());
            }
        } catch (HttpClientErrorException e) {
            String resBody = e.getResponseBodyAsString();
            log.error("JPush error: {}", resBody, e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 生成jpush api json
     */
    private String createAndroidJPushMessage(String alert, String title, int badgeAddNum, String rid) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        //root
        ObjectNode rootNode = objectMapper.createObjectNode();
        //platform
        ArrayNode platForm = objectMapper.createArrayNode();
        platForm.add("android");
        rootNode.set("platform", platForm);
        //notification
        ObjectNode notificationNode = objectMapper.createObjectNode();
        rootNode.set("notification", notificationNode);
        notificationNode.put("alert", alert);
        //android notification
        ObjectNode androidNotificationNode = objectMapper.createObjectNode();
        notificationNode.set("android", androidNotificationNode);
        androidNotificationNode.put("alert", alert);
        androidNotificationNode.put("title", title);
        androidNotificationNode.put("sound", "");
        androidNotificationNode.put("priority", 0);
        androidNotificationNode.put("category", "");
        androidNotificationNode.put("alert_type", 7);
        androidNotificationNode.put("style", 0);
        androidNotificationNode.put("large_icon", "");
        androidNotificationNode.put("badge_add_num", badgeAddNum);
        ObjectNode intentNode = objectMapper.createObjectNode();
        androidNotificationNode.set("intent", intentNode);
        //直接打开应用
        intentNode.put("url", "intent:#Intent;action=android.intent.action.MAIN;end");
        //audience node
        ObjectNode audienceNode = objectMapper.createObjectNode();
        rootNode.set("audience", audienceNode);
        //rid array
        ArrayNode ridArray = objectMapper.createArrayNode();
        audienceNode.set("registration_id", ridArray);
        ridArray.add(rid);
        //options
        ObjectNode optionsNode = objectMapper.createObjectNode();
        rootNode.set("options", optionsNode);
        optionsNode.put("classification", 0);
        optionsNode.put("time_to_live", 86400);
        optionsNode.put("apns_production", false);
        return objectMapper.writeValueAsString(rootNode);
    }

    public static void main(String[] args) throws JsonProcessingException {
        PushServiceImpl impl = new PushServiceImpl(null, null, null);
        final String json = impl.createAndroidJPushMessage("您有一条待办", "您有一条刘宋菀提交的费用报销申请待处理", 1, "100d8559087c6701c7c");
        impl.doPush(json);
    }
}
