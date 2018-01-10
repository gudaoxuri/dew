/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost:3306
 Source Schema         : ds_yaml_0

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : 65001

 Date: 03/10/2017 21:27:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_order_0
-- ----------------------------
DROP TABLE IF EXISTS `t_order_0`;
CREATE TABLE `t_order_0` (
  `id` varchar(32) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_order_0
-- ----------------------------
BEGIN;
INSERT INTO `t_order_0` VALUES ('0b8dffab8e86433281330c5f04716ae6', 1004, 10, 'INIT');
INSERT INTO `t_order_0` VALUES ('94894e7c987344a88174c5ef29cbb39f', 1006, 10, 'INIT');
INSERT INTO `t_order_0` VALUES ('da715f1c1df14c81b76f6a16c0b4a1c7', 1002, 10, 'INIT');
INSERT INTO `t_order_0` VALUES ('e9b76669bdf4453a80390b324230ad6a', 1000, 10, 'INIT');
INSERT INTO `t_order_0` VALUES ('f1a668bd9fd14b6ca05820cd81f3e130', 1008, 10, 'INIT');
COMMIT;

-- ----------------------------
-- Table structure for t_order_1
-- ----------------------------
DROP TABLE IF EXISTS `t_order_1`;
CREATE TABLE `t_order_1` (
  `id` varchar(32) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_order_1
-- ----------------------------
BEGIN;
INSERT INTO `t_order_1` VALUES ('2597974949824183a48513b29417a391', 1007, 10, 'INIT');
INSERT INTO `t_order_1` VALUES ('2a24fbe6a4874055b7d7f054d64530de', 1003, 10, 'INIT');
INSERT INTO `t_order_1` VALUES ('4f2c4ee405af461bb1399677f4e55d53', 1009, 10, 'INIT');
INSERT INTO `t_order_1` VALUES ('5223e78e905f4083946bbbaba558f92f', 1005, 10, 'INIT');
INSERT INTO `t_order_1` VALUES ('f59d583db92548d4b66faf4e76e0835a', 1001, 10, 'INIT');
COMMIT;

-- ----------------------------
-- Table structure for t_order_item_0
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item_0`;
CREATE TABLE `t_order_item_0` (
  `id` varchar(32) NOT NULL,
  `item_id` bigint(20) NOT NULL,
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_order_item_0
-- ----------------------------
BEGIN;
INSERT INTO `t_order_item_0` VALUES ('2ba3625a808849918af274197f2b31f3', 100001, 1000, 10);
INSERT INTO `t_order_item_0` VALUES ('2f9dde4ca6e541d3940a2952bfc9dd25', 100801, 1008, 10);
INSERT INTO `t_order_item_0` VALUES ('54abf463124e4b8fb1c2fbe7699744a2', 100201, 1002, 10);
INSERT INTO `t_order_item_0` VALUES ('71ad8bf6430f47dc9533079231367bfb', 100401, 1004, 10);
INSERT INTO `t_order_item_0` VALUES ('eef46142b96846e6b7a2ef721c964ef1', 100601, 1006, 10);
COMMIT;

-- ----------------------------
-- Table structure for t_order_item_1
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item_1`;
CREATE TABLE `t_order_item_1` (
  `id` varchar(32) NOT NULL,
  `item_id` bigint(20) NOT NULL,
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_order_item_1
-- ----------------------------
BEGIN;
INSERT INTO `t_order_item_1` VALUES ('465b9c5986274f14a6ce080de2321a1c', 100501, 1005, 10);
INSERT INTO `t_order_item_1` VALUES ('50ec5c889d2f4e42a86afdc15d6bbea8', 100101, 1001, 10);
INSERT INTO `t_order_item_1` VALUES ('b2f4f942555f4471af07fb1f01f2e87e', 100701, 1007, 10);
INSERT INTO `t_order_item_1` VALUES ('bcc567fefc874d7095235ec1e2f1d6d1', 100901, 1009, 10);
INSERT INTO `t_order_item_1` VALUES ('ce0b02e9e1ad45d1955e87649651abef', 100301, 1003, 10);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
