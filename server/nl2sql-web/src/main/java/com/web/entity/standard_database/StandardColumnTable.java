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

package com.web.entity.standard_database;


import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "standard_column_table")
public class StandardColumnTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "standard_column_table_id")
    private Integer standardColumnTableId; // 主键id

    @Column(name = "standard_table_name", nullable = false)
    private String standardTableName; // 标准表名

    @Column(name = "standard_column_name", nullable = false)
    private String standardColumnName; // 标准字段名

    @Column(name = "standard_column_id" , nullable = false)
    private Integer standardColumnId; // 标准字段id

    @Column(name = "standard_table_id", nullable = false)
    private Integer standardTableId; // 标准规范表id
}

