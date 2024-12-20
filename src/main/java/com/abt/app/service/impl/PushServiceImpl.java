package com.abt.app.service.impl;

import com.abt.app.entity.PushRegister;
import com.abt.app.respository.PushRegisterRepository;
import com.abt.app.service.PushService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 推送服务
 */
@Service
@Slf4j
public class PushServiceImpl implements PushService {

    private final PushRegisterRepository pushRegisterRepository;
    private final EmployeeService employeeService;

    public static final String jpush_appKey = "5852054ccdf204b7c5c819bc";
    public static final String jpush_masterSecret = "f3b0b8ce982000777e642ea6";
    public static final String jpush_restapi = "https://api.jpush.cn/v3/push";

    public PushServiceImpl(PushRegisterRepository pushRegisterRepository, EmployeeService employeeService) {
        this.pushRegisterRepository = pushRegisterRepository;
        this.employeeService = employeeService;
    }

    @Override
    public void register(PushRegister pushRegister) {
        String userid = pushRegister.getUserid();
        final EmployeeInfo emp = employeeService.findUserByUserid(userid);
        if (emp == null) {
            throw new BusinessException("用户不存在(id=" + userid + ")");
        }
        pushRegisterRepository.save(pushRegister);
    }


    /**
     * 推送一条消息给指定用户
     */
    public void push() {
        RestTemplate restTemplate = new RestTemplate();
        // 设置 Authorization Header
        HttpHeaders headers = new HttpHeaders();
        String auth = jpush_appKey + ":" + jpush_masterSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        // 发起 GET 请求
        ResponseEntity<String> response = restTemplate.exchange(jpush_restapi, HttpMethod.POST, entity, String.class);
        System.out.println("响应状态: " + response.getStatusCode());
        System.out.println("响应内容: " + response.getBody());
    }


    public static void main(String[] args) {
        PushServiceImpl impl = new PushServiceImpl(null, null);
        impl.push();
    }
}
