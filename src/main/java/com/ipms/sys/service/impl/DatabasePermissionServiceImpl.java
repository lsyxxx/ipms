package com.ipms.sys.service.impl;

import com.ipms.sys.mapper.RoleFuncMapper;
import com.ipms.sys.model.entity.RoleFunc;
import com.ipms.sys.model.entity.User;
import com.ipms.sys.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 通过数据库获取权限
 */
@Service
@Slf4j
public class DatabasePermissionServiceImpl implements PermissionService {

    private SqlSessionFactory sqlSessionFactory;
    private RoleFuncMapper roleFuncMapper;

    public DatabasePermissionServiceImpl(SqlSessionFactory sqlSessionFactory, RoleFuncMapper roleFuncMapper) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.roleFuncMapper = roleFuncMapper;
    }

    @Override
    public void loadPermissionBy(User user) {

    }

    /**
     * 设置sqlSession的ExecutorType后，当前会话只能是batch，不会使用spring自动事务管理
     * @param list
     */
    @Override
    public void addRoleFuncRelations(List<RoleFunc> list) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        try {
            RoleFuncMapper mapper = sqlSession.getMapper(RoleFuncMapper.class);
            list.forEach(i -> mapper.insert(i));
            sqlSession.commit();
        } catch (Exception e) {
            log.error("Insert sys_role_func error - ", e);
            sqlSession.rollback();
        } finally {
            if (!Objects.isNull(sqlSession)) {
                 sqlSession.close();
            }
        }
    }

    public void insertOne(RoleFunc roleFunc) {
        roleFuncMapper.insert(roleFunc);
    }
}
