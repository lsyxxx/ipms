package com.abt.sys.repository;

import com.abt.common.model.User;
import com.abt.sys.model.entity.SystemUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {

    private UserRepositoryImpl userRepo;
    private JdbcTemplate jdbcTemplate;

    private Connection connection;
    @BeforeEach
    void setUp() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String jdbcUrl = "jdbc:sqlserver://localhost:1433;database=abt_biz;trustServerCertificate=true";

        dataSource.setDriverClassName(driverName); // 设置你的数据库驱动
        dataSource.setUrl(jdbcUrl); // 设置数据库连接URL
        dataSource.setUsername("sa"); // 设置数据库用户名
        dataSource.setPassword("123456"); // 设置数据库密码

        connection = DriverManager.getConnection(jdbcUrl, "sa", "123456");

        // 使用数据源初始化JdbcTemplate
        jdbcTemplate = new JdbcTemplate(dataSource);
        userRepo = new UserRepositoryImpl(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserBy() {
        final SystemUser userBy = userRepo.getUserBy("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
        System.out.println(userBy);
    }

    @Test
    void getSimple() {
        final User userBy = userRepo.getSimpleUserInfo("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
        System.out.println(userBy);
    }

    @Test
    void getSimpleUserInfo() {
//        final User userBy = userRepo.getSimpleUserInfo("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
//        System.out.println(userBy);
        String sql = "select Id, Account, Name from [User] where Id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("Id");
                    String account = resultSet.getString("Account");
                    String name = resultSet.getString("Name");
                    System.out.printf("id: %s, account: %s, name: %s \n", id, account, name);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}