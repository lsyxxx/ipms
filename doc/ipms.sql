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

 Date: 28/07/2023 16:50:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dept_no` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '部门编号',
  `dept_abr` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '部门简称',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父部门id',
  `sort` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '部门显示顺序',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_function
-- ----------------------------
DROP TABLE IF EXISTS `sys_function`;
CREATE TABLE `sys_function`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `number` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '编号',
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '名称',
  `parent_number` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '上级编号',
  `url` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '链接',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '说明',
  `component` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '组件',
  `state` bit(1) NULL DEFAULT NULL COMMENT '收缩',
  `sort` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '排序',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `type` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '类型',
  `push_btn` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '功能按钮',
  `icon` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '图标',
  `delete_flag` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `url`(`url` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 260 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '功能模块表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_function
-- ----------------------------
INSERT INTO `sys_function` VALUES (1, '0001', '系统管理', '0', '/sys/u/all', '系统管理-用户列表', '/sys', b'1', '0910', b'1', '电脑版', '', 'setting', '0');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '名称',
  `type` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '类型',
  `price_limit` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '价格屏蔽 1-屏蔽采购价 2-屏蔽零售价 3-屏蔽销售价',
  `value` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '值',
  `description` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '描述',
  `enabled` bit(1) NULL DEFAULT NULL COMMENT '启用',
  `sort` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '排序',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (4, '管理员', '全部数据', NULL, NULL, NULL, b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (10, '租户', '全部数据', NULL, NULL, '', b'1', NULL, NULL, '0');
INSERT INTO `sys_role` VALUES (16, '销售经理', '全部数据', NULL, NULL, 'ddd', b'1', NULL, 63, '0');
INSERT INTO `sys_role` VALUES (17, '销售代表', '个人数据', NULL, NULL, 'rrr', b'1', NULL, 63, '0');
INSERT INTO `sys_role` VALUES (21, '财务主管', '个人数据', NULL, NULL, NULL, b'1', NULL, NULL, '0');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户姓名--例如张三',
  `login_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '登陆密码',
  `leader_flag` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '0' COMMENT '是否经理，0否，1是',
  `position` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '职位',
  `department` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '所属部门',
  `email` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `phonenum` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `ismanager` tinyint NOT NULL DEFAULT 1 COMMENT '是否为管理者 0==管理者 1==员工',
  `isystem` tinyint NOT NULL DEFAULT 0 COMMENT '是否系统自带数据 ',
  `Status` tinyint NULL DEFAULT 0 COMMENT '状态，0：正常，1：删除，2封禁',
  `description` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户描述信息',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '备注',
  `weixin_open_id` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '微信绑定',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 156 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (63, '测试用户', 'jsh', 'e10adc3949ba59abbe56e057f20f883e', '0', '主管', NULL, '666666@qq.com', '1123123123132', 1, 1, 0, '', NULL, NULL, 63);
INSERT INTO `sys_user` VALUES (111, 'abt', 'abt', '$2a$10$tggV1KjdpW7PQ3QOB6TKfOe9uc8yM6nhOJYMh.IU1u/ryGpJboBGm', '0', '软件开发', NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (120, '管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '0', NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (131, 'test123', 'test123', 'e10adc3949ba59abbe56e057f20f883e', '0', '总监', NULL, '7777777@qq.com', '', 1, 0, 0, '', NULL, NULL, 63);
INSERT INTO `sys_user` VALUES (146, 'Chang Hiu Tung', 'hiutung4', 'vMNLyWSsGM', '0', 'Web developer', 'Information Technology Support', 'htchang@mail.com', '7828 357850', 1, 0, 0, NULL, NULL, NULL, 339);
INSERT INTO `sys_user` VALUES (147, 'Koo Tsz Hin', 'tszhinko13', 'cBbpgHu05Z', '0', 'Wedding planner', '采购部', 'kooth@hotmail.com', '52-165-4685', 1, 0, 0, NULL, NULL, NULL, 200);
INSERT INTO `sys_user` VALUES (148, 'Bernard Ford', 'bernf', '3m9FzodjEA', '0', 'Veterinarian', 'Sales', 'bernf@outlook.com', '5763 806743', 1, 0, 0, NULL, NULL, NULL, 727);
INSERT INTO `sys_user` VALUES (149, 'Tsui Kwok Yin', 'kyt', 'Bd3kyVq68X', '0', 'Information security analyst', 'Logistics', 'kwokyin405@gmail.com', '11-248-3823', 1, 0, 0, NULL, NULL, NULL, 991);
INSERT INTO `sys_user` VALUES (150, 'Ku Wai Han', 'waihan74', '77tBkovBGA', '0', 'Auditor', 'Purchasing', 'kwaihan@gmail.com', '74-582-9230', 1, 0, 0, NULL, NULL, NULL, 537);
INSERT INTO `sys_user` VALUES (151, 'Du Lan', 'ladu', 'CngvccDSAS', '0', 'Actuary', 'Export', 'landu@mail.com', '212-755-0934', 1, 0, 0, NULL, NULL, NULL, 764);
INSERT INTO `sys_user` VALUES (152, 'Yung Fu Shing', 'yung90', 'Kdu6gHkMJt', '0', 'Technical support', '法律部', 'yungfushing@gmail.com', '(116) 203 0572', 1, 0, 0, NULL, NULL, NULL, 657);
INSERT INTO `sys_user` VALUES (153, 'Hu Jialun', 'hu1998', 'ZOVbz0DFO6', '0', 'Information security analyst', 'Accounting & Finance', 'hu5@gmail.com', '330-774-6554', 1, 0, 0, NULL, NULL, NULL, 674);
INSERT INTO `sys_user` VALUES (154, 'Jiang Zitao', 'zitaoj', 'u49vWINjqy', '0', 'Orthodontist', 'Information Technology Support', 'zitajia@outlook.com', '70-7403-5576', 1, 0, 0, NULL, NULL, NULL, 918);
INSERT INTO `sys_user` VALUES (155, 'Tian Zhennan', 'zhennantian611', 'LSlow77q9b', '0', 'Marketing director', 'Research & Development', 'zhennantian15@hotmail.com', '755-706-9307', 1, 0, 0, NULL, NULL, NULL, 918);

SET FOREIGN_KEY_CHECKS = 1;
