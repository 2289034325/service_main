/*
Navicat MySQL Data Transfer

Source Server         : mtc
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : ava

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2019-12-09 21:08:25
*/

CREATE DATABASE ava CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
use ava;

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
  `video_id` varchar(36) DEFAULT NULL,
  `audio_id` varchar(36) DEFAULT NULL,
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
  `media_usage` tinyint(4) NOT NULL COMMENT '目前只有 表演者的原声 audio,video，以后可以扩展到讲解视频，模仿视频',
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for speech.media
-- ----------------------------
DROP TABLE IF EXISTS `speech.media`;
CREATE TABLE `speech.media` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL,
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
  `index` int(11) NOT NULL,
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
  `index` int(11) NOT NULL,
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
-- Table structure for user.credential
-- ----------------------------
DROP TABLE IF EXISTS `user.credential`;
CREATE TABLE `user.credential` (
  `id` varchar(36) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `roles` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `ava`.`user.credential` (`id`, `username`, `password`, `roles`, `enabled`) VALUES ('daec0636-0db0-43d4-a968-5095b66afd15', 'ac', 'e10adc3949ba59abbe56e057f20f883e', 'admin,user', b'1');

-- ----------------------------
-- Table structure for user.user
-- ----------------------------
DROP TABLE IF EXISTS `user.user`;
CREATE TABLE `user.user` (
  `id` varchar(36) NOT NULL,
  `credential_id` varchar(36) NOT NULL,
  `name` varchar(50) NOT NULL,
  `sex` bit(1) NOT NULL,
  `birth` date NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `avatar` text NOT NULL,
  `reg_date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

INSERT INTO `ava`.`user.user` (`id`, `credential_id`, `name`, `sex`, `birth`, `email`, `phone`, `avatar`, `reg_date`) VALUES ('237640c8-9f25-47ab-8bb3-658490af4738', 'daec0636-0db0-43d4-a968-5095b66afd15', '旺财', b'1', '2019-11-12', '22@qq.com', '123', '123', '2019-11-16');

-- ----------------------------
-- Table structure for vocab.explain
-- ----------------------------
DROP TABLE IF EXISTS `vocab.explain`;
CREATE TABLE `vocab.explain` (
  `id` varchar(36) NOT NULL,
  `word_id` varchar(36) NOT NULL,
  `pronounce` varchar(50) NOT NULL,
  `explain` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vocab.learn_record
-- ----------------------------
DROP TABLE IF EXISTS `vocab.learn_record`;
CREATE TABLE `vocab.learn_record` (
  `id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `lang` tinyint(4) NOT NULL,
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
  `id` varchar(36) NOT NULL,
  `learn_record_id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `word_id` varchar(36) NOT NULL,
  `lang` tinyint(4) NOT NULL,
  `answer_times` int(11) NOT NULL COMMENT '上次学习或者复习时 回答次数',
  `wrong_times` int(11) NOT NULL COMMENT '上次学习或者复习时 回答错误的次数',
  `learn_time` datetime NOT NULL COMMENT '上次学习或者复习的时间',
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for vocab.sentence
-- ----------------------------
DROP TABLE IF EXISTS `vocab.sentence`;
CREATE TABLE `vocab.sentence` (
  `id` varchar(36) NOT NULL,
  `word_id` varchar(36) NOT NULL,
  `explain_id` varchar(36) NOT NULL,
  `word` varchar(50) NOT NULL,
  `sentence` varchar(255) NOT NULL,
  `translation` varchar(255) NOT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  `answer_times` int(11) NOT NULL,
  `wrong_times` int(11) NOT NULL,
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
  `id` varchar(36) NOT NULL,
  `lang` tinyint(4) NOT NULL,
  `spell` varchar(50) NOT NULL,
  `pronounce` varchar(50) NOT NULL,
  `meaning` varchar(512) NOT NULL,
  `forms` varchar(255) NOT NULL COMMENT '可能的变形，例如负数，进行时，过去时等等',
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
