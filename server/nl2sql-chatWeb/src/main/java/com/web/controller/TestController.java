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
package com.web.controller;

import com.web.entity.elasticsearch.StandardTableIndex;

import com.web.service.elasticsearch.ElasticsearchService;
import com.web.service.graph.GraphComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    GraphComponent graphComponent;

    @Autowired
    ElasticsearchService esService;
    // 创建一个简单的接口用于测试是否成功启动
    @PostMapping("/graph")
    public void testEndpoint(@RequestBody String comment) {
        System.out.println(esService.searchByColumnComment("standard_column_index", comment));
    }
}
