-- granularity: table
CREATE TABLE `granularity` (
  `granularity_id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `granularity_name` varchar(255) NOT NULL,
  `granularity_comment` varchar(255) NOT NULL COMMENT '粒度备注',
  PRIMARY KEY (`granularity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='粒度表';

-- standard_column: table
CREATE TABLE `standard_column` (
  `standard_column_id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `standard_column_name` varchar(255) DEFAULT NULL COMMENT '标准表名',
  `original_column_name` varchar(255) NOT NULL COMMENT '源库字段名',
  `column_comment` varchar(255) NOT NULL COMMENT '字段备注',
  `table_schema` varchar(255) NOT NULL COMMENT '源库名',
  PRIMARY KEY (`standard_column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标准字段映射';

-- standard_column_table: table
CREATE TABLE `standard_column_table` (
  `standard_column_table_id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `standard_table_name` varchar(255) NOT NULL COMMENT '标准表名',
  `standard_column_name` varchar(255) NOT NULL COMMENT '标准字段名',
  `standard_column_id` int NOT NULL COMMENT '标准字段id',
  `standard_table_id` int NOT NULL COMMENT '标准表id',
  PRIMARY KEY (`standard_column_table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标准字段和表的映射';

-- standard_granularity_column: table
CREATE TABLE `standard_granularity_column` (
  `granularity_id` int NOT NULL COMMENT '粒度id',
  `granularity_name` varchar(255) NOT NULL COMMENT '粒度名',
  `standard_column_name` varchar(255) DEFAULT NULL COMMENT '标准字段名',
  `standard_column_id` int DEFAULT NULL COMMENT '标准字段id',
  `granularity_column_id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  PRIMARY KEY (`granularity_column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='粒度与字段的关系';

-- standard_table: table
CREATE TABLE `standard_table` (
  `standard_table_id` int NOT NULL AUTO_INCREMENT COMMENT '标准规范表id',
  `original_table_name` varchar(255) NOT NULL COMMENT '源库表名',
  `standard_table_name` varchar(255) DEFAULT NULL COMMENT '标准规范表名',
  `table_schema` varchar(255) NOT NULL COMMENT '源库名',
  `table_comment` varchar(255) NOT NULL COMMENT '表备注',
  `column_rows` int DEFAULT NULL COMMENT '表数据量',
  `granularity_name` varchar(255) DEFAULT NULL COMMENT '表的粒度',
  `granularity_id` int DEFAULT NULL,
  `original_table_ddl` text COMMENT '表的ddl语句',
  PRIMARY KEY (`standard_table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标准表映射';

