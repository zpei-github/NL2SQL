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
package com.web.service.elasticsearch;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.web.entity.elasticsearch.StandardColumnIndex;
import com.web.entity.elasticsearch.StandardTableIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ElasticsearchService {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchService.class);
    @Autowired
    private ElasticsearchClient client;

    // 插入文档
    public String indexDocument(String indexName, String documentId, Object document) {
        try {
            IndexResponse response;

            if (documentId != null) {
                // 使用指定的 documentId 插入文档
                response = client.index(IndexRequest.of(i -> i
                        .index(indexName)
                        .id(documentId)
                        .document(document)
                ));
            } else {
                // 自动生成 documentId
                response = client.index(IndexRequest.of(i -> i
                        .index(indexName)
                        .document(document)
                ));
            }
            return response.result().name();
        } catch (IOException e) {
            log.error(e.getMessage());
            return "Error";
        }
    }


    // 获取文档
    public <T> T getDocument(String indexName, String documentId, Class<T> documentClass) {
        try {
            GetResponse<T> response = client.get(GetRequest.of(g -> g
                    .index(indexName)
                    .id(documentId)
            ), documentClass);

            return response.source();
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }


    // 通过 tableComment 使用 IK 分词器进行查询
    public List<StandardTableIndex> searchByTableComment(String indexName, String tableComment) {
        try {
            // 创建搜索请求，使用 match 查询，并指定 ik_max_word 分词器
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(indexName)
                    .query(q -> q
                            .match(m -> m
                                    .field("table_comment")
                                    .query(tableComment) // 搜索的内容
                                    .analyzer("ik_max_word") // 使用 IK 分词器
                            )
                    )
            );

            // 执行查询
            SearchResponse<StandardTableIndex> searchResponse = client.search(searchRequest, StandardTableIndex.class);

            // 处理查询结果并返回
            List<Hit<StandardTableIndex>> hits = searchResponse.hits().hits();
            return hits.stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // 通过 columnComment 使用 IK 分词器进行查询
    public List<StandardColumnIndex> searchByColumnComment(String indexName, String columnComment) {
        try {
            // 创建搜索请求，使用 match 查询，并指定 ik_max_word 分词器
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(indexName)
                    .query(q -> q
                            .match(m -> m
                                    .field("column_comment")
                                    .query(columnComment) // 搜索的内容
                                    .analyzer("ik_max_word") // 使用 IK 分词器
                            )
                    )
            );
            // 执行查询
            SearchResponse<StandardColumnIndex> searchResponse = client.search(searchRequest, StandardColumnIndex.class);
            // 处理查询结果并返回
            List<Hit<StandardColumnIndex>> hits = searchResponse.hits().hits();
            return hits.stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}


