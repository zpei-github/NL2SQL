/*
 *   Copyright (c) 2024 zpei-github
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.zpei.nl2sql.web.entity.mysql.standard_database;
import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@Entity
public class StandardTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer standard_table_id; // 标准规范表id

    private String original_table_name; // 源库表名

    private String standard_table_name; // 标准规范表名

    private String table_schema; // 源库名

    private String table_comment; // 表备注

    private Long column_rows; // 表数据量

    private String granularity_name; // 表的粒度

    private Integer granularity_id; // 粒度id

    private String original_table_ddl; //表的建表语句
}

