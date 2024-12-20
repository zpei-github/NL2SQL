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

package com.web.entity.mysql.standard_database;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "standard_table")
public class StandardTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "standard_table_id")
    private Integer standardTableId; // 标准规范表id

    @Column(name = "original_table_name", nullable = false)
    private String originalTableName; // 源库表名

    @Column(name = "standard_table_name")
    private String standardTableName; // 标准规范表名

    @Column(name = "table_schema", nullable = false)
    private String tableSchema; // 源库名

    @Column(name = "table_comment", nullable = false)
    private String tableComment; // 表备注

    @Column(name = "column_rows")
    private Integer columnRows; // 表数据量

    @Column(name = "granularity_name")
    private String granularityName; // 表的粒度
}

