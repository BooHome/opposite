/*
Navicat MySQL Data Transfer

Source Server         : local_mysql
Source Server Version : 50723
Source Host           : localhost:3306
Source Database       : boohome_wechat_base

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2018-11-20 18:38:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for base_test
-- ----------------------------
DROP TABLE IF EXISTS `base_test`;
CREATE TABLE `base_test` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `REMARK` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of base_test
-- ----------------------------
INSERT INTO `base_test` VALUES ('1', '测试用例base');

-- ----------------------------
-- Table structure for wechat_usermember
-- ----------------------------
DROP TABLE IF EXISTS `wechat_usermember`;
CREATE TABLE `wechat_usermember` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '关联用户ID',
  `openid` varchar(255) DEFAULT NULL COMMENT 'openID',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `sex` int(1) DEFAULT NULL COMMENT '性别 1男 2女',
  `language` varchar(255) DEFAULT NULL COMMENT '语言版本',
  `city` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `headimgurl` varchar(255) DEFAULT NULL,
  `subscribe_time` datetime DEFAULT NULL COMMENT '关注时间',
  `unsubscribe_time` datetime DEFAULT NULL COMMENT '取关时间',
  `subscribe` int(1) DEFAULT NULL COMMENT '关注状态 1已关注 0未关注',
  `subscribe_scene` varchar(255) DEFAULT NULL COMMENT '关注来源',
  `status` int(1) DEFAULT '1' COMMENT '用户状态 1 启用 0禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of wechat_usermember
-- ----------------------------
INSERT INTO `wechat_usermember` VALUES ('1', null, 'oFbUQ03SVA64c8OfOM0mynR_s8_4', '冯世博', '1', 'zh_CN', '平顶山', '河南', '中国', 'http://thirdwx.qlogo.cn/mmopen/SYeWkon6C6LT1IUVjqEpCzpFKmWeKSic7JlmIyCtXDkBCp3gIHkE4bicgF0KhMoVfx4Q3fyX7Svt7KnOb5jIgVBY3lqYwNC3Hj/132', '1970-01-18 20:31:47', null, '1', 'ADD_SCENE_QR_CODE', '1');
