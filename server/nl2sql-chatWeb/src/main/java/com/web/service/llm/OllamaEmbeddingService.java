package com.web.service.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.configurer.OllamaEmbeddingConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OllamaEmbeddingService {

    private final OllamaEmbeddingConfig ollamaConfig;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 获取文本嵌入向量
    public float[] getEmbedding(String text) throws IOException {
        // 构建请求体
        RequestBody requestBody = RequestBody.create(
                objectMapper.writeValueAsString(new EmbedRequest(ollamaConfig.getModel(), text)),
                MediaType.parse("application/json")
        );

        // 创建请求
        Request request = new Request.Builder()
                .url(ollamaConfig.getUrl())
                .post(requestBody)
                .build();

        // 发送请求并解析响应
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Ollama 请求失败: " + response.code() + " - " + response.message());
            }
            return objectMapper.readValue(response.body().string(), EmbedResponse.class).getEmbedding();
        }
    }

    // 内部类：请求体结构
    @Data
    private static class EmbedRequest {
        private final String model;
        private final String prompt;
    }

    // 内部类：响应体结构
    @Data
    private static class EmbedResponse {
        private float[] embedding;
    }
}
