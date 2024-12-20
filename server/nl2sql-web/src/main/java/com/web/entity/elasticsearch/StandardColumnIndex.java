package com.web.entity.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "standardColumnIndex")
public class StandardColumnIndex {

    @Id
    private Integer standardColumnId;

    private String standardColumnName;

    private String columnComment; // 字段备注
}
