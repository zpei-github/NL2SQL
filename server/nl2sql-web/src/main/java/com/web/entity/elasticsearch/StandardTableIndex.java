package com.web.entity.elasticsearch;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "StandardTableIndex")
public class StandardTableIndex {

    @Id
    private Integer standardTableId; // 标准规范表id

    private String standardTableName; // 标准规范表名

    private String tableComment; // 表备注

}
