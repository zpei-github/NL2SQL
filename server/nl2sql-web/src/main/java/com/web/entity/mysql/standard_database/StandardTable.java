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
    private Integer standardTableId; // 标准规范表id

    private String originalTableName; // 源库表名

    private String standardTableName; // 标准规范表名

    private String tableSchema; // 源库名

    private String tableComment; // 表备注

    private Integer columnRows; // 表数据量

    private String granularityName; // 表的粒度

    private Integer granularityId; // 粒度id

    private String originalTableDDL; //表的建表语句
}

