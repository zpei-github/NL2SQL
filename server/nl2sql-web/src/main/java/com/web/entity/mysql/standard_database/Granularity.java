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
@Table(name = "granularity")
public class Granularity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "granularity_id")
    private Integer granularityId; // 主键id

    @Column(name = "granularit_name", nullable = false)
    private String granularitName; // 粒度名

    @Column(name = "granularity_comment", nullable = false)
    private String granularityComment; // 粒度备注
}
