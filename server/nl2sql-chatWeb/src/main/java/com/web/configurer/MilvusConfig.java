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
package com.web.configurer;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "milvus")
public class MilvusConfig {
    String url;
    String token;

    /*
    *  milvus-sdk-java和mysql-connector-java两个依赖会产生Protobuf依赖冲突。导致milvus的服务无法连接
     * 需要将mysql-connector-java中的<groupId>com.google.protobuf</groupId><artifactId>protobuf-java</artifactId>
    * 排除才能使Milvus运行，经过测试Milvus的Protobuf可以支撑mysql运行
    * */
    @Bean
    public MilvusClientV2 getClient() {
        ConnectConfig connectConfig = ConnectConfig.builder()
                .uri(url)
                .token(token)
                .build();
        return new MilvusClientV2(connectConfig);
    }
}
