drop table if exists simple_lock;
CREATE TABLE simple_lock(
    id             BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT COMMENT 'id',
    lock_key VARCHAR(50) NOT NULL,
    thread_id VARCHAR(50) NOT NULL COMMENT '锁的当前线程',
    expire_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锁失效时间',

    PRIMARY KEY (id),
    UNIQUE KEY uniq_lock_key (lock_key)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

drop table if exists serialize_support;
CREATE TABLE serialize_support(
                                  id  int UNSIGNED   NOT NULL COMMENT 'id',
                                  PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

insert into serialize_support(id) values (0), (1), (2), (3), (4), (5), (6), (7), (8), (9);