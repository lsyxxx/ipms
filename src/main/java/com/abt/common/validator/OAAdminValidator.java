package com.abt.common.validator;

import com.abt.common.util.TokenUtil;
import com.abt.flow.model.entity.FlowSetting;
import com.abt.http.dto.WebApiConfig;
import com.abt.http.dto.WebApiDto;
import com.abt.http.dto.WebApiToken;
import com.abt.http.service.HttpConnectService;
import com.abt.sys.model.dto.UserView;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 验证是否是OA审批管理员/具有权限
 */
@Data
@Component
@Slf4j
public class OAAdminValidator implements IValidator<UserView>{

    @Value("${webapi.http.api.loadforuser}")
    private String api;
    private final WebApiConfig webApiConfig;
    private final HttpConnectService<WebApiDto> httpConnectService;
    private final List<FlowSetting> oaAuthList;

    public OAAdminValidator(WebApiConfig webApiConfig, HttpConnectService<WebApiDto> httpConnectService, @Qualifier("oaAuthList") List<FlowSetting> oaAuthList) {
        this.webApiConfig = webApiConfig;
        this.httpConnectService = httpConnectService;
        this.oaAuthList = oaAuthList;
    }

    @Override
    public ValidationResult validate(UserView userView) {
        String userId = userView.getId();
        WebApiToken webApiToken = WebApiToken.of(TokenUtil.getToken());

        final WebApiDto webApiDto = httpConnectService.get(api+userId, webApiToken);
        final Object result = webApiDto.getResult();
        if (result == null) {
            log.warn("用户[{}]没有分配任何角色", userView.simpleInfo());
            return ValidationResult.fail("No OA Auth");
        }

        List<String> auths = (List<String>) result;
        log.info("用户拥有的角色: {}", auths);
        if (oaAuthList == null || oaAuthList.isEmpty()) {
            log.warn("没有配置OA管理员权限");
            return ValidationResult.success();
        }
        List<String> oa = oaAuthList.stream().map(FlowSetting::getValue).toList();
        final boolean has = auths.stream().anyMatch(oa::contains);
        if (has) {
            return ValidationResult.success();
        }
        log.warn("用户[{}]不具有[OA审批权限]", userView.simpleInfo());
        return ValidationResult.fail("No OA Auth");
    }


}
