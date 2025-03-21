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
package com.web.service.milvus;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.entity.milvus.StandardColumnSchema;
import com.web.service.llm.OllamaEmbeddingService;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.collection.request.LoadCollectionReq;
import io.milvus.v2.service.collection.request.ReleaseCollectionReq;
import io.milvus.v2.service.vector.response.SearchResp;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ColumnMilvusService{
    @Autowired
    private MilvusClientV2 client;

    @Autowired
    private OllamaEmbeddingService embedding;

    @Value("${milvus.collections.standard_column.name}")
    private String collectionName;

    @Value("${milvus.collections.standard_column.dense_vector_field}")
    private String denseVectorField;

    @Value("${milvus.collections.standard_column.sparse_vector_field}")
    private String sparseVectorField;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MilvusSearchKit milvusSearchKit;


    @PostConstruct
    private void init(){
        // 装载collection是进行相似性索引的必要条件
        client.loadCollection(LoadCollectionReq.builder()
                .collectionName(collectionName)
                .build());

        milvusSearchKit = new MilvusSearchKit(client, embedding, collectionName, denseVectorField, sparseVectorField);

        // 通过反射设置需要从collection中获取结果的字段
        milvusSearchKit.setOutputFields(milvusSearchKit.outputFieldsConstruct(StandardColumnSchema.class));
    }

    @PreDestroy
    private void destroy(){
        // 服务销毁之后会释放milvus上的collection
        client.releaseCollection(ReleaseCollectionReq.builder()
                .collectionName(collectionName)
                .build());
    }


    public List<StandardColumnSchema> annSearch(String query, int topK){
        List<StandardColumnSchema> results = new ArrayList<>();
        SearchResp resp = milvusSearchKit.annSearch(query, topK);
        for(List<SearchResp.SearchResult> searchResult : resp.getSearchResults()){
            results.add(objectMapper.convertValue(searchResult.get(0).getEntity(), StandardColumnSchema.class));
        }
        return results;
    }

    public List<StandardColumnSchema> hybridSearch(String query, int topK){
        List<StandardColumnSchema> results = new ArrayList<>();
        SearchResp resp = milvusSearchKit.hybridSearch(query, topK);
        for(List<SearchResp.SearchResult> searchResult : resp.getSearchResults()){
            results.add(objectMapper.convertValue(searchResult.get(0).getEntity(), StandardColumnSchema.class));
        }
        return results;
    }
}
