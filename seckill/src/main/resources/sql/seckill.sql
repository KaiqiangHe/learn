drop table if exists stock;
CREATE TABLE stock(
                      id          BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT COMMENT 'id',
                      stock_id    VARCHAR(50) NOT NULL comment '库存id',
                      activity_id VARCHAR(50) NOT NULL comment '活动id',
                      use_stock   int unsigned not null default 0 comment '当前已使用库存',
                      total_stock int unsigned not null comment '总可使用库存',
                      create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                      PRIMARY KEY (id),
                      UNIQUE KEY uniq_stock_id (stock_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

/* 分表 */
drop table if exists sec_bill;
CREATE TABLE sec_bill
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    user_id varchar(32) not null comment '用户唯一id',
    bill_no      varchar(64)     not null comment '账单no',
    price       varchar(16)     not null comment '支付金额',
    ext         varchar(8192)   not null comment '支付信息',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE uniq_bill_no(bill_no)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

/* 分表 */
drop table if exists sec_order_1;
CREATE TABLE sec_order_1
(
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    user_id varchar(32) not null comment '用户唯一id',
    activity_id varchar(64) not null comment '活动id',
    order_no varchar(64) not null comment '订单号',
    bill_no varchar(64) not null comment '账单号',
    product_id varchar(64) not null comment '商品id',
    sec_count int UNSIGNED not null comment '商品个数',
    order_status int not null comment '订单状态',
    init_order_time DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT ' 生单时间',
    deduct_stock_time DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '库存扣减时间',
    pay_callback_time DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '支付回调时间',

    callback_count int not null default 0 comment '回调业务线接口次数',
    last_callback_time DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '上一次回调业务线时间',
    callback_success bit(1) not null default FALSE comment '回调业务线是否成功',

    ext         varchar(2048)   not null default '' comment '拓展字段',

    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE uniq_order_no(order_no),
    UNIQUE uniq_bill_no(bill_no),
    INDEX idx_user_id_activity_id(user_id, activity_id),
    INDEX idx_activity_id(activity_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;


