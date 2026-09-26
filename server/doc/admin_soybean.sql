/*
 Navicat Premium Dump SQL

 Source Server         : admin_soybean
 Source Server Type    : MySQL
 Source Server Version : 80029 (8.0.29)
 Source Host           : localhost:3306
 Source Schema         : admin_soybean

 Target Server Type    : MySQL
 Target Server Version : 80029 (8.0.29)
 File Encoding         : 65001

 Date: 26/09/2026 23:28:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置ID（雪花）',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置名称',
  `config_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置编码',
  `config_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '配置值',
  `config_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '配置分组',
  `value_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'string' COMMENT '值类型：string/number/boolean/json',
  `is_builtin` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否内置：1是 0否',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用：1启用 0停用',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `config_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '配置说明',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`config_id`) USING BTREE,
  UNIQUE INDEX `uk_config_code`(`config_code` ASC) USING BTREE,
  INDEX `idx_config_group_order`(`config_group` ASC, `order_num` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_config_enabled`(`is_enabled` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('1227775834488705024', '系统名称', 'system.name', 'YLmao-Soybean', 'system', 'string', 1, 1, 1, '后台系统显示名称', '1662038524471218177', '2026-06-19 10:29:40', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227854583687155712', '系统简称', 'system.shortName', 'YLmao', 'system', 'string', 1, 1, 3, '侧边栏折叠、移动端、小空间展示', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227854583691350016', '系统Logo', 'system.logo', '/static/admin/images/ylmao/logo.png', 'system', 'string', 1, 1, 5, '后台左上角 Logo', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227854583691350017', '浏览器图标', 'system.favicon', '/static/ico/favicon.ico', 'system', 'string', 1, 1, 6, '浏览器 tab 图标', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227854583695544320', '版权信息', 'system.copyright', 'Copyright © 2026 YLmao', 'system', 'string', 1, 1, 4, '登录页、页脚', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227854583695544321', '联系邮箱', 'system.adminMail', '99999999@gmail.com', 'system', 'string', 1, 1, 7, '系统联系邮箱、异常通知联系人', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227854583695544322', '系统版本', 'system.version', '1.0.0', 'system', 'string', 1, 1, 8, '关于页面、页脚、诊断信息', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227854583699738624', '官网地址', 'system.website', 'www.baidu.com', 'system', 'string', 1, 1, 9, '登录页、关于页、品牌链接', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227854583699738625', '网站备案号', 'system.icp', '测试备案号', 'system', 'string', 1, 1, 10, '公网网站页脚备案', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227854583703932928', '公安备案号', 'system.policeIcp', '测试公安备案号', 'system', 'string', 1, 1, 11, '公网网站页脚公安备案', 'system', '2026-06-19 15:42:35', '1662038524471218177', '2026-09-21 00:12:39', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000011', '上传开关', 'upload.enabled', 'true', 'upload', 'boolean', 1, 1, 1, '是否允许后台上传文件', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-09-25 02:27:54', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000012', '存储方式', 'upload.storType', 'local', 'upload', 'string', 1, 1, 2, '上传存储方式：local 本地，oss/cos/minio 为后续扩展', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-09-25 02:27:55', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000013', '本地存储目录', 'upload.locPath', 'upload', 'upload', 'string', 1, 1, 3, '本地上传文件根目录，文件流接口按该目录读取文件', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-09-25 02:27:55', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000014', '公开访问前缀', 'upload.pubUrlPfx', '/upload', 'upload', 'string', 1, 1, 4, '上传文件访问接口前缀，由后端读取文件流返回，不使用静态资源映射', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-09-25 02:27:55', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000015', '单文件大小MB', 'upload.maxFileSzMb', '10', 'upload', 'number', 1, 1, 5, '业务层允许上传的单文件最大大小，单位 MB', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-09-25 02:27:55', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000016', '图片允许后缀', 'upload.imgExts', '[\"jpg\",\"jpeg\",\"png\",\"gif\",\"webp\"]', 'upload', 'json', 1, 1, 6, '图片上传场景允许的文件后缀，如头像、Logo、富文本图片', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-09-25 02:27:55', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000017', '文档允许后缀', 'upload.docsExts', '[\"pdf\",\"doc\",\"docx\",\"ppt\",\"pptx\",\"txt\"]', 'upload', 'json', 1, 1, 7, '普通文档附件上传场景允许的文件后缀', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-09-25 02:27:55', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000018', '表格允许后缀', 'upload.excelExts', '[\"xls\",\"xlsx\",\"csv\"]', 'upload', 'json', 1, 1, 8, '表格导入和模板上传场景允许的文件后缀', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-09-25 02:27:55', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000021', '登录日志开关', 'log.loginEn', 'true', 'log', 'boolean', 1, 1, 1, '是否记录登录、注销日志', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:41:13', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000022', '操作日志开关', 'log.operEn', 'true', 'log', 'boolean', 1, 1, 2, '是否记录后台业务操作日志', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:41:13', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000023', '配置审计开关', 'log.configEn', 'true', 'log', 'boolean', 1, 1, 3, '是否记录系统配置变更审计', 'system', '2026-09-21 00:24:17', 'system', '2026-09-21 00:24:17', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000027', '日志保留天数', 'log.retainDays', '0', 'log', 'number', 1, 1, 7, '操作日志保留天数，0 表示不自动清理', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-07-19 17:41:13', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000031', '账号失败锁定次数', 'security.acctFailLim', '5', 'security', 'number', 1, 1, 1, '账号密码连续错误达到该次数后锁定；0不锁号', 'system', '2026-07-19 23:17:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000032', 'IP失败拉黑阈值', 'security.ipFailLim', '10', 'security', 'number', 1, 1, 2, 'IP鉴权失败次数每达到该阈值的整数倍时自动拉黑或续期；0不自动拉黑', 'system', '2026-07-19 23:17:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000033', '自动拉黑分钟数', 'security.autoBanMin', '60', 'security', 'number', 1, 1, 3, '自动拉黑每次叠加的分钟数；0不自动拉黑', 'system', '2026-08-27 10:39:46', '', NULL, 0);
INSERT INTO `sys_config` VALUES ('1227899000000000034', '验证码IP限流', 'security.capIpLim', '10', 'security', 'number', 1, 1, 4, '同一IP每窗口最多拉取验证码次数；0关闭该项', 'system', '2026-08-27 09:00:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000035', '登录IP限流', 'security.loginIpLim', '20', 'security', 'number', 1, 1, 5, '同一IP每窗口最多提交登录次数；0关闭该项', 'system', '2026-08-27 09:00:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000036', '登录账号限流', 'security.loginAcctLim', '15', 'security', 'number', 1, 1, 6, '同一账号每窗口最多被提交登录次数；0关闭该项', 'system', '2026-08-27 09:00:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000037', '限流窗口分钟', 'security.winMinLim', '5', 'security', 'number', 1, 1, 7, '软拦固定窗口分钟数；0关闭整组软拦', 'system', '2026-08-27 09:00:00', '1662038524471218177', '2026-08-27 09:53:53', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000041', '任务扫描间隔秒', 'job.scanSecs', '600', 'job', 'number', 1, 1, 1, '定时扫描 sys_job 并重新注册触发的间隔秒数，最低 60，无上限', 'system', '2026-08-04 23:17:14', '1662038524471218177', '2026-08-26 11:40:37', 0);
INSERT INTO `sys_config` VALUES ('1227899000000000051', '已读保留天数', 'notice.readDays', '0', 'notice', 'number', 1, 1, 1, '收件箱已读保留天数，0 表示不自动清理', 'system', '2026-08-25 00:00:00', '', NULL, 0);
INSERT INTO `sys_config` VALUES ('1227899000000000052', '未读保留天数', 'notice.unreadDays', '0', 'notice', 'number', 1, 1, 2, '收件箱未读保留天数，0 表示不自动清理', 'system', '2026-08-25 00:00:00', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门id（雪花）',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '父部门id，根为0',
  `dept_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '部门路径，如 0,1001,1002',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `dept_leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `leader_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人电话',
  `leader_email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人邮箱',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`dept_id`) USING BTREE,
  UNIQUE INDEX `uk_dept_parent_name`(`parent_id` ASC, `dept_name` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('1', '0', '0', 'v2', 1, 'v2', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_dept` VALUES ('2', '1', '0,1', '技术部门', 2, 'x某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_dept` VALUES ('3', '1', '0,1', '人事部门', 3, 'a某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_dept` VALUES ('4', '2', '0,1,2', '开发一小组1', 4, 'b某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '1662038524471218177', '2026-06-09 01:58:22', 0);
INSERT INTO `sys_dept` VALUES ('5', '3', '0,1,3', '销售部门', 5, 'd某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '1662038524471218177', '2026-06-20 16:24:56', 0);
INSERT INTO `sys_dept` VALUES ('6', '5', '0,1,3,5', '销售一组', 6, 'e某某', '13012345678', 'v2@qq.com', 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_data_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典数据ID（雪花）',
  `dict_type_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型编码',
  `dict_data_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典数据标签',
  `dict_data_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典数据值',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否默认（1是 0否）',
  `dict_data_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典数据说明',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`dict_data_id`) USING BTREE,
  UNIQUE INDEX `uk_dict_data_label`(`dict_type_code` ASC, `dict_data_label` ASC) USING BTREE,
  UNIQUE INDEX `uk_dict_data_value`(`dict_type_code` ASC, `dict_data_value` ASC) USING BTREE,
  INDEX `idx_dict_data_type_order`(`dict_type_code` ASC, `order_num` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES ('1227900000000000101', 'sys_user_sex', '男', '0', 1, 1, '0', '', 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-06-20 16:25:09', 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000102', 'sys_user_sex', '女', '1', 1, 2, '1', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000201', 'sys_post_type', '管理岗', '1', 1, 1, '1', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000202', 'sys_post_type', '技术岗', '2', 1, 2, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000203', 'sys_post_type', '运营岗', '3', 1, 3, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000204', 'sys_post_type', '市场岗', '4', 1, 4, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000301', 'sys_notice_type', '系统通知', '1', 1, 1, '1', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000302', 'sys_notice_type', '运营活动', '2', 1, 2, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000303', 'sys_notice_type', '平台公告', '3', 1, 3, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000304', 'sys_notice_type', '用户私信', '4', 1, 4, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000401', 'sys_notice_receiver_type', '全体用户', '1', 1, 1, '1', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000402', 'sys_notice_receiver_type', '指定角色', '2', 1, 2, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000403', 'sys_notice_receiver_type', '指定部门', '3', 1, 3, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);
INSERT INTO `sys_dict_data` VALUES ('1227900000000000404', 'sys_notice_receiver_type', '指定个人', '4', 1, 4, '0', '', 'system', '2026-06-20 00:00:00', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_type_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典类型ID（雪花）',
  `dict_type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型名称',
  `dict_type_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型编码',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `dict_type_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型说明',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`dict_type_id`) USING BTREE,
  UNIQUE INDEX `uk_dict_type_code`(`dict_type_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES ('1227900000000000001', '用户性别', 'sys_user_sex', 1, 1, 'system', '2026-06-20 00:00:00', '1662038524471218177', '2026-06-20 16:29:49', '对应 sys_user.user_sex', 0);
INSERT INTO `sys_dict_type` VALUES ('1227900000000000002', '岗位类型', 'sys_post_type', 2, 1, 'system', '2026-06-20 00:00:00', '', NULL, '对应 sys_post.post_type', 0);
INSERT INTO `sys_dict_type` VALUES ('1227900000000000003', '公告类型', 'sys_notice_type', 3, 1, 'system', '2026-06-20 00:00:00', '', NULL, '对应 sys_notice.notice_type', 0);
INSERT INTO `sys_dict_type` VALUES ('1227900000000000004', '公告接收者类型', 'sys_notice_receiver_type', 4, 1, 'system', '2026-06-20 00:00:00', '', NULL, '对应 sys_notice.receiver_type', 0);

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `file_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件ID（雪花）',
  `folder_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属虚拟目录ID',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '原始文件名',
  `storage_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '物理存储键，如 2026/07/{fileId}.png',
  `storage_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'local' COMMENT '存储方式，当前仅 local',
  `file_suffix` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件后缀',
  `content_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'MIME类型',
  `file_size` bigint NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `file_scene` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'image' COMMENT '场景：image/document/excel',
  `need_login` tinyint(1) NOT NULL DEFAULT 1 COMMENT '预览是否需登录（1是 0否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1回收站',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '进入回收站时间',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_file_folder`(`folder_id` ASC, `is_del` ASC) USING BTREE,
  INDEX `idx_file_name`(`folder_id` ASC, `original_name` ASC, `is_del` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件资源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_filter
-- ----------------------------
DROP TABLE IF EXISTS `sys_filter`;
CREATE TABLE `sys_filter`  (
  `filter_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '访问控制ID（雪花）',
  `filter_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：IP / USER_ID',
  `filter_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '过滤值：IP / 用户ID',
  `filter_source` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'MANUAL' COMMENT '来源：MANUAL人工 AUTO自动',
  `filter_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '说明',
  `policy_mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'BLACK' COMMENT '策略：WHITE白名单 BLACK黑名单',
  `expire_time` datetime NOT NULL COMMENT '过期时间；永久为 9999-12-31 23:59:59',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  `uk_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS (if((`is_del` = 0),`filter_type`,NULL)) STORED COMMENT '未删唯一：类型' NULL,
  `uk_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS (if((`is_del` = 0),`filter_value`,NULL)) STORED COMMENT '未删唯一：值' NULL,
  PRIMARY KEY (`filter_id`) USING BTREE,
  UNIQUE INDEX `uk_filter_active`(`uk_type` ASC, `uk_value` ASC) USING BTREE,
  INDEX `idx_filter_type_value`(`filter_type` ASC, `filter_value` ASC) USING BTREE,
  INDEX `idx_filter_hit`(`is_del` ASC, `is_enabled` ASC, `policy_mode` ASC, `filter_type` ASC, `filter_value` ASC, `expire_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '访问控制（黑白名单）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_filter
-- ----------------------------

-- ----------------------------
-- Table structure for sys_folder
-- ----------------------------
DROP TABLE IF EXISTS `sys_folder`;
CREATE TABLE `sys_folder`  (
  `folder_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目录ID（雪花）',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '父目录ID，根为0',
  `folder_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目录路径，如 0,1001',
  `folder_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '目录名称',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `is_builtin` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否内置（1是 0否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1回收站',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '进入回收站时间',
  PRIMARY KEY (`folder_id`) USING BTREE,
  INDEX `idx_folder_parent`(`parent_id` ASC, `is_del` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件虚拟目录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_folder
-- ----------------------------
INSERT INTO `sys_folder` VALUES ('1229000000000000001', '0', '0', '未分类', 0, 1, 'system', '2026-07-19 00:00:00', '', NULL, 0, NULL);

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID（雪花）',
  `job_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务编码（与代码注册表对应）',
  `job_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_cron` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Cron 表达式（展示/下次执行计算）',
  `job_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务说明',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用：0-停用，1-启用（定时触发前校验）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-存在，1-删除',
  PRIMARY KEY (`job_id`) USING BTREE,
  UNIQUE INDEX `uk_job_code`(`job_code` ASC) USING BTREE,
  INDEX `idx_job_enabled_order`(`is_enabled` ASC, `order_num` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统内置定时任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES ('1229000000000000101', 'operateLogRetention', '操作日志保留清理', '0 0 3 * * ?', '按 log.retainDays 清理过期操作日志', 1, 0, 'system', '2026-07-22 00:00:00', '', NULL, 0);
INSERT INTO `sys_job` VALUES ('1229000000000000102', 'noticeExpireClean', '过期公告清理', '0 10 3 * * ?', '软删除已过期公告及收件箱关联', 2, 0, 'system', '2026-07-22 00:00:00', '', NULL, 0);
INSERT INTO `sys_job` VALUES ('1229000000000000103', 'noticeReadClean', '收件箱已读清理', '0 20 3 * * ?', '按 notice.readDays 软删收件箱已读记录', 3, 0, 'system', '2026-08-25 00:00:00', '', NULL, 0);
INSERT INTO `sys_job` VALUES ('1229000000000000104', 'noticeUnreadClean', '收件箱未读清理', '0 30 3 * * ?', '按 notice.unreadDays 软删收件箱未读记录', 4, 0, 'system', '2026-08-25 00:00:00', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行日志ID（雪花）',
  `job_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `job_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务编码快照',
  `trigger_type` tinyint(1) NOT NULL COMMENT '触发方式：1-定时，2-手动',
  `run_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行结果：SUCCESS成功，FAILED失败，SKIPPED跳过',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `cost_ms` bigint NOT NULL DEFAULT 0 COMMENT '耗时毫秒',
  `message` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结果摘要或失败原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE,
  INDEX `idx_job_log_job_start`(`job_id` ASC, `start_time` ASC) USING BTREE,
  INDEX `idx_job_log_code_start`(`job_code` ASC, `start_time` ASC) USING BTREE,
  INDEX `idx_job_log_job_status_end`(`job_id` ASC, `run_status` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统内置定时任务执行日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键（雪花）',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '父节点，根为 0',
  `menu_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '树路径，如 0,id1,id2，须与 parent 链一致并以自身 menu_id 结尾',
  `menu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '显示名称（侧栏/授权树）',
  `menu_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `menu_type` tinyint(1) NULL DEFAULT NULL COMMENT '0目录 1菜单 2按钮',
  `route_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端路由 name；目录/菜单用，按钮为空',
  `route_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端路由 path；目录/菜单用，按钮为空',
  `route_comp` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端组件标识（component）；目录多为 layout，按钮为空',
  `route_query` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由默认 query（JSON，如 {\"status\":\"1\"}）；无则空',
  `menu_href` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单外链；与站内路由二选一，无外链则空',
  `is_blank` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0本页 1新窗口（多用于外链）',
  `perm_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识；仅按钮填写，供 buttons[] / 接口鉴权；目录与页面必须为空',
  `menu_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标标识（解析方式见 icon_type）',
  `icon_type` tinyint(1) NULL DEFAULT NULL COMMENT '图标通道：1 Iconify 2 本地 SVG（可按项目扩展）',
  `i18n_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '国际化 key，空则用 menu_name',
  `keep_alive` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否缓存页面（0否 1是）',
  `is_show` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否在侧栏显示（1显示 0仅注册路由不进侧栏）',
  `active_menu` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '隐藏路由时高亮的菜单 route_name；无则空',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识（0未删 1已删）',
  PRIMARY KEY (`menu_id`) USING BTREE,
  UNIQUE INDEX `uk_menu_route_name`(`route_name` ASC) USING BTREE,
  INDEX `idx_menu_parent_order`(`parent_id` ASC, `order_num` ASC) USING BTREE,
  UNIQUE INDEX `uk_menu_parent_perm_code`(`parent_id` ASC, `perm_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理端菜单表（目录/菜单/按钮；按角色过滤后吐 SPA 路由 JSON）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('3000343202867900001', '0', '0,3000343202867900001', '首页', '工作台首页', 1, 'home', '/home', 'layout.base$view.home', NULL, NULL, 0, NULL, 'mdi:monitor-dashboard', 1, 'route.home', 0, 1, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900010', '0', '0,3000343202867900010', '系统管理', '用户 / 角色 / 菜单', 0, 'manage', '/manage', 'layout.base', NULL, NULL, 0, NULL, 'carbon:cloud-service-management', 1, 'route.manage', 0, 1, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900011', '3000343202867900010', '0,3000343202867900010,3000343202867900011', '菜单管理', '菜单管理', 1, 'manage_menu', '/manage/menu', 'view.manage_menu', NULL, NULL, 0, NULL, 'material-symbols:route', 1, 'route.manage_menu', 0, 1, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900012', '3000343202867900010', '0,3000343202867900010,3000343202867900012', '角色管理', '角色管理', 1, 'manage_role', '/manage/role', 'view.manage_role', NULL, NULL, 0, NULL, 'carbon:user-role', 1, 'route.manage_role', 0, 1, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900013', '3000343202867900010', '0,3000343202867900010,3000343202867900013', '用户管理', '用户管理', 1, 'manage_user', '/manage/user', 'view.manage_user', NULL, NULL, 0, NULL, 'ic:round-manage-accounts', 1, 'route.manage_user', 0, 1, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900020', '0', '0,3000343202867900020', '组织权限', '部门与岗位', 0, 'org', '/org', 'layout.base', NULL, NULL, 0, NULL, 'mdi:account-group-outline', 1, 'route.org', 0, 1, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900021', '3000343202867900020', '0,3000343202867900020,3000343202867900021', '部门管理', '部门管理', 1, 'org_dept', '/org/dept', 'view.org_dept', NULL, NULL, 0, NULL, 'mdi:sitemap-outline', 1, 'route.org_dept', 0, 1, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900022', '3000343202867900020', '0,3000343202867900020,3000343202867900022', '岗位管理', '岗位管理', 1, 'org_post', '/org/post', 'view.org_post', NULL, NULL, 0, NULL, 'mdi:briefcase-outline', 1, 'route.org_post', 0, 1, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900030', '0', '0,3000343202867900030', '系统设置', '系统信息 / 字典 / 公告 / 文件', 0, 'setting', '/setting', 'layout.base', NULL, NULL, 0, NULL, 'mdi:cog-outline', 1, 'route.setting', 0, 1, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900031', '3000343202867900030', '0,3000343202867900030,3000343202867900031', '数据字典', '数据字典', 1, 'setting_dict', '/setting/dict', 'view.setting_dict', NULL, NULL, 0, NULL, 'mdi:book-alphabet', 1, 'route.setting_dict', 0, 1, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900032', '3000343202867900030', '0,3000343202867900030,3000343202867900032', '系统信息', '系统名称 / Logo / 版权等品牌信息', 1, 'setting_config', '/setting/config', 'view.setting_config', NULL, NULL, 0, NULL, 'mdi:tune-variant', 1, 'route.setting_config', 0, 1, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900033', '3000343202867900030', '0,3000343202867900030,3000343202867900033', '公告管理', '公告管理', 1, 'setting_notice', '/setting/notice', 'view.setting_notice', NULL, NULL, 0, NULL, 'mdi:bullhorn-outline', 1, 'route.setting_notice', 0, 1, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900034', '3000343202867900030', '0,3000343202867900030,3000343202867900034', '公告控制台', '公告投递与阅读情况', 1, 'setting_notice-console', '/setting/notice-console', 'view.setting_notice-console', NULL, NULL, 0, NULL, 'mdi:chart-box-outline', 1, 'route.setting_notice-console', 0, 0, 'setting_notice', 4, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-25 21:53:23', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900035', '3000343202867900030', '0,3000343202867900030,3000343202867900035', '文件管理', '文件管理', 1, 'setting_file', '/setting/file', 'view.setting_file', NULL, NULL, 0, NULL, 'mdi:file-multiple-outline', 1, 'route.setting_file', 0, 1, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900040', '0', '0,3000343202867900040', '安全运维', '日志 / 任务 / 访问控制 / 在线用户', 0, 'ops', '/ops', 'layout.base', NULL, NULL, 0, NULL, 'mdi:shield-lock-outline', 1, 'route.ops', 0, 1, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900041', '3000343202867900040', '0,3000343202867900040,3000343202867900041', '行为日志', '操作日志', 1, 'ops_operate-log', '/ops/operate-log', 'view.ops_operate-log', NULL, NULL, 0, NULL, 'mdi:history', 1, 'route.ops_operate-log', 0, 1, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900042', '3000343202867900040', '0,3000343202867900040,3000343202867900042', '访问控制', '访问控制', 1, 'ops_filter', '/ops/filter', 'view.ops_filter', NULL, NULL, 0, NULL, 'mdi:filter-outline', 1, 'route.ops_filter', 0, 1, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900043', '3000343202867900040', '0,3000343202867900040,3000343202867900043', '定时任务', '定时任务', 1, 'ops_job', '/ops/job', 'view.ops_job', NULL, NULL, 0, NULL, 'mdi:clock-outline', 1, 'route.ops_job', 0, 1, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900044', '3000343202867900040', '0,3000343202867900040,3000343202867900044', '任务日志', '定时任务执行日志', 1, 'ops_job-log', '/ops/job-log', 'view.ops_job-log', NULL, NULL, 0, NULL, 'mdi:text-box-outline', 1, 'route.ops_job-log', 0, 0, 'ops_job', 4, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-24 23:32:16', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900045', '3000343202867900040', '0,3000343202867900040,3000343202867900045', '在线用户', '在线用户', 1, 'ops_online', '/ops/online', 'view.ops_online', NULL, NULL, 0, NULL, 'mdi:account-check-outline', 1, 'route.ops_online', 0, 1, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900050', '0', '0,3000343202867900050', '个人', '个人中心与公告（默认不进侧栏）', 0, 'account', '/account', 'layout.base', NULL, NULL, 0, NULL, 'ic:round-person', 1, 'route.account', 0, 0, NULL, 9, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900051', '3000343202867900050', '0,3000343202867900050,3000343202867900051', '个人中心', '个人资料', 1, 'account_info', '/account/info', 'view.account_info', NULL, NULL, 0, NULL, 'ic:round-person', 1, 'route.account_info', 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900052', '3000343202867900050', '0,3000343202867900050,3000343202867900052', '我的公告', '个人公告收件箱', 1, 'account_notice', '/account/notice', 'view.account_notice', NULL, NULL, 0, NULL, 'mdi:bell-outline', 1, 'route.account_notice', 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900060', '0', '0,3000343202867900060', '开发工具', '开发辅助', 0, 'dev', '/dev', 'layout.base', NULL, NULL, 0, NULL, 'mdi:tools', 1, 'route.dev', 0, 1, NULL, 90, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900061', '3000343202867900060', '0,3000343202867900060,3000343202867900061', '接口文档', 'SpringDoc OpenAPI', 1, 'dev_apidoc', '/dev/apidoc', 'view.dev_apidoc', NULL, NULL, 0, NULL, 'mdi:api', 1, 'route.dev_apidoc', 0, 1, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 15:06:51', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900100', '3000343202867900011', '0,3000343202867900010,3000343202867900011,3000343202867900100', '菜单添加', '菜单添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:menu:insert', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900101', '3000343202867900011', '0,3000343202867900010,3000343202867900011,3000343202867900101', '菜单删除', '菜单删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:menu:delete', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900102', '3000343202867900011', '0,3000343202867900010,3000343202867900011,3000343202867900102', '菜单修改', '菜单修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:menu:update', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900103', '3000343202867900011', '0,3000343202867900010,3000343202867900011,3000343202867900103', '菜单集合', '菜单集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:menu:select', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900104', '3000343202867900011', '0,3000343202867900010,3000343202867900011,3000343202867900104', '菜单状态修改', '菜单状态修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:menu:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900106', '3000343202867900012', '0,3000343202867900010,3000343202867900012,3000343202867900106', '角色添加', '角色添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:role:insert', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900107', '3000343202867900012', '0,3000343202867900010,3000343202867900012,3000343202867900107', '角色删除', '角色删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:role:delete', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900108', '3000343202867900012', '0,3000343202867900010,3000343202867900012,3000343202867900108', '角色修改', '角色修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:role:update', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900109', '3000343202867900012', '0,3000343202867900010,3000343202867900012,3000343202867900109', '角色集合', '角色集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:role:select', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900110', '3000343202867900012', '0,3000343202867900010,3000343202867900012,3000343202867900110', '角色授权', '角色授权', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:role:auth', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900111', '3000343202867900012', '0,3000343202867900010,3000343202867900012,3000343202867900111', '角色状态修改', '角色状态修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:role:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 6, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900113', '3000343202867900013', '0,3000343202867900010,3000343202867900013,3000343202867900113', '用户添加', '用户添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:insert', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900114', '3000343202867900013', '0,3000343202867900010,3000343202867900013,3000343202867900114', '用户删除', '用户删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:delete', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900115', '3000343202867900013', '0,3000343202867900010,3000343202867900013,3000343202867900115', '用户修改', '用户修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:update', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900116', '3000343202867900013', '0,3000343202867900010,3000343202867900013,3000343202867900116', '用户集合', '用户集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:select', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900117', '3000343202867900013', '0,3000343202867900010,3000343202867900013,3000343202867900117', '用户密码修改', '用户密码修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:updatePwd', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900118', '3000343202867900013', '0,3000343202867900010,3000343202867900013,3000343202867900118', '用户状态修改', '用户状态修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 6, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900119', '3000343202867900013', '0,3000343202867900010,3000343202867900013,3000343202867900119', '用户锁定', '用户锁定/解锁', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:updateLock', NULL, NULL, NULL, 0, 0, NULL, 7, 1, 'soybean', '2026-09-03 14:40:00', 'patch', '2026-09-26 17:13:17', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900120', '3000343202867900013', '0,3000343202867900010,3000343202867900013,3000343202867900120', '用户导出', '用户导出', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:export', NULL, NULL, NULL, 0, 0, NULL, 8, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900121', '3000343202867900013', '0,3000343202867900010,3000343202867900013,3000343202867900121', '用户权限详情', '用户权限详情', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:permDetail', NULL, NULL, NULL, 0, 0, NULL, 9, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900122', '3000343202867900042', '0,3000343202867900040,3000343202867900042,3000343202867900122', '安全配置', '安全配置', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:security', NULL, NULL, NULL, 0, 0, NULL, 6, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900123', '3000343202867900021', '0,3000343202867900020,3000343202867900021,3000343202867900123', '部门添加', '部门添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dept:insert', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900124', '3000343202867900021', '0,3000343202867900020,3000343202867900021,3000343202867900124', '部门删除', '部门删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dept:delete', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900125', '3000343202867900021', '0,3000343202867900020,3000343202867900021,3000343202867900125', '部门修改', '部门修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dept:update', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900126', '3000343202867900021', '0,3000343202867900020,3000343202867900021,3000343202867900126', '部门集合', '部门集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dept:select', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900127', '3000343202867900021', '0,3000343202867900020,3000343202867900021,3000343202867900127', '部门状态修改', '部门状态修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dept:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900129', '3000343202867900022', '0,3000343202867900020,3000343202867900022,3000343202867900129', '岗位添加', '岗位添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:post:insert', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900130', '3000343202867900022', '0,3000343202867900020,3000343202867900022,3000343202867900130', '岗位删除', '岗位删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:post:delete', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900131', '3000343202867900022', '0,3000343202867900020,3000343202867900022,3000343202867900131', '岗位修改', '岗位修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:post:update', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900132', '3000343202867900022', '0,3000343202867900020,3000343202867900022,3000343202867900132', '岗位集合', '岗位集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:post:select', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900133', '3000343202867900022', '0,3000343202867900020,3000343202867900022,3000343202867900133', '岗位状态修改', '岗位状态修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:post:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900134', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900134', '字典类型添加', '字典类型添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictType:insert', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900135', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900135', '字典类型删除', '字典类型删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictType:delete', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900136', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900136', '字典类型修改', '字典类型修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictType:update', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900137', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900137', '字典类型集合', '字典类型集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictType:select', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900138', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900138', '字典类型状态修改', '字典类型状态修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictType:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900139', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900139', '字典数据添加', '字典数据添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictData:insert', NULL, NULL, NULL, 0, 0, NULL, 6, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900140', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900140', '字典数据删除', '字典数据删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictData:delete', NULL, NULL, NULL, 0, 0, NULL, 7, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900141', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900141', '字典数据修改', '字典数据修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictData:update', NULL, NULL, NULL, 0, 0, NULL, 8, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900142', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900142', '字典数据集合', '字典数据集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictData:select', NULL, NULL, NULL, 0, 0, NULL, 9, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900143', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900143', '字典数据状态修改', '字典数据状态修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictData:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 10, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900144', '3000343202867900031', '0,3000343202867900030,3000343202867900031,3000343202867900144', '字典数据默认修改', '字典数据默认修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:dictData:updateDefault', NULL, NULL, NULL, 0, 0, NULL, 11, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900145', '3000343202867900032', '0,3000343202867900030,3000343202867900032,3000343202867900145', '编辑系统信息', '编辑 system 分组约束项', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:system', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900146', '3000343202867900032', '0,3000343202867900030,3000343202867900032,3000343202867900146', '配置维护', '配置维护（脚手架保留，默认停用）', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:maintain', NULL, NULL, NULL, 0, 0, NULL, 2, 0, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900147', '3000343202867900032', '0,3000343202867900030,3000343202867900032,3000343202867900147', '配置添加', '配置添加（脚手架保留，默认停用）', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:insert', NULL, NULL, NULL, 0, 0, NULL, 3, 0, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900148', '3000343202867900032', '0,3000343202867900030,3000343202867900032,3000343202867900148', '配置删除', '配置删除（脚手架保留，默认停用）', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:delete', NULL, NULL, NULL, 0, 0, NULL, 4, 0, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900149', '3000343202867900032', '0,3000343202867900030,3000343202867900032,3000343202867900149', '配置修改', '配置修改（脚手架保留，默认停用）', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:update', NULL, NULL, NULL, 0, 0, NULL, 5, 0, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900150', '3000343202867900032', '0,3000343202867900030,3000343202867900032,3000343202867900150', '配置状态修改', '配置状态修改（脚手架保留，默认停用）', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 6, 0, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900151', '3000343202867900033', '0,3000343202867900030,3000343202867900033,3000343202867900151', '公告添加', '公告添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:notice:insert', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900152', '3000343202867900033', '0,3000343202867900030,3000343202867900033,3000343202867900152', '公告删除', '公告删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:notice:delete', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900153', '3000343202867900033', '0,3000343202867900030,3000343202867900033,3000343202867900153', '公告修改', '公告修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:notice:update', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900154', '3000343202867900033', '0,3000343202867900030,3000343202867900033,3000343202867900154', '公告集合', '公告集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:notice:select', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900155', '3000343202867900033', '0,3000343202867900030,3000343202867900033,3000343202867900155', '公告发布状态修改', '公告发布状态修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:notice:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900156', '3000343202867900033', '0,3000343202867900030,3000343202867900033,3000343202867900156', '公告配置', '公告配置', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:notice', NULL, NULL, NULL, 0, 0, NULL, 6, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900157', '3000343202867900035', '0,3000343202867900030,3000343202867900035,3000343202867900157', '文件集合', '文件集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:file:select', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900158', '3000343202867900035', '0,3000343202867900030,3000343202867900035,3000343202867900158', '目录树', '目录树', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:file:tree', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900159', '3000343202867900035', '0,3000343202867900030,3000343202867900035,3000343202867900159', '目录添加', '目录添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:file:folderInsert', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900160', '3000343202867900035', '0,3000343202867900030,3000343202867900035,3000343202867900160', '目录修改', '目录修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:file:folderUpdate', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900161', '3000343202867900035', '0,3000343202867900030,3000343202867900035,3000343202867900161', '目录删除', '目录删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:file:folderDelete', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900162', '3000343202867900035', '0,3000343202867900030,3000343202867900035,3000343202867900162', '文件修改', '文件修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:file:update', NULL, NULL, NULL, 0, 0, NULL, 6, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900163', '3000343202867900035', '0,3000343202867900030,3000343202867900035,3000343202867900163', '文件删除', '文件删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:file:delete', NULL, NULL, NULL, 0, 0, NULL, 7, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900164', '3000343202867900035', '0,3000343202867900030,3000343202867900035,3000343202867900164', '上传配置', '上传配置', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:upload', NULL, NULL, NULL, 0, 0, NULL, 8, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900165', '3000343202867900041', '0,3000343202867900040,3000343202867900041,3000343202867900165', '日志集合', '日志集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:log:select', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900166', '3000343202867900041', '0,3000343202867900040,3000343202867900041,3000343202867900166', '日志配置', '日志配置', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:log', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900167', '3000343202867900042', '0,3000343202867900040,3000343202867900042,3000343202867900167', '访问控制集合', '访问控制集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:filter:select', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900168', '3000343202867900042', '0,3000343202867900040,3000343202867900042,3000343202867900168', '访问控制添加', '访问控制添加', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:filter:insert', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900169', '3000343202867900042', '0,3000343202867900040,3000343202867900042,3000343202867900169', '访问控制修改', '访问控制修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:filter:update', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900170', '3000343202867900042', '0,3000343202867900040,3000343202867900042,3000343202867900170', '访问控制删除', '访问控制删除', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:filter:delete', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900171', '3000343202867900042', '0,3000343202867900040,3000343202867900042,3000343202867900171', '访问控制状态修改', '访问控制状态修改', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:filter:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900172', '3000343202867900043', '0,3000343202867900040,3000343202867900043,3000343202867900172', '任务集合', '任务集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:job:select', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900173', '3000343202867900043', '0,3000343202867900040,3000343202867900043,3000343202867900173', '任务启停', '任务启停', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:job:updateEnabled', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900174', '3000343202867900043', '0,3000343202867900040,3000343202867900043,3000343202867900174', '手动执行', '手动执行', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:job:run', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900175', '3000343202867900043', '0,3000343202867900040,3000343202867900043,3000343202867900175', '任务配置', '任务配置', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:config:job', NULL, NULL, NULL, 0, 0, NULL, 4, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900176', '3000343202867900045', '0,3000343202867900040,3000343202867900045,3000343202867900176', '在线用户集合', '在线用户集合', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:online:select', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900177', '3000343202867900045', '0,3000343202867900040,3000343202867900045,3000343202867900177', '在线用户强退', '在线用户强退', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:online:kick', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'soybean', '2026-09-03 14:40:00', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900178', '3000343202867900043', '0,3000343202867900040,3000343202867900043,3000343202867900178', '查看日志', '查看任务执行日志', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:job:log', NULL, NULL, NULL, 0, 0, NULL, 5, 1, 'patch', '2026-09-24 23:32:16', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900179', '3000343202867900033', '0,3000343202867900030,3000343202867900033,3000343202867900179', '公告控制台', '查看公告投递与阅读情况', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:notice:console', NULL, NULL, NULL, 0, 0, NULL, 7, 1, 'patch', '2026-09-25 21:54:33', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900180', '3000343202867900001', '0,3000343202867900001,3000343202867900180', '用户信息查询', '首页工作台调用个人资料相关接口', 2, NULL, NULL, NULL, NULL, NULL, 0, 'user:info:select', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'patch', '2026-09-26 15:06:51', 'patch', '2026-09-26 16:41:14', 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900181', '3000343202867900051', '0,3000343202867900050,3000343202867900051,3000343202867900181', '个人中心查询', '个人资料与改密等接口', 2, NULL, NULL, NULL, NULL, NULL, 0, 'user:info:select', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'patch', '2026-09-26 15:06:51', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900182', '3000343202867900052', '0,3000343202867900050,3000343202867900052,3000343202867900182', '我的公告查询', '个人公告收件箱与已读', 2, NULL, NULL, NULL, NULL, NULL, 0, 'user:notice:select', NULL, NULL, NULL, 0, 0, NULL, 1, 1, 'patch', '2026-09-26 15:06:51', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900183', '3000343202867900001', '0,3000343202867900001,3000343202867900183', '公告信息查询', '首页工作台调用个人公告相关接口', 2, NULL, NULL, NULL, NULL, NULL, 0, 'user:notice:select', NULL, NULL, NULL, 0, 0, NULL, 2, 1, 'patch', '2026-09-26 16:42:39', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900184', '3000343202867900033', '0,3000343202867900030,3000343202867900033,3000343202867900184', '用户检索', '公告指定接收人远程检索用户', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:search', NULL, NULL, NULL, 0, 0, NULL, 8, 1, 'patch', '2026-09-26 17:52:24', '', NULL, 0);
INSERT INTO `sys_menu` VALUES ('3000343202867900185', '3000343202867900045', '0,3000343202867900040,3000343202867900045,3000343202867900185', '用户检索', '在线用户筛选远程检索用户', 2, NULL, NULL, NULL, NULL, NULL, 0, 'system:user:search', NULL, NULL, NULL, 0, 0, NULL, 3, 1, 'patch', '2026-09-26 17:52:24', '', NULL, 0);

-- ----------------------------
-- Table structure for sys_menu_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_role`;
CREATE TABLE `sys_menu_role`  (
  `menu_role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色菜单关联ID（雪花）',
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色ID',
  `menu_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`menu_role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu_role
-- ----------------------------
INSERT INTO `sys_menu_role` VALUES ('2103784952528224257', '488243256161730560', '3000343202867900001', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952536612865', '488243256161730560', '3000343202867900180', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952545001474', '488243256161730560', '3000343202867900183', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952549195777', '488243256161730560', '3000343202867900010', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952557584385', '488243256161730560', '3000343202867900011', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952570167297', '488243256161730560', '3000343202867900100', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952578555905', '488243256161730560', '3000343202867900101', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952582750209', '488243256161730560', '3000343202867900102', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952591138817', '488243256161730560', '3000343202867900103', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952599527426', '488243256161730560', '3000343202867900104', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952607916034', '488243256161730560', '3000343202867900012', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952616304642', '488243256161730560', '3000343202867900106', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952620498945', '488243256161730560', '3000343202867900107', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952633081858', '488243256161730560', '3000343202867900108', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952637276162', '488243256161730560', '3000343202867900109', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952645664769', '488243256161730560', '3000343202867900110', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952654053378', '488243256161730560', '3000343202867900111', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952666636289', '488243256161730560', '3000343202867900013', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952666636290', '488243256161730560', '3000343202867900113', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952679219201', '488243256161730560', '3000343202867900114', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952683413506', '488243256161730560', '3000343202867900115', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952695996417', '488243256161730560', '3000343202867900116', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952704385025', '488243256161730560', '3000343202867900117', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952704385026', '488243256161730560', '3000343202867900118', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952716967938', '488243256161730560', '3000343202867900119', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952725356546', '488243256161730560', '3000343202867900120', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952729550850', '488243256161730560', '3000343202867900121', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952742133761', '488243256161730560', '3000343202867900020', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952742133762', '488243256161730560', '3000343202867900021', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952754716674', '488243256161730560', '3000343202867900123', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952763105281', '488243256161730560', '3000343202867900124', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952767299586', '488243256161730560', '3000343202867900125', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952779882498', '488243256161730560', '3000343202867900126', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952788271105', '488243256161730560', '3000343202867900127', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952796659713', '488243256161730560', '3000343202867900022', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952805048322', '488243256161730560', '3000343202867900129', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952813436929', '488243256161730560', '3000343202867900130', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952821825538', '488243256161730560', '3000343202867900131', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952830214145', '488243256161730560', '3000343202867900132', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952838602754', '488243256161730560', '3000343202867900133', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952855379969', '488243256161730560', '3000343202867900030', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952859574274', '488243256161730560', '3000343202867900031', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952872157185', '488243256161730560', '3000343202867900134', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952880545793', '488243256161730560', '3000343202867900135', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952884740098', '488243256161730560', '3000343202867900136', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952897323009', '488243256161730560', '3000343202867900137', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952901517314', '488243256161730560', '3000343202867900138', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952905711617', '488243256161730560', '3000343202867900139', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952918294530', '488243256161730560', '3000343202867900140', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952918294531', '488243256161730560', '3000343202867900141', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952930877442', '488243256161730560', '3000343202867900142', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952939266049', '488243256161730560', '3000343202867900143', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952947654658', '488243256161730560', '3000343202867900144', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952947654659', '488243256161730560', '3000343202867900032', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952956043266', '488243256161730560', '3000343202867900145', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952964431874', '488243256161730560', '3000343202867900146', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952972820482', '488243256161730560', '3000343202867900147', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952989597698', '488243256161730560', '3000343202867900148', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784952993792001', '488243256161730560', '3000343202867900149', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953002180610', '488243256161730560', '3000343202867900150', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953010569217', '488243256161730560', '3000343202867900033', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953014763521', '488243256161730560', '3000343202867900151', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953023152130', '488243256161730560', '3000343202867900152', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953031540737', '488243256161730560', '3000343202867900153', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953039929346', '488243256161730560', '3000343202867900154', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953044123650', '488243256161730560', '3000343202867900155', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953052512257', '488243256161730560', '3000343202867900156', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953060900865', '488243256161730560', '3000343202867900179', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953065095169', '488243256161730560', '3000343202867900184', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953073483777', '488243256161730560', '3000343202867900034', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953077678082', '488243256161730560', '3000343202867900035', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953086066689', '488243256161730560', '3000343202867900157', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953094455298', '488243256161730560', '3000343202867900158', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953102843905', '488243256161730560', '3000343202867900159', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953111232513', '488243256161730560', '3000343202867900160', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953115426818', '488243256161730560', '3000343202867900161', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953123815426', '488243256161730560', '3000343202867900162', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953132204033', '488243256161730560', '3000343202867900163', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953140592642', '488243256161730560', '3000343202867900164', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953148981249', '488243256161730560', '3000343202867900040', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953148981250', '488243256161730560', '3000343202867900041', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953161564162', '488243256161730560', '3000343202867900165', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953161564163', '488243256161730560', '3000343202867900166', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953178341378', '488243256161730560', '3000343202867900042', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953178341379', '488243256161730560', '3000343202867900122', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953186729985', '488243256161730560', '3000343202867900167', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953195118593', '488243256161730560', '3000343202867900168', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953203507202', '488243256161730560', '3000343202867900169', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953211895810', '488243256161730560', '3000343202867900170', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953220284417', '488243256161730560', '3000343202867900171', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953228673026', '488243256161730560', '3000343202867900043', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953232867330', '488243256161730560', '3000343202867900172', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953241255938', '488243256161730560', '3000343202867900173', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953241255939', '488243256161730560', '3000343202867900174', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953253838850', '488243256161730560', '3000343202867900175', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953258033154', '488243256161730560', '3000343202867900178', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953262227457', '488243256161730560', '3000343202867900044', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953270616066', '488243256161730560', '3000343202867900045', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953279004673', '488243256161730560', '3000343202867900176', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953287393282', '488243256161730560', '3000343202867900177', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953295781890', '488243256161730560', '3000343202867900185', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953304170498', '488243256161730560', '3000343202867900050', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953304170499', '488243256161730560', '3000343202867900051', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953316753409', '488243256161730560', '3000343202867900181', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953325142017', '488243256161730560', '3000343202867900052', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953333530625', '488243256161730560', '3000343202867900182', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953337724930', '488243256161730560', '3000343202867900060', '1662038524471218177', '2026-09-26 17:53:14');
INSERT INTO `sys_menu_role` VALUES ('2103784953346113537', '488243256161730560', '3000343202867900061', '1662038524471218177', '2026-09-26 17:53:14');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知ID（雪花）',
  `notice_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知标题',
  `notice_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知内容（富文本/Markdown）',
  `notice_type` tinyint NOT NULL DEFAULT 1 COMMENT '通知类型：1-系统通知，2-运营活动，3-平台公告，4-用户私信',
  `receiver_type` tinyint NOT NULL DEFAULT 1 COMMENT '接收者类型：1-全体用户，2-指定角色，3-指定部门，4-指定个人',
  `receiver_ids` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接收目标ID，逗号分隔（角色/部门/用户）',
  `notice_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公告说明',
  `is_send` tinyint(1) NOT NULL DEFAULT 0 COMMENT '发布状态：0-草稿，1-已发布',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发布时间（已发布时写入）',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间（到期后不再展示）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-存在，1-删除',
  PRIMARY KEY (`notice_id`) USING BTREE,
  INDEX `idx_notice_type`(`notice_type` ASC) USING BTREE,
  INDEX `idx_notice_expire`(`expire_time` ASC) USING BTREE,
  INDEX `idx_notice_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_notice_send`(`is_send` ASC, `order_num` ASC, `send_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES ('1225060037265854464', '测试全体用户', '测试全体用户', 1, 1, NULL, '测试全体用户', 1, 0, '2026-06-11 14:38:04', NULL, '', '2026-06-11 14:38:04', '', '2026-06-11 22:38:03', 0);

-- ----------------------------
-- Table structure for sys_notice_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_user`;
CREATE TABLE `sys_notice_user`  (
  `user_notice_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户通知ID（雪花）',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `notice_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知ID',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-存在，1-删除（用户侧隐藏）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（投递时间）',
  PRIMARY KEY (`user_notice_id`) USING BTREE,
  UNIQUE INDEX `uk_user_notice`(`user_id` ASC, `notice_id` ASC) USING BTREE,
  INDEX `idx_user_inbox`(`user_id` ASC, `is_del` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_notice_id`(`notice_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户通知已读状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice_user
-- ----------------------------
INSERT INTO `sys_notice_user` VALUES ('1225060037282631680', '1220945860838428672', '1225060037265854464', 0, NULL, 0, '2026-06-11 22:38:03');
INSERT INTO `sys_notice_user` VALUES ('1225060037286825984', '1221663362941849600', '1225060037265854464', 0, NULL, 0, '2026-06-11 22:38:03');
INSERT INTO `sys_notice_user` VALUES ('1225060037291020288', '1662038524471218177', '1225060037265854464', 1, '2026-06-18 16:18:18', 0, '2026-06-11 22:38:03');

-- ----------------------------
-- Table structure for sys_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operate_log`;
CREATE TABLE `sys_operate_log`  (
  `operate_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作日志主键ID',
  `log_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志类型：LOGIN/OPERATE',
  `business_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务类型：QUERY/ADD/UPDATE/DELETE/LOGIN/LOGOUT/OTHER',
  `operate_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作模块标题',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'HTTP请求方式',
  `operate_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '后端类方法名',
  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求接口路径',
  `request_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求URL参数',
  `request_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求体内容',
  `response_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '响应体内容',
  `is_success` tinyint(1) NULL DEFAULT NULL COMMENT '是否执行成功：1成功，0失败',
  `status_code` int NULL DEFAULT NULL COMMENT 'HTTP响应状态码',
  `error_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '异常类名',
  `error_msg` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误简要信息',
  `error_stack` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误堆栈详情',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人员用户ID',
  `operate_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人员姓名快照',
  `operate_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人员IP地址',
  `server_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务端IP地址',
  `user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户浏览器UA串',
  `browser` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器类型',
  `system_os` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求链路ID',
  `cost_time` bigint NULL DEFAULT NULL COMMENT '操作耗时(毫秒)',
  `operate_time` datetime NULL DEFAULT NULL COMMENT '操作发生时间',
  PRIMARY KEY (`operate_id`) USING BTREE,
  INDEX `idx_operate_time`(`operate_time` ASC) USING BTREE,
  INDEX `idx_business_type`(`business_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_success`(`is_success` ASC) USING BTREE,
  INDEX `idx_request_uri`(`request_uri` ASC) USING BTREE,
  INDEX `idx_log_type`(`log_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_operate_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位ID（雪花）',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '岗位编码',
  `post_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `post_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1管理岗 2技术岗 3运营岗 4市场岗',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (`post_id`) USING BTREE,
  UNIQUE INDEX `uk_post_code`(`post_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_post_name`(`post_name` ASC) USING BTREE,
  INDEX `idx_post_enabled_order`(`is_enabled` ASC, `order_num` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES ('410792368778907648', 'POST_78907648', '总经理', 1, 1, 1, 'migrate', '2026-05-29 23:28:04', '1662038524471218177', '2026-06-20 16:25:02', 0);
INSERT INTO `sys_post` VALUES ('410792443127140352', 'POST_27140352', '技术经理', 2, 2, 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_post` VALUES ('410792478929719296', 'POST_29719296', '人事经理', 1, 3, 1, 'migrate', '2026-05-29 23:28:04', '', NULL, 0);
INSERT INTO `sys_post` VALUES ('411477874382606336', 'POST_82606336', '员工', 2, 4, 1, 'migrate', '2026-05-29 23:28:04', '1662038524471218177', '2026-06-09 01:46:45', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID（雪花）',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_name`(`role_name` ASC) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('488243256161730560', '管理员', 'admin', 1, 1, '', '2026-06-18 23:41:36', '1662038524471218177', '2026-06-20 16:24:33', 0);

-- ----------------------------
-- Table structure for sys_role_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_user`;
CREATE TABLE `sys_role_user`  (
  `role_user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户角色ID（雪花）',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_user_id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_role_user_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_user
-- ----------------------------
INSERT INTO `sys_role_user` VALUES ('1220945860842622976', '1220945860838428672', '488243256161730560', '', '2026-06-18 23:41:36');
INSERT INTO `sys_role_user` VALUES ('2068369327669002242', '1221663362941849600', '488243256161730560', '1662038524471218177', '2026-06-20 16:24:11');
INSERT INTO `sys_role_user` VALUES ('495571139645542400', '1662038524471218177', '488243256161730560', '', '2026-06-18 23:41:36');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID（雪花）',
  `user_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `user_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户密码（bcrypt cost=12）',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `user_sex` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别：0-男，1-女',
  `user_email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `user_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `dept_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门id',
  `post_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位id',
  `user_avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像访问地址，如 /upload/{fileId}',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用（1启用 0停用）',
  `is_lock` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否锁定（1锁定 0正常）；与启停独立，登录失败满 5 次自动锁定',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0存在 1删除',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_user_account`(`user_account` ASC) USING BTREE,
  INDEX `idx_user_dept_id`(`dept_id` ASC) USING BTREE,
  INDEX `idx_user_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_enabled`(`is_del` ASC, `is_enabled` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1220945860838428672', 'test01', '$2a$12$Nl6syrPkjvm/7uZE56XiRuPUky5L2waDu9D9wvhKZfvNeRLelG01m', '测试', '1', '111@111.com', '19199999999', '1', '410792368778907648', NULL, 1, 0, '', '2026-05-31 06:09:48', '1662038524471218177', '2026-07-19 18:13:19', 0);
INSERT INTO `sys_user` VALUES ('1221663362941849600', 'test02', '$2a$12$Ju3T7jpxXmQMOJFD9bsXTeJ63J4V/MqDtq/4.KDrpdGpBqgTrkx9O', '测试02', '1', 'sga@qq.com', '13199998888', '3', '410792443127140352', NULL, 1, 1, '', '2026-06-02 05:40:53', '1662038524471218177', '2026-09-23 15:29:35', 0);
INSERT INTO `sys_user` VALUES ('1662038524471218177', 'admin', '$2a$12$Nl6syrPkjvm/7uZE56XiRuPUky5L2waDu9D9wvhKZfvNeRLelG01m', '超级管理员', '0', 'admin', '19199999999', '2', '410792368778907648', NULL, 1, 0, NULL, '2023-05-26 10:10:38', NULL, '2026-05-31 06:14:03', 0);

SET FOREIGN_KEY_CHECKS = 1;
