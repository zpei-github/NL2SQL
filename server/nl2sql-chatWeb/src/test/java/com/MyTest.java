package com;
import com.web.entity.milvus.StandardTableSchema;
import com.web.service.llm.OllamaEmbeddingService;
import com.web.service.milvus.TableMilvusService;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.response.SearchResp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;

@SpringBootTest
public class MyTest {

    @Autowired
    TableMilvusService tableMilvusService;

    @Autowired
    OllamaEmbeddingService ollamaEmbeddingService;

    @Test
    public void test(){
        System.out.println(tableMilvusService.hybridSearch("产品核查", 2));
    }


    @Test
    public void ollamatest() throws IOException {
        System.out.println(Arrays.toString(ollamaEmbeddingService.getEmbedding("fdsafdsafdsafdsa")));
    }
}
