/*
 Navicat Premium Data Transfer

 Source Server         : mysql_local
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : localhost:3306
 Source Schema         : ipms

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 03/08/2023 17:23:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dept_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门编号',
  `dept_abr` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门简称',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父部门id',
  `sort` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门显示顺序',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `role_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '多个角色以\",\"分隔',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (24, 'GPYi3GySN8', 'Public Relations', 0, NULL, NULL, '2023-03-28 14:01:16', '2008-02-12 09:25:09', NULL, '0', NULL);
INSERT INTO `sys_dept` VALUES (25, '7KvgWRi7Wl', 'Logistics', 0, NULL, NULL, '2006-09-29 22:30:46', '2011-03-23 19:57:42', NULL, '0', NULL);
INSERT INTO `sys_dept` VALUES (26, 'RAXwOWsYVk', '物流部', 0, NULL, NULL, '2009-05-13 16:34:43', '2009-03-18 20:42:48', NULL, '0', NULL);
INSERT INTO `sys_dept` VALUES (27, 'v0Q41ILS7y', 'Production', 0, NULL, NULL, '2014-01-11 04:46:40', '2002-02-26 22:16:59', NULL, '0', NULL);
INSERT INTO `sys_dept` VALUES (28, 'KbVbH3oezU', '研究及开发部', 0, NULL, NULL, '2008-07-14 14:51:52', '2016-04-19 20:57:45', NULL, '0', NULL);
INSERT INTO `sys_dept` VALUES (30, 'FK0pstuQrr', 'Engineering', 0, NULL, NULL, '2016-12-02 19:53:06', '2008-03-18 09:58:09', NULL, '0', NULL);
INSERT INTO `sys_dept` VALUES (31, 'D89qmYDd1I', '工程部', 0, NULL, NULL, '2011-03-21 11:17:14', '2007-07-17 11:48:00', NULL, '0', NULL);
INSERT INTO `sys_dept` VALUES (32, 'kyCbJIT7E7', 'Engineering', 0, NULL, NULL, '2017-08-23 01:21:57', '2015-10-30 22:19:56', NULL, '0', NULL);
INSERT INTO `sys_dept` VALUES (33, 'Q5zOshw0F4', '会计及金融部', 0, NULL, NULL, '2006-07-02 02:05:39', '2000-04-25 16:21:46', NULL, '0', NULL);

-- ----------------------------
-- Table structure for sys_function
-- ----------------------------
DROP TABLE IF EXISTS `sys_function`;
CREATE TABLE `sys_function`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `path` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '功能路径, 每一级为权限编号, 以\'.\'分隔: 一级权限.二级权限.三级权限.n级',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `pid` bigint NOT NULL COMMENT 'parent id, 如果为0则表示是一级功能',
  `rid` bigint NOT NULL COMMENT 'root id, 所属一级功能id',
  `url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '链接',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '说明',
  `component` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件',
  `state` bit(1) NULL DEFAULT NULL COMMENT '收缩',
  `sort` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '排序',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
  `push_btn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '功能按钮',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `delete_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `is_leaf` bit(1) NOT NULL COMMENT '是否是最底层功能(单一功能点), 0否(等于模块), 1是',
  `level` tinyint NOT NULL COMMENT '当前功能层级。0表示根节点',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `url`(`url` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 285 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '功能模块表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_function
-- ----------------------------
INSERT INTO `sys_function` VALUES (1, '0', 'SuperAdmin', 0, 0, '/', '超级权限', '/', b'1', '0', b'1', NULL, NULL, NULL, '0', b'0', 0);
INSERT INTO `sys_function` VALUES (2, '1', '系统管理', 0, 0, '/sys', '系统管理-用户列表', '/sys', b'1', '0910', b'1', '电脑版', '', 'setting', '0', b'0', 0);
INSERT INTO `sys_function` VALUES (3, '3', '财务', 0, 0, '/ac', '财务', '/ac', b'1', '1', b'1', NULL, NULL, NULL, '0', b'0', 0);
INSERT INTO `sys_function` VALUES (4, '4', '人力', 0, 0, '/hr', '人事', '/hr', b'1', '2', b'1', NULL, NULL, NULL, '0', b'0', 0);
INSERT INTO `sys_function` VALUES (5, '1.1.1', '系统管理-查看用户列表', 1, 2, '/sys/u/all', '查看用户列表', '/sys/u', b'1', '111', b'1', NULL, NULL, NULL, '0', b'1', 2);
INSERT INTO `sys_function` VALUES (261, '1.1.2', '系统管理-修改用户信息', 1, 2, '/sys/u/upd', '修改用户信息', '/sys/u', b'1', '112', b'1', NULL, NULL, NULL, '0', b'1', 2);
INSERT INTO `sys_function` VALUES (262, '1.1', '系统管理-用户模块', 1, 2, '/sys/u', '用户模块', '/sys', b'1', '11', b'1', NULL, NULL, NULL, '0', b'0', 1);
INSERT INTO `sys_function` VALUES (263, '1.1.3', '系统管理-删除用户', 1, 2, '/sys/u/del', '删除用户', '/sys/u', b'1', '113', b'1', NULL, NULL, NULL, '0', b'1', 2);
INSERT INTO `sys_function` VALUES (264, '3.1', '财务-会计', 3, 3, '/ac/a', '会计', '/ac/a', b'1', '1', b'1', NULL, NULL, NULL, '0', b'0', 1);
INSERT INTO `sys_function` VALUES (265, '3.2', '财务-出纳', 3, 3, '/ac/c', '出纳', '/ac/c', b'1', '2', b'1', NULL, NULL, NULL, '0', b'0', 1);
INSERT INTO `sys_function` VALUES (266, '3.1.1', '财务-会计-凭证', 264, 3, '/ac/a/vc', '凭证模块', '/ac/a', b'1', '1', b'1', NULL, NULL, NULL, '0', b'0', 2);
INSERT INTO `sys_function` VALUES (267, '3.1.1.1', '财务-会计-凭证-查看凭证', 266, 3, '/ac/a/vc/all', '查看凭证', '/ac/a', b'1', '1', b'1', NULL, NULL, NULL, '0', b'1', 3);
INSERT INTO `sys_function` VALUES (268, '3.1.1.2', '财务-会计-凭证-修改凭证', 266, 3, '/ac/a/vc/upd', '修改凭证', '/ac/a', b'1', '2', b'1', NULL, NULL, NULL, '0', b'1', 3);
INSERT INTO `sys_function` VALUES (269, '3.1.1.3', '财务-会计-凭证-删除凭证', 266, 3, '/ac/a/vc/del', '删除凭证', '/ac/a', b'1', '3', b'1', NULL, NULL, NULL, '0', b'1', 3);
INSERT INTO `sys_function` VALUES (270, '3.2.1', '财务-出纳-资金', 265, 3, '/ac/c/cash', '资金模块', '/ac/c', b'1', '1', b'1', NULL, NULL, NULL, '0', b'0', 2);
INSERT INTO `sys_function` VALUES (272, '3.2.1.1', '财务-出纳-资金-流出', 265, 3, '/ac/c/cash/out', '资金流出', '/ac/c', b'1', '2', b'1', NULL, NULL, NULL, '0', b'0', 3);
INSERT INTO `sys_function` VALUES (273, '3.2.1.1', '财务-出纳-资金-流入', 265, 3, '/ac/c/cash/in', '资金流入', '/ac/c', b'1', '1', b'1', NULL, NULL, NULL, '0', b'0', 3);
INSERT INTO `sys_function` VALUES (274, '3.3', '财务-报表', 3, 3, '/ac/b', '财务报表', '/ac/b', b'1', '1', b'1', NULL, NULL, NULL, '0', b'0', 1);
INSERT INTO `sys_function` VALUES (275, '1.2', '系统管理-角色模块', 1, 2, '/sys/r', '角色管理', '/sys/r', b'1', '2', b'1', NULL, NULL, NULL, '0', b'0', 1);
INSERT INTO `sys_function` VALUES (276, '1.3', '系统管理-功能模块', 1, 3, '/sys/f', '功能权限模块', '/sys/f', b'1', '3', b'1', NULL, NULL, NULL, '0', b'0', 1);
INSERT INTO `sys_function` VALUES (277, '1.4', '系统管理-部门模块', 1, 2, '/sys/d', '部门模块', '/sys/d', b'1', '3', b'1', NULL, NULL, NULL, '0', b'0', 1);
INSERT INTO `sys_function` VALUES (278, '3.4', '财务-报销', 3, 3, '/ac/r', '报销模块', '/ac/r', b'1', '4', b'1', NULL, NULL, NULL, '0', b'0', 1);
INSERT INTO `sys_function` VALUES (279, '3.3.1', '财务-报表-资产负债表', 274, 3, '/ac/b/bal', '资产负债表', '/ac/b', b'1', '1', b'1', NULL, NULL, NULL, '0', b'0', 2);
INSERT INTO `sys_function` VALUES (280, '3.3.2', '财务-报表-单项报表', 274, 3, '/ac/b/item', '单项统计报表', '/ac/b', b'1', '2', b'1', NULL, NULL, NULL, '0', b'0', 2);
INSERT INTO `sys_function` VALUES (281, '3.3.3', '财务-报表-定制报表', 274, 3, '/ac/b/cus', '定制报表', '/ac/b/', b'1', '3', b'1', NULL, NULL, NULL, '0', b'0', 2);
INSERT INTO `sys_function` VALUES (282, '3.3.3.1', '财务-报表-定制报表-查看', 281, 3, '/ac/b/cus/show', '定制报表查看', '/ac/b/', b'1', '1', b'1', NULL, NULL, NULL, '0', b'1', 3);
INSERT INTO `sys_function` VALUES (283, '3.3.3.2', '财务-报表-定制报表-下载', 281, 3, '/ac/b/cus/download', '定制报表下载', '/ac/b/', b'1', '1', b'1', NULL, NULL, NULL, '0', b'1', 3);
INSERT INTO `sys_function` VALUES (284, '3.5', '财务-税务', 3, 3, '/ac/t', '税务模块', '/ac/t', b'1', '5', b'1', NULL, NULL, NULL, '0', b'0', 1);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色代码',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `enabled` bit(1) NULL DEFAULT b'1' COMMENT '启用，1启用, 0未启用',
  `sort` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '排序',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (4, '管理员', 'ADMIN', '全部数据', NULL, b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (10, '租户', 'TNT', '全部数据', '', b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (16, '销售经理', 'SALE_M', '全部数据', 'ddd', b'1', NULL, 63, '0');
INSERT INTO `sys_role` VALUES (17, '销售代表', 'SALE_A', '个人数据', 'rrr', b'1', NULL, 63, '0');
INSERT INTO `sys_role` VALUES (21, '财务主管', 'ACC_M', '个人数据', NULL, b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (22, '普通用户', 'USER', '', '仅有查看功能', b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (23, '会计专员', 'ACC_A', NULL, NULL, b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (24, '出纳专员', 'CAS_A', NULL, NULL, b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (25, '税务专员', 'TAX_A', NULL, NULL, b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (26, '税务主管', 'TAX_M', NULL, NULL, b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (27, '总经理', 'GM', NULL, NULL, b'1', NULL, NULL, '0');

-- ----------------------------
-- Table structure for sys_role_func
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_func`;
CREATE TABLE `sys_role_func`  (
  `role_id` bigint NOT NULL COMMENT 'sys_role: id',
  `func_id` bigint NOT NULL COMMENT 'sys_function: id。如果不是功能组，则表示单一功能点；如果是功能组，则表示具有该模块所有子功能权限',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'username',
  `create_date` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`func_id`, `role_id`) USING BTREE,
  INDEX `func_id`(`func_id` ASC) USING BTREE,
  INDEX `role_id2`(`role_id` ASC) USING BTREE,
  CONSTRAINT `func_id` FOREIGN KEY (`func_id`) REFERENCES `sys_function` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `role_id2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色-功能关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_func
-- ----------------------------
INSERT INTO `sys_role_func` VALUES (4, 1, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (27, 1, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (4, 2, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (4, 3, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (23, 264, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (21, 267, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (24, 270, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (21, 274, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (23, 274, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (24, 274, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (26, 284, NULL, NULL);
INSERT INTO `sys_role_func` VALUES (27, 284, NULL, '2023-08-03 16:56:46');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户姓名--例如张三',
  `login_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登陆密码',
  `leader_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否经理，0否，1是',
  `position` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职位',
  `department` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属部门',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `phonenum` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `ismanager` tinyint NOT NULL DEFAULT 1 COMMENT '是否为管理者 0==管理者 1==员工',
  `isystem` tinyint NOT NULL DEFAULT 0 COMMENT '是否系统自带数据 ',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态，0：正常，1：删除，2封禁',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户描述信息',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `weixin_open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信绑定',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 156 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (63, '测试用户', 'jsh', 'e10adc3949ba59abbe56e057f20f883e', '0', '主管', NULL, '666666@qq.com', '1123123123132', 1, 1, 0, '', NULL, NULL, 63);
INSERT INTO `sys_user` VALUES (111, 'abt', 'abt', '$2a$10$tggV1KjdpW7PQ3QOB6TKfOe9uc8yM6nhOJYMh.IU1u/ryGpJboBGm', '0', '软件开发', NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (120, '管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '0', NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (131, 'test111', 'testlogin', 'e10adc3949ba59abbe56e057f20f883e', '0', '总监', 'acc', '7777777@qq.com', '', 1, 0, 0, '', NULL, NULL, 63);
INSERT INTO `sys_user` VALUES (146, 'Chang Hiu Tung', 'hiutung4', 'vMNLyWSsGM', '0', 'Web developer', 'Information Technology Support', 'htchang@mail.com', '7828 357850', 1, 0, 0, NULL, NULL, NULL, 339);
INSERT INTO `sys_user` VALUES (147, 'FinaceManager', 'fi_manager', '$2a$10$tggV1KjdpW7PQ3QOB6TKfOe9uc8yM6nhOJYMh', '0', 'Finance', '财务部', 'kooth@hotmail.com', '52-165-4685', 1, 0, 0, NULL, NULL, NULL, 200);
INSERT INTO `sys_user` VALUES (148, 'Bernard Ford', 'bernf', '3m9FzodjEA', '0', 'Veterinarian', 'Sales', 'bernf@outlook.com', '5763 806743', 1, 0, 0, NULL, NULL, NULL, 727);
INSERT INTO `sys_user` VALUES (149, 'Tsui Kwok Yin', 'kyt', 'Bd3kyVq68X', '0', 'Information security analyst', 'Logistics', 'kwokyin405@gmail.com', '11-248-3823', 1, 0, 0, NULL, NULL, NULL, 991);
INSERT INTO `sys_user` VALUES (150, 'Ku Wai Han', 'waihan74', '77tBkovBGA', '0', 'Auditor', 'Purchasing', 'kwaihan@gmail.com', '74-582-9230', 1, 0, 0, NULL, NULL, NULL, 537);
INSERT INTO `sys_user` VALUES (151, 'Du Lan', 'ladu', 'CngvccDSAS', '0', 'Actuary', 'Export', 'landu@mail.com', '212-755-0934', 1, 0, 0, NULL, NULL, NULL, 764);
INSERT INTO `sys_user` VALUES (152, 'Yung Fu Shing', 'yung90', 'Kdu6gHkMJt', '0', 'Technical support', '法律部', 'yungfushing@gmail.com', '(116) 203 0572', 1, 0, 0, NULL, NULL, NULL, 657);
INSERT INTO `sys_user` VALUES (153, 'Hu Jialun', 'hu1998', 'ZOVbz0DFO6', '0', 'Information security analyst', 'Accounting & Finance', 'hu5@gmail.com', '330-774-6554', 1, 0, 0, NULL, NULL, NULL, 674);
INSERT INTO `sys_user` VALUES (154, 'Jiang Zitao', 'zitaoj', 'u49vWINjqy', '0', 'Orthodontist', 'Information Technology Support', 'zitajia@outlook.com', '70-7403-5576', 1, 0, 0, NULL, NULL, NULL, 918);
INSERT INTO `sys_user` VALUES (155, 'Tian Zhennan', 'zhennantian611', 'LSlow77q9b', '0', 'Marketing director', 'Research & Development', 'zhennantian15@hotmail.com', '755-706-9307', 1, 0, 0, NULL, NULL, NULL, 918);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `user_id` bigint NOT NULL COMMENT 'sys_user: id',
  `role_id` bigint NOT NULL COMMENT 'sys_role: id',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_date` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建日期',
  `delete_flag` bit(1) NULL DEFAULT b'0' COMMENT '删除标志：0未删除,1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 111, 4, NULL, NULL, b'0');
INSERT INTO `sys_user_role` VALUES (3, 111, 21, NULL, NULL, b'0');
INSERT INTO `sys_user_role` VALUES (4, 147, 21, NULL, NULL, b'0');
INSERT INTO `sys_user_role` VALUES (5, 147, 26, NULL, NULL, b'0');

SET FOREIGN_KEY_CHECKS = 1;
