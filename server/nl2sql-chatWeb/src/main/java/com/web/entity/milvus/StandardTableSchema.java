package com.web.entity.milvus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardTableSchema {
    private Integer standard_table_id;

    private String standard_table_name;

    private String original_table_name;

    private String table_comment;

    private String table_schema;
}
