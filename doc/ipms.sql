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

 Date: 22/07/2023 18:01:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for jsh_user
-- ----------------------------
DROP TABLE IF EXISTS `jsh_user`;
CREATE TABLE `jsh_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户姓名--例如张三',
  `login_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '登陆密码',
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
) ENGINE = InnoDB AUTO_INCREMENT = 146 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jsh_user
-- ----------------------------
INSERT INTO `jsh_user` VALUES (63, '测试用户', 'jsh', 'e10adc3949ba59abbe56e057f20f883e', '0', '主管', NULL, '666666@qq.com', '1123123123132', 1, 1, 0, '', NULL, NULL, 63);
INSERT INTO `jsh_user` VALUES (111, 'abt1', 'abt1', '123456', '0', '软件开发', NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `jsh_user` VALUES (120, '管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '0', NULL, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL, 0);
INSERT INTO `jsh_user` VALUES (131, 'test123', 'test123', 'e10adc3949ba59abbe56e057f20f883e', '0', '总监', NULL, '7777777@qq.com', '', 1, 0, 0, '', NULL, NULL, 63);

SET FOREIGN_KEY_CHECKS = 1;
