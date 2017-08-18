create database wxRank
DEFAULT CHARACTER SET utf8
DEFAULT COLLATE utf8_general_ci;

use wxRank;
#理财产品记录表
create table mr_recorders(
		id INT NOT NULL AUTO_INCREMENT COMMENT '非空自增',
    productName VARCHAR(100) COMMENT '产品名称',
    ProductCode VARCHAR(100) COMMENT '产品代码',
    type  VARCHAR(10) COMMENT '类型 1，活期保本 2，定期保本 3，活期高收益 4，定期高收益',
		rate VARCHAR(10) COMMENT '年华利率',
		profit VARCHAR(10) COMMENT '万份收益',
    lockups VARCHAR(10) COMMENT '锁定期',
    startAccount VARCHAR(1000) COMMENT '起投金额',
    manager  VARCHAR(1000) COMMENT '产品管理单位',
    trusteeship VARCHAR(1000) COMMENT '产品托管/存管单位',
    riskrank   VARCHAR(1000) COMMENT '风险等级',
    intestes VARCHAR(1000) COMMENT '计利方式（单利，日复利）',
		date Date COMMENT '利率对应日期',
    productStatus  VARCHAR(1000) COMMENT '产品状态（零活赎买）',
		PRIMARY KEY (id) COMMENT '设置主键'
);
//企业信息
create table mr_enterprise(
    enterpriseName VARCHAR(1000) COMMENT '企业名称',
    shortName VARCHAR(1000) COMMENT '企业简称',
    enterpriseCode VARCHAR(100) COMMENT '企业代码',
    legalRepresentative VARCHAR(100) COMMENT '法定代表人',
    enterpriseType  VARCHAR(100) COMMENT '企业类型(有限责任公司)',
		registrationCapital VARCHAR(100) COMMENT '注册资本',
    registrationAuthority VARCHAR(100) COMMENT '登记机关',
    registrationStatus VARCHAR(100) COMMENT '经营状态',
		registrationDate Date  COMMENT '注册日期',
    operateFromDate Date  COMMENT '营业期限自',
    operateToDate Date    COMMENT '营业期限到',
    enterpriceAnomal  VARCHAR(1000) COMMENT '企业异常情况，0.企业运行良好 1.行政处罚 2.经营异常 3.严重违法失信企业',
		PRIMARY KEY (enterpriseCode) COMMENT '设置主键'
);

//企业产品关联表
create table mr_enterpriseProduct(
		id INT NOT NULL AUTO_INCREMENT COMMENT '非空自增',
    enterpriseCode VARCHAR(100) COMMENT '企业代码',
    productName   VARCHAR(1000) COMMENT '产品名称',
    productCode  VARCHAR(1000) COMMENT '产品代码',
    PRIMARY KEY (id) COMMENT '设置主键'
);
//
DROP TABLE IF EXISTS `mr_currentFinancialProducts`;
CREATE TABLE `mr_currentFinancialProducts` (
  `productId` int(11) NOT NULL COMMENT '产品ID',
  `productName` varchar(1000) DEFAULT NULL COMMENT '产品名称',
  `type` varchar(10) DEFAULT NULL,
  `rate` varchar(100) DEFAULT NULL COMMENT '7日年华利率',
  `profit` varchar(100) DEFAULT NULL COMMENT '万分收益',
  `startAccount` varchar(100) DEFAULT NULL COMMENT '起投金额',
  `auditFlag` varchar(100) DEFAULT NULL,
  `enterpriseName` varchar(1000) DEFAULT NULL COMMENT '发行机构',
  `takeSpeedStr` varchar(1000) DEFAULT NULL COMMENT '提现速度',
  `takeSpeed` varchar(1000) DEFAULT NULL,
  `takeLimit` varchar(1000) DEFAULT NULL COMMENT '单笔及单日上限',
  `weight` varchar(1000) DEFAULT NULL,
  `date` date DEFAULT NULL COMMENT '日期',
  `capitalScale` varchar(1000) DEFAULT NULL COMMENT '资金规模',
  `interest` varchar(100) DEFAULT NULL COMMENT '计利方式',
  `riskRank` VARCHAR(100) COMMENT '风险评级',
  `investmentMoney` VARCHAR(100) COMMENT '起投金额',
  primary key(productId,date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#风险等级表，产品风险特性，一般银行将理财产品风险由低到高分为R1-R5 5个等级：
DROP TABLE IF EXISTS `mr_risk`;
CREATE TABLE `mr_risk` (
  `id` int(11) NOT NULL COMMENT 'ID',
  `code` varchar(10) DEFAULT NULL COMMENT '等级编码',
  `memo` varchar(10) DEFAULT NULL COMMENT '备注说明'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mr_bank_rate`;
create table mr_bank_rate(
`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`name` VARCHAR(100) COMMENT '银行名称',
`rateLiquid` varchar(30) COMMENT '活期存款利率',
`rateFixed0p3m` varchar(30) COMMENT '整存整取3个月定期',
`rateFixed0p6m` varchar(30) COMMENT '整存整取6个月定期',
`rateFixed0p1y` varchar(30) COMMENT '整存整取1年定期',
`rateFixed0p2y` varchar(30) COMMENT '整存整取2年定期',
`rateFixed0p3y` varchar(30) COMMENT '整存整取3年定期',
`rateFixed0p5y` varchar(30) COMMENT '整存整取5年定期',
`rateOther1y` varchar(30) COMMENT '零存1年',
`rateOther3y` varchar(30) COMMENT '零存3年',
`rateOther5y` varchar(30) COMMENT '零存5年',
`rateNotice1d` varchar(30) COMMENT '通知存款1天',
`rateNotice7d` varchar(30) COMMENT '通知存款7天',
`updateDate` varchar(30) COMMENT '日期'
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- T+0使用
DROP TABLE IF EXISTS `mr_capitalProfit`;
create table mr_capitalProfit(
`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`name` VARCHAR(100) COMMENT '产品名称',
`bank` varchar(30) COMMENT '发行银行',
`type` varchar(30) COMMENT '投向类型',
`limit`  varchar(30) COMMENT '锁定期限',
`rate` varchar(30) COMMENT '预期收益率',
`profitType` varchar(30) COMMENT '收益类型',
`StartAccount` varchar(30) COMMENT '投资起点',
`weeklynumber` varchar(30) COMMENT '期数',
`currency` varchar(30) COMMENT '币种',
`dateStart` varchar(30) COMMENT '期数开始日期',
`dateEnd` varchar(30) COMMENT '期数结束日期'
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mr_regular`;
create table mr_capitalProfit(
`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`name` VARCHAR(100) COMMENT '产品名称',
`bank` varchar(30) COMMENT '发行银行',
`type` varchar(30) COMMENT '投向类型',
`limit`  INT COMMENT '锁定期限',
`rate` varchar(30) COMMENT '预期收益率',
`profitType` varchar(30) COMMENT '收益类型',
`StartAccount` varchar(30) COMMENT '投资起点',
`weeklynumber` varchar(30) COMMENT '期数',
`currency` varchar(30) COMMENT '币种',
`dateStart` varchar(30) COMMENT '期数开始日期',
`dateEnd` varchar(30) COMMENT '期数结束日期'
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mr_user`;
create table `mr_user`(
`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`avatarUrl` VARCHAR(1000) COMMENT '图片网址',
`nickName` varchar(30) COMMENT '昵称',
`loginDate` varchar(30) COMMENT '登录日期'
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mr_favorite`;
create table `mr_favorite`(
`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`userName` VARCHAR(100) COMMENT '用户名',
`favorite_list1` varchar(1000) COMMENT '收藏活期非保本',
`favorite_list2` varchar(1000) COMMENT '收藏活期保本',
`favorite_list3` varchar(1000) COMMENT '收藏定期非保本',
`favorite_list4` varchar(1000) COMMENT '收藏定期保本',
`avatarUrl` VARCHAR(1000) COMMENT '图片网址',
`updateDate` varchar(30) COMMENT '更新日期'
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

#活期非保本数据
DROP TABLE IF EXISTS `mr_current`;
CREATE TABLE `mr_current` (
  `code` int(30) NOT NULL COMMENT '产品代码',
  `name` varchar(1000) NOT NULL COMMENT '产品名称',
  `rate` varchar(100) DEFAULT NULL COMMENT '7日年华利率',
  `profit` varchar(100) DEFAULT NULL COMMENT '昨日万元收益',
  `investMoney` INT DEFAULT 0 COMMENT '起投金额',
  `provider` varchar(1000) DEFAULT NULL COMMENT '发行机构',
  `trusteeship` varchar(1000) DEFAULT NULL COMMENT '托管银行',
  `takeSpeed` varchar(1000) DEFAULT NULL COMMENT '转出速度',
  `takeLimit`  INT DEFAULT 0 COMMENT '单笔及单日上限',
  `riskLevel` varchar(1000) DEFAULT NULL COMMENT '风险评级',
  `date` Date COMMENT '日期',
  `interest` varchar(100) DEFAULT NULL COMMENT '计利方式',
  `riskRank` VARCHAR(100) COMMENT '风险评级',
  primary key(`code`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

