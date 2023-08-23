package com.abt.sys.service.impl;

import com.abt.sys.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过webapi获取用户权限
 */
@Service
public class WebApiPermissionService implements PermissionService {
    @Override
    public List getPermissionsBy(String token) {
        return new ArrayList();
    }
}
