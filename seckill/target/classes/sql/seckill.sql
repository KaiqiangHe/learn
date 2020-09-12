
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

/* 秒杀活动表 */
drop table if exists seckill_activity;
CREATE TABLE seckill_activity(
    id          BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT COMMENT 'id',
    activity_id VARCHAR(50) NOT NULL comment '活动id',
    activity_name   varchar(50) not null comment '活动名称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uniq_activity_id (activity_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;