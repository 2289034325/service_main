/*
Navicat MySQL Data Transfer

Source Server         : mtc
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : ava

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2019-11-15 17:45:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for reading.book_mark
-- ----------------------------
DROP TABLE IF EXISTS `reading.book_mark`;
CREATE TABLE `reading.book_mark` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `url` text NOT NULL,
  `time` datetime NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for speech.article
-- ----------------------------
DROP TABLE IF EXISTS `speech.article`;
CREATE TABLE `speech.article` (
  `id` varchar(36) NOT NULL,
  `lang` tinyint(4) NOT NULL,
  `title` varchar(50) NOT NULL,
  `description` varchar(512) NOT NULL,
  `performer` varchar(50) NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for speech.article_media
-- ----------------------------
DROP TABLE IF EXISTS `speech.article_media`;
CREATE TABLE `speech.article_media` (
  `id` varchar(36) NOT NULL,
  `article_id` varchar(36) NOT NULL,
  `media_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for speech.media
-- ----------------------------
DROP TABLE IF EXISTS `speech.media`;
CREATE TABLE `speech.media` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` varchar(20) NOT NULL,
  `path` varchar(255) NOT NULL COMMENT '文件存储位置，本地硬盘中或者oss上',
  `time` float NOT NULL COMMENT '音频或者视频时长',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for speech.paragraph
-- ----------------------------
DROP TABLE IF EXISTS `speech.paragraph`;
CREATE TABLE `speech.paragraph` (
  `id` varchar(36) NOT NULL,
  `article_id` varchar(36) NOT NULL,
  `text` text NOT NULL,
  `translation` text NOT NULL,
  `performer` varchar(20) NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for speech.recite
-- ----------------------------
DROP TABLE IF EXISTS `speech.recite`;
CREATE TABLE `speech.recite` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `article_id` varchar(36) NOT NULL,
  `paragraph_id` varchar(36) NOT NULL,
  `split_id` varchar(36) NOT NULL,
  `use_time` float NOT NULL,
  `score` float NOT NULL,
  `content` text NOT NULL,
  `submit_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for speech.split
-- ----------------------------
DROP TABLE IF EXISTS `speech.split`;
CREATE TABLE `speech.split` (
  `id` varchar(36) NOT NULL,
  `article_id` varchar(36) NOT NULL,
  `paragraph_id` varchar(36) NOT NULL,
  `start_index` int(11) NOT NULL COMMENT '在段落中的起始位置（字符串中的index）',
  `end_index` int(11) NOT NULL,
  `start_time` float NOT NULL COMMENT '在音频或者视频中对应的时间点',
  `end_time` float NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for stardict
-- ----------------------------
DROP TABLE IF EXISTS `stardict`;
CREATE TABLE `stardict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(64) NOT NULL,
  `sw` varchar(64) NOT NULL,
  `phonetic` varchar(64) DEFAULT NULL,
  `definition` text,
  `translation` text,
  `pos` varchar(16) DEFAULT NULL,
  `collins` smallint(6) DEFAULT '0',
  `oxford` smallint(6) DEFAULT '0',
  `tag` varchar(64) DEFAULT NULL,
  `bnc` int(11) DEFAULT NULL,
  `frq` int(11) DEFAULT NULL,
  `exchange` text,
  `detail` text,
  `audio` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `word` (`word`),
  KEY `sw` (`sw`,`word`),
  KEY `collins` (`collins`),
  KEY `oxford` (`oxford`),
  KEY `tag` (`tag`)
) ENGINE=MyISAM AUTO_INCREMENT=3397295 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(36) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `roles` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `avatar` text NOT NULL,
  `enabled` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vocab.explain
-- ----------------------------
DROP TABLE IF EXISTS `vocab.explain`;
CREATE TABLE `vocab.explain` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word_id` int(11) NOT NULL,
  `pronounce` varchar(255) NOT NULL,
  `explain` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1658 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vocab.learn_record
-- ----------------------------
DROP TABLE IF EXISTS `vocab.learn_record`;
CREATE TABLE `vocab.learn_record` (
  `id` varchar(36) NOT NULL,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `user_book_id` int(11) NOT NULL,
  `word_count` int(11) NOT NULL,
  `answer_times` int(11) NOT NULL COMMENT '答题次数',
  `wrong_times` int(11) NOT NULL COMMENT '答错次数',
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `deleted` bit(1) NOT NULL COMMENT '暂停时间太长，用户想重新学习时，可以将所有学习记录删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vocab.learn_record_detail
-- ----------------------------
DROP TABLE IF EXISTS `vocab.learn_record_detail`;
CREATE TABLE `vocab.learn_record_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `learn_record_id` varchar(36) NOT NULL,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `user_book_id` int(11) NOT NULL,
  `word_id` int(11) NOT NULL,
  `answer_times` int(11) NOT NULL COMMENT '上次学习或者复习时 回答次数',
  `wrong_times` int(11) NOT NULL COMMENT '上次学习或者复习时 回答错误的次数',
  `learn_time` datetime NOT NULL COMMENT '上次学习或者复习的时间',
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=385 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vocab.sentence
-- ----------------------------
DROP TABLE IF EXISTS `vocab.sentence`;
CREATE TABLE `vocab.sentence` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `word_id` int(11) NOT NULL,
  `explain_id` int(11) NOT NULL,
  `word` varchar(255) NOT NULL,
  `sentence` varchar(255) NOT NULL,
  `translation` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2664 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vocab.user_word
-- ----------------------------
DROP TABLE IF EXISTS `vocab.user_word`;
CREATE TABLE `vocab.user_word` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `word_id` varchar(36) NOT NULL,
  `lang` tinyint(4) NOT NULL,
  `phase` tinyint(4) NOT NULL,
  `finished` bit(1) NOT NULL,
  `last_review_time` datetime DEFAULT NULL,
  `next_review_date` date DEFAULT NULL,
  `add_time` datetime NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vocab.word
-- ----------------------------
DROP TABLE IF EXISTS `vocab.word`;
CREATE TABLE `vocab.word` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lang` tinyint(4) NOT NULL,
  `spell` varchar(255) NOT NULL,
  `pronounce` varchar(255) NOT NULL,
  `meaning` varchar(255) NOT NULL,
  `forms` varchar(255) NOT NULL COMMENT '可能的变形，例如负数，进行时，过去时等等',
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=217 DEFAULT CHARSET=utf8mb4;
