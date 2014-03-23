/**
 *  所有TABLE均包含如下字段(ID)作为主键：
 *  id           INTEGER NOT NULL AUTO_INCREMENT COMMENT 'ID',
 *  owner        BIGINT NOT NULL COMMENT '创建者',
 *  modifier     BIGINT NOT NULL COMMENT '修改者',
 *  created      TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
 *  modified     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
 *  version      TINYINT NOT NULL DEFAULT 0 COMMENT '数据版本',
 *  status       TINYINT NOT NULL DEFAULT 1 COMMENT '状态标记'
 */

/*创建数据库*/
DROP database IF EXISTS xfileupload;
CREATE database xfileupload;
USE xfileupload;

/*创建用户*/
GRANT SELECT,INSERT,UPDATE,DELETE,EXECUTE ON xfileupload.* TO 'xfileupload'@'localhost' IDENTIFIED BY 'xfileupload';

/*==============================================================*/
/* Table: xhome_xfileupload_file_content                        */
/*==============================================================*/
CREATE TABLE xhome_xfileupload_file_content
(
   id                   INTEGER NOT NULL AUTO_INCREMENT COMMENT '上传文件记录ID',
   path                 VARCHAR(255) NOT NULL COMMENT '文件保存路径',
   name                 VARCHAR(32) NOT NULL COMMENT '上传文件名',
   original             VARCHAR(255) NOT NULL COMMENT '原始文件名',
   type                 VARCHAR(20) NOT NULL COMMENT '文件类型',
   size                 BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小',
   owner                BIGINT NOT NULL COMMENT '创建者',
   modifier             BIGINT NOT NULL COMMENT '修改者',
   created              TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
   modified             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
   version              TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '版本',
   status               TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态 1: 正常, 5: 锁定, 10: 不允许修改, 15: 不允许删除',
   PRIMARY KEY (id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
AUTO_INCREMENT = 1;

ALTER TABLE xhome_xfileupload_file_content COMMENT '上传文件记录';
