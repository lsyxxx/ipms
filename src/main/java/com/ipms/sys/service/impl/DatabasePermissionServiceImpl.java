package com.ipms.sys.service.impl;

import com.ipms.sys.mapper.RoleFuncMapper;
import com.ipms.sys.model.entity.RoleFunc;
import com.ipms.sys.model.entity.User;
import com.ipms.sys.service.BatchProcessService;
import com.ipms.sys.service.PermissionService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通过数据库获取权限
 */
@Service
public class DatabasePermissionServiceImpl implements PermissionService {

    private SqlSessionFactory sqlSessionFactory;

    public DatabasePermissionServiceImpl(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public void loadPermissionBy(User user) {

    }

    @Override
    public void addRoleFuncRelations(List<RoleFunc> list) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            RoleFuncMapper mapper = sqlSession.getMapper(RoleFuncMapper.class);
            list.forEach(i -> mapper.insert(i));
            sqlSession.commit();
        }
    }
}
