DROP DATABASE IF EXISTS eussiwebsitedb;
CREATE DATABASE eussiwebsitedb DEFAULT CHARACTER SET utf8;
USE eussiwebsitedb;


/*==============================================================*/
/* DBMS name:      MySQL 5.2                                    */
/* Created on:     2018-9-13 0:34:13                             */
/*==============================================================*/


drop table if exists t_board;

drop table if exists t_board_manager;

drop table if exists t_post;

drop table if exists t_topic;

drop table if exists t_user;

drop table if exists t_login_log;

CREATE TABLE `t_board` (
  `board_id` int(11) NOT NULL COMMENT '论坛版块ID',
  `board_name` varchar(150) NOT NULL default '' COMMENT '论坛版块名',
  `board_desc` varchar(255) default NULL COMMENT '论坛版块描述',
  `topic_num` int(11) NOT NULL default '0' COMMENT '帖子数目',
  PRIMARY KEY  (`board_id`),
  KEY `AK_Board_NAME` (`board_name`)
) ENGINE=InnoDB=9 DEFAULT CHARSET=utf8;

#
# Dumping data for table t_board
#

INSERT INTO `t_board` VALUES (1,'科技','5G时代将到来\r\n',2);
INSERT INTO `t_board` VALUES (2,'教育','时代教育\r\n',0);

#
# Source for table t_board_manager
#

CREATE TABLE `t_board_manager` (
  `board_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY  (`board_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='论坛管理员';

#
# Dumping data for table t_board_manager
#

INSERT INTO `t_board_manager` VALUES (1,1);
INSERT INTO `t_board_manager` VALUES (1,2);
INSERT INTO `t_board_manager` VALUES (2,2);
INSERT INTO `t_board_manager` VALUES (2,3);

#
# Source for table t_login_log
#

CREATE TABLE `t_login_log` (
  `login_log_id` int(11) NOT NULL,
  `user_id` int(11) default NULL,
  `ip` varchar(30) NOT NULL default '',
  `login_datetime` varchar(30) NOT NULL,
  PRIMARY KEY  (`login_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Dumping data for table t_login_log
#


#
# Source for table t_post
#

CREATE TABLE `t_post` (
  `post_id` int(11) NOT NULL COMMENT '帖子ID',
  `board_id` int(11) NOT NULL default '0' COMMENT '论坛ID',
  `topic_id` int(11) NOT NULL default '0' COMMENT '话题ID',
  `user_id` int(11) NOT NULL default '0' COMMENT '发表者ID',
  `post_type` tinyint(4) NOT NULL default '2' COMMENT '帖子类型 1:主帖子 2:回复帖子',
  `post_title` varchar(50) NOT NULL COMMENT '帖子标题',
  `post_text` text NOT NULL COMMENT '帖子内容',
  `create_time` date NOT NULL COMMENT '创建时间',
  PRIMARY KEY  (`post_id`),
  KEY `IDX_POST_TOPIC_ID` (`topic_id`)
) ENGINE=InnoDB=25 DEFAULT CHARSET=utf8 COMMENT='帖子';

#
# Dumping data for table t_post
#

INSERT INTO `t_post` VALUES (1,1,1,1,1,'科技','苹果并非芯片和AI公司，但它的芯片和AI真的令人惊艳\r\n','2018-03-07');
INSERT INTO `t_post` VALUES (2,1,1,1,2,'5G','想要八冠王恒大就必须赢国安上港 卡帅正名之战','2018-03-07');

#
# Source for table t_topic
#

CREATE TABLE `t_topic` (
  `topic_id` int(11) NOT NULL COMMENT '帖子ID',
  `board_id` int(11) NOT NULL COMMENT '所属论坛',
  `topic_title` varchar(100) NOT NULL default '' COMMENT '帖子标题',
  `user_id` int(11) NOT NULL default '0' COMMENT '发表用户',
  `create_time` date NOT NULL COMMENT '发表时间',
  `last_post` date NOT NULL COMMENT '最后回复时间',
  `topic_views` int(11) NOT NULL default '1' COMMENT '浏览数',
  `topic_replies` int(11) NOT NULL default '0' COMMENT '回复数',
  `digest` int(11) NOT NULL COMMENT '0:不是精华话题 1:是精华话题',
  PRIMARY KEY  (`topic_id`),
  KEY `IDX_TOPIC_USER_ID` (`user_id`),
  KEY `IDX_TOPIC_TITLE` (`topic_title`)
) ENGINE=InnoDB=24 DEFAULT CHARSET=utf8 COMMENT='话题';

#
# Dumping data for table t_topic
#

INSERT INTO `t_topic` VALUES (1,1,'test',1,'2018-09-07','2018-09-18',0,1,1);


#
# Source for table t_user
#

CREATE TABLE `t_user` (
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `user_name` varchar(30) NOT NULL COMMENT '用户名',
  `password` varchar(30) NOT NULL default '' COMMENT '密码',
  `user_type` tinyint(4) NOT NULL default '1' COMMENT '1:普通用户 2:管理员',
  `locked` tinyint(4) NOT NULL default '0' COMMENT '0:未锁定 1:锁定',
  `credit` int(11) default NULL COMMENT '积分',
  `last_visit` datetime default NULL COMMENT '最后登陆时间',
  `last_ip` varchar(20) default NULL COMMENT '最后登陆IP',
  PRIMARY KEY  (`user_id`),
  KEY `AK_AK_USER_USER_NAME` (`user_name`)
) ENGINE=InnoDB=4 DEFAULT CHARSET=utf8;

#
# Dumping data for table t_user
#

INSERT INTO `t_user` VALUES (1,'user1','1234',1,1,203,NULL,NULL);
INSERT INTO `t_user` VALUES (2,'admin','1234',2,1,10,NULL,NULL);
INSERT INTO `t_user` VALUES (3,'user2','1234',1,0,110,NULL,NULL);