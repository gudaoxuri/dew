/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost:3306
 Source Schema         : ds_yaml_1

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : 65001

 Date: 08/10/2017 16:44:06
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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_order_0
-- ----------------------------
BEGIN;
INSERT INTO `t_order_0` VALUES ('7de948bf613a48ae9b9685125296ce57', 1108, 11, 'INIT');
INSERT INTO `t_order_0` VALUES ('97ccb2f6894d4324967448b681029fe2', 1100, 11, 'INIT');
INSERT INTO `t_order_0` VALUES ('998657f4270d425598fb9fecc5de578c', 1104, 11, 'INIT');
INSERT INTO `t_order_0` VALUES ('b2778efb69554a06809f856cd40b100a', 1106, 11, 'INIT');
INSERT INTO `t_order_0` VALUES ('ed5871ff7d794116915cd26f50b76c8e', 1102, 11, 'INIT');
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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_order_1
-- ----------------------------
BEGIN;
INSERT INTO `t_order_1` VALUES ('0c7d072694224f08a0d226e246f3f60f', 1101, 11, 'INIT');
INSERT INTO `t_order_1` VALUES ('20c745292b1d4d5e91179cbb78476848', 1107, 11, 'INIT');
INSERT INTO `t_order_1` VALUES ('2b2dae40ef2e45a6a4a5cdefb141e87a', 1105, 11, 'INIT');
INSERT INTO `t_order_1` VALUES ('77efed2798e8412493b87d7c7051502c', 1103, 11, 'INIT');
INSERT INTO `t_order_1` VALUES ('fe34e095b6de4d128f0c64e2f657bb03', 1109, 11, 'INIT');
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
INSERT INTO `t_order_item_0` VALUES ('0c1d85c769804e5593b812fd9f97200b', 110401, 1104, 11);
INSERT INTO `t_order_item_0` VALUES ('40903b9e1c734530bcaca3e7c63c4c64', 110801, 1108, 11);
INSERT INTO `t_order_item_0` VALUES ('907b732d20ce47dca749d51b611272bf', 110201, 1102, 11);
INSERT INTO `t_order_item_0` VALUES ('b48b239811704f878cc4ac9b018ee500', 110601, 1106, 11);
INSERT INTO `t_order_item_0` VALUES ('e6a0b46400c54a7f8ea8bb5cbc86ebd9', 110001, 1100, 11);
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
INSERT INTO `t_order_item_1` VALUES ('cb3fc510106541a1ad9111e9743141c4', 110901, 1109, 11);
INSERT INTO `t_order_item_1` VALUES ('cd8bb0027b684302bbbf2e3684022d95', 110701, 1107, 11);
INSERT INTO `t_order_item_1` VALUES ('ce72cb24e5b34af49df6392cd6a2df4a', 110501, 1105, 11);
INSERT INTO `t_order_item_1` VALUES ('dfe6024061fe4f22828dadfcd3af1000', 110301, 1103, 11);
INSERT INTO `t_order_item_1` VALUES ('fc94632e82cd49279d26966848c97968', 110101, 1101, 11);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
