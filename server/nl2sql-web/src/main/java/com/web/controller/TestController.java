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
