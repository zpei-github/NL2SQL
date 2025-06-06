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


import com.zpei.nl2sql.web.service.llm.OllamaEmbeddingService;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.ConsistencyLevel;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.vector.request.AnnSearchReq;
import io.milvus.v2.service.vector.request.HybridSearchReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.BaseVector;
import io.milvus.v2.service.vector.request.data.EmbeddedText;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.request.ranker.BaseRanker;
import io.milvus.v2.service.vector.request.ranker.WeightedRanker;
import io.milvus.v2.service.vector.response.SearchResp;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class MilvusSearchKit {
    private MilvusClientV2 client;

    private OllamaEmbeddingService embedding;

    private String collectionName;

    private String denseVectorField;

    private String sparseVectorField;

    private List<String> outputFields;

    public MilvusSearchKit(){}

    public MilvusSearchKit(MilvusClientV2 client, OllamaEmbeddingService embedding, String collectionName, String denseVectorField, String sparseVectorField, List<String> outputFields) {
        this.client = client;
        this.embedding = embedding;
        this.collectionName = collectionName;
        this.denseVectorField = denseVectorField;
        this.sparseVectorField = sparseVectorField;
        this.outputFields = outputFields;
    }


    public SearchResp annSearch(String query, Integer topK){
        FloatVec queryVector;
        try {
            queryVector = new FloatVec(embedding.getEmbedding(query));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return client.search(SearchReq.builder()
                .collectionName(collectionName)
                .data(Collections.singletonList(queryVector))
                .annsField(denseVectorField)
                .metricType(IndexParam.MetricType.IP)
                .outputFields(outputFields)
                .topK(topK)
                .build());
    }

    public SearchResp hybridSearch(String query, Integer topK){
        List<BaseVector> queryDenseVectors;
        try {
            queryDenseVectors= Collections.singletonList(new FloatVec(embedding.getEmbedding(query)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<BaseVector> querySparseVectors = Collections.singletonList(new EmbeddedText(query));

        // 配置搜索请求
        List<AnnSearchReq> searchRequests = new ArrayList<>();
        searchRequests.add(AnnSearchReq.builder()
                .vectorFieldName(denseVectorField)  // Field Name
                .vectors(queryDenseVectors) // Query Vector
                .metricType(IndexParam.MetricType.IP) // Inner Product Metric
                .params("{\"nprobe\": 10}") // Search Params
                .topK(topK + 3) // Limit results to top topK + 3
                .build());

        searchRequests.add(AnnSearchReq.builder()
                .vectorFieldName(sparseVectorField) // Field Name
                .vectors(querySparseVectors) // Query Text Vector
                .metricType(IndexParam.MetricType.BM25) // BM25 Metric for sparse
                .params("{}") // No additional parameters for BM25
                .topK(topK + 3) // Limit results to top topK + 3
                .build());

        // 配置rerank策略
        BaseRanker reranker = new WeightedRanker(Arrays.asList(0.8f, 0.3f));

        return client.hybridSearch(HybridSearchReq.builder()
                .collectionName(collectionName)
                .searchRequests(searchRequests)
                .ranker(reranker)
                .topK(topK)
                .consistencyLevel(ConsistencyLevel.BOUNDED)
                .outFields(outputFields)
                .build());
    }
}
