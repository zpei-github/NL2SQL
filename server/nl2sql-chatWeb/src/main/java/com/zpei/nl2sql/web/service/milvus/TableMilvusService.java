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
package com.zpei.nl2sql.web.service.milvus;

import com.zpei.nl2sql.web.entity.milvus.StandardTableSchema;
import com.zpei.nl2sql.web.service.llm.OllamaEmbeddingService;
import com.zpei.nl2sql.web.tools.ReflectTools;
import io.milvus.v2.client.MilvusClientV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.milvus.v2.service.collection.request.LoadCollectionReq;
import io.milvus.v2.service.collection.request.ReleaseCollectionReq;
import io.milvus.v2.service.vector.response.SearchResp;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TableMilvusService {
    @Autowired
    private MilvusClientV2 client;

    @Autowired
    private OllamaEmbeddingService embedding;

    @Value("${milvus.collections.standard_table.name}")
    private String collectionName;

    @Value("${milvus.collections.standard_table.dense_vector_field}")
    private String denseVectorField;

    @Value("${milvus.collections.standard_table.sparse_vector_field}")
    private String sparseVectorField;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MilvusSearchKit milvusSearchKit;


    @PostConstruct
    private void init(){
        // 装载collection是进行相似性索引的必要条件
        client.loadCollection(LoadCollectionReq.builder()
                .collectionName(collectionName)
                .build());

        milvusSearchKit = new MilvusSearchKit(client,
                embedding,
                collectionName,
                denseVectorField,
                sparseVectorField,
                ReflectTools.outputFieldsNameList(StandardTableSchema.class)
        );
    }


    @PreDestroy
    private void destroy(){
        // 服务销毁之后会释放milvus上的collection
        client.releaseCollection(ReleaseCollectionReq.builder()
                .collectionName(collectionName)
                .build());
    }


    public List<StandardTableSchema> annSearch(String query, int topK){
        List<StandardTableSchema> results = new ArrayList<>();
        SearchResp resp = milvusSearchKit.annSearch(query, topK);
        for(List<SearchResp.SearchResult> searchResult : resp.getSearchResults()){
            results.add(objectMapper.convertValue(searchResult.get(0).getEntity(), StandardTableSchema.class));
        }
        return results;
    }


    public List<StandardTableSchema> hybridSearch(String query, int topK){
        List<StandardTableSchema> results = new ArrayList<>();
        SearchResp resp = milvusSearchKit.hybridSearch(query, topK);
        for(List<SearchResp.SearchResult> searchResult : resp.getSearchResults()){
            results.add(objectMapper.convertValue(searchResult.get(0).getEntity(), StandardTableSchema.class));
        }
        return results;
    }
}
