/*
duckula的初始化脚本
 Date: 18/12/2020 17:49:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE `duckula` CHARACTER SET 'utf8' COLLATE 'utf8_bin';
USE duckula;
-- ----------------------------
-- Table structure for comback_task
-- ----------------------------
DROP TABLE IF EXISTS `comback_task`;
CREATE TABLE `comback_task`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '监听名称',
  `buffer_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT 'threadpoolmuli' COMMENT '缓冲类型，disruptor,threadpoolmuli',
  `send_num` int(11) NULL DEFAULT 3 COMMENT '发送线程数，默认为3',
  `rule` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '规则',
  `version` bigint(255) NULL DEFAULT NULL COMMENT 'duckula监听的版本',
  `deploy_id` bigint(20) NULL DEFAULT NULL COMMENT '部署ID配置',
  `user_id` bigint(11) NULL DEFAULT NULL COMMENT '用户id',
  `middleware_id` bigint(20) NULL DEFAULT NULL COMMENT '目的中间件配置',
  `instance_id` bigint(20) NULL DEFAULT NULL COMMENT '需要监听的实例配置',
  `checkpoint` bigint(255) NULL DEFAULT NULL COMMENT '检查点',
  `group_id` int(11) NULL DEFAULT NULL COMMENT '组id，用于分布式锁，checkpoint mysql启作用',
  `client_id` int(11) NULL DEFAULT NULL COMMENT '连接mysql的客户端，最初由id生成',
  `ha_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'cur表示从当前最新位点启动，last表示从记录的最后位点启动，pos表示从上面设置的gtids启动',
  `gtids` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '开始的gtid值。',
  `attr_config` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '附加的配置，如自动创建ES索引',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for common_checkpoint
-- ----------------------------
DROP TABLE IF EXISTS `common_checkpoint`;
CREATE TABLE `common_checkpoint`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'checkpointName',
  `checkpoint_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '检查点类型',
  `host` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '检查点主机',
  `port` int(255) NULL DEFAULT 3306 COMMENT '端口',
  `default_db` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '检查点使用的数据库，不填为tams',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '登陆用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '登陆密码',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '所属用户',
  `com_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '所属公司',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1323948699531472899 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for common_consumer
-- ----------------------------
DROP TABLE IF EXISTS `common_consumer`;
CREATE TABLE `common_consumer`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'consumer名称',
  `topic` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '监听的topic，只能监听一个',
  `rule` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '规则',
  `version_id` bigint(255) NULL DEFAULT NULL COMMENT 'duckula监听的版本',
  `deploy_id` bigint(20) NULL DEFAULT NULL COMMENT '部署ID配置',
  `user_id` bigint(11) NULL DEFAULT NULL COMMENT '用户id',
  `middleware_kafka_id` bigint(20) NULL DEFAULT NULL COMMENT '要监听的kafka配置',
  `middleware_id` bigint(20) NULL DEFAULT NULL COMMENT '目的中间件配置',
  `instance_id` bigint(20) NULL DEFAULT NULL COMMENT '幂等时的反查实例',
  `group_id` int(11) NULL DEFAULT NULL COMMENT '组id,监听组',
  `attr_config` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '附加的配置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1330357299376095233 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for common_deploy
-- ----------------------------
DROP TABLE IF EXISTS `common_deploy`;
CREATE TABLE `common_deploy`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `deploy` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '部署类型:  k8s/docker/host',
  `env` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '环境：开发/测试/预发/演示/生产',
  `namespace` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '名称空间',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `token` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `config` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'config配置文件',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT 'user表用户id',
  `host` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '用户名，deploy为host才启作用',
  `port` int(255) NULL DEFAULT 22 COMMENT 'SSH端口',
  `pwd_duckula` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'duckula启动程序用密码，不保留root密码',
  `hostsconfig` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '需要配置的config,对于\"host\"类型启作用',
  `is_init` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '是否初始化，yes:已初始化  no:未初始化 创建duckula用户，创建环境变量创建目录',
  `docker_login` varchar(1000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'docker类型需要的登陆脚本',
  `is_default` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '是否默认值，yes：是默认某个用户环境的部署机器  no：不是',
  `version_id` bigint(255) NULL DEFAULT NULL COMMENT '当前的版本,host和docker模式需要升级',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1339543268893474819 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '部署相关配置，默认为：k8s环境配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for common_dump
-- ----------------------------
DROP TABLE IF EXISTS `common_dump`;
CREATE TABLE `common_dump`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '监听名称',
  `rule` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '规则',
  `version_id` bigint(255) NULL DEFAULT NULL COMMENT 'duckula监听的版本',
  `deploy_id` bigint(20) NULL DEFAULT NULL COMMENT '部署ID配置',
  `user_id` bigint(11) NULL DEFAULT NULL COMMENT '用户id',
  `middleware_id` bigint(20) NULL DEFAULT NULL COMMENT '目的中间件配置',
  `instance_id` bigint(20) NULL DEFAULT NULL COMMENT '需要监听的实例配置',
  `attr_config` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '附加的配置，如自动创建ES索引',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1328513017153392642 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for common_instance
-- ----------------------------
DROP TABLE IF EXISTS `common_instance`;
CREATE TABLE `common_instance`  (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数据库实例名',
  `host` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `port` int(255) NULL DEFAULT 3306 COMMENT '端口',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `is_drds` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT 'false' COMMENT 'yes:是no:不是',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '所属用户',
  `com_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '所属公司',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for common_middleware
-- ----------------------------
DROP TABLE IF EXISTS `common_middleware`;
CREATE TABLE `common_middleware`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '名称',
  `middleware_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '配置类型：ES/Mysql/Kafka',
  `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '中间件版本',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '所属用户',
  `com_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '所属公司',
  `host` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '主机，逗号隔开',
  `port` int(11) NULL DEFAULT NULL COMMENT '端口，逗号隔开',
  `port2` int(11) NULL DEFAULT NULL COMMENT '端口2，如es需要2个端口',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '密码',
  `hostsconfig` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '配置到主机上的hosts，如kafka配置为hosts注册node时',
  `opt` varchar(8000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '选项，json类型',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1328693206999187459 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for common_task
-- ----------------------------
DROP TABLE IF EXISTS `common_task`;
CREATE TABLE `common_task`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '监听名称',
  `buffer_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT 'threadpoolmuli' COMMENT '缓冲类型，disruptor,threadpoolmuli',
  `send_num` int(11) NULL DEFAULT 3 COMMENT '发送线程数，默认为3',
  `rule` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '规则',
  `version_id` bigint(255) NULL DEFAULT NULL COMMENT 'duckula监听的版本',
  `deploy_id` bigint(20) NULL DEFAULT NULL COMMENT '部署ID配置',
  `user_id` bigint(11) NULL DEFAULT NULL COMMENT '用户id',
  `middleware_id` bigint(20) NULL DEFAULT NULL COMMENT '目的中间件配置',
  `instance_id` bigint(20) NULL DEFAULT NULL COMMENT '需要监听的实例配置',
  `checkpoint_id` bigint(255) NULL DEFAULT NULL COMMENT '检查点',
  `group_id` int(11) NULL DEFAULT NULL COMMENT '组id，用于分布式锁，checkpoint mysql启作用',
  `client_id` int(11) NULL DEFAULT NULL COMMENT '连接mysql的客户端，最初由id生成',
  `ha_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'cur表示从当前最新位点启动，last表示从记录的最后位点启动，pos表示从上面设置的gtids启动',
  `gtids` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '开始的gtid值。',
  `attr_config` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '附加的配置，如自动创建ES索引',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1328624577251176450 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for common_version
-- ----------------------------
DROP TABLE IF EXISTS `common_version`;
CREATE TABLE `common_version`  (
  `id` bigint(11) NOT NULL,
  `main_version` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '主程序版本',
  `main_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '主程序存储路径',
  `data_version` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数据版本',
  `data_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数据存储路径',
  `image_group` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '镜像库的组，如果没有配置就使用阿里云的镜像组',
  `author` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '作者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '升级时间',
  `readme` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '更新说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_global
-- ----------------------------
DROP TABLE IF EXISTS `sys_global`;
CREATE TABLE `sys_global`  (
  `id` bigint(20) NOT NULL,
  `config_globle` varchar(5000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '分组',
  `last_username` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '验证类型',
  `last_updatetime` datetime(0) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '全局对象' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_permissions
-- ----------------------------
DROP TABLE IF EXISTS `sys_permissions`;
CREATE TABLE `sys_permissions`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限编号',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '权限名',
  `opt_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '操作类型，data:数据，url:功能',
  `data_pattern` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数据权限有的模式：all,like,equal,expression',
  `data_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数据权限的值，跟模式搭配使用',
  `url_pattern` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '功能权限的模式：*:全部地址  pre:前缀模式，regular：正则表达式',
  `url_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '功能权限用的值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '角色名',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '登陆ID',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'username',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `salt` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '盐',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
  `com_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司名称',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统用户' ROW_FORMAT = Dynamic;

INSERT INTO `common_version`(`id`, `main_version`, `main_path`, `data_version`, `data_path`, `image_group`, `author`, `update_time`, `readme`) VALUES (1, '3.0.0', 'home:/', '3.0.0', 'home:/', 'registry.cn-hangzhou.aliyuncs.com/rjzjh/duckula', 'Andy', '2020-12-24 00:00:00', '第一个版本。。。。。。');
INSERT INTO `common_version`(`id`, `main_version`, `main_path`, `data_version`, `data_path`, `image_group`, `author`, `update_time`, `readme`) VALUES (2, '3.0.1', 'home:/', '3.0.1', 'home:/', 'registry.cn-hangzhou.aliyuncs.com/rjzjh/duckula', 'Andy', '2020-12-31 00:00:00', '增加过滤重要功能');
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', 'YzcmCZNvbXocrsz9dm8e', 'hulang6666@qq.com', '12345671123', 1, '太美医疗科技', '2016-11-11 11:11:11', '2016-11-11 11:11:11');
SET FOREIGN_KEY_CHECKS = 1;
