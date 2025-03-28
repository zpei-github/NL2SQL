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
@Table(name = "standard_granularity_column")
public class StandardGranularityColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer granularity_column_id;

    private Integer granularity_id; // 粒度id

    private String granularity_name; // 粒度名

    private String standard_column_name; // 标准字段名

    private Integer standard_column_id; // 标准字段id


}

