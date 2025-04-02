package com.zpei.nl2sql.web.entity.milvus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardTableSchema {
    @Id
    private Integer standard_table_id;

    private String standard_table_name;

    private String original_table_name;

    private String table_comment;

    private String table_schema;
}
