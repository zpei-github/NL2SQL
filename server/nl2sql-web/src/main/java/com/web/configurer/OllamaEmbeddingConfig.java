package com.web.configurer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ollama.embedding")
public class OllamaEmbeddingConfig {
    private String url;
    private String model;
}
