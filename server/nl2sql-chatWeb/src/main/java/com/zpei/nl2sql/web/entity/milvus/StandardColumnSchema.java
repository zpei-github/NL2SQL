package com.zpei.nl2sql.web.entity.milvus;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardColumnSchema {
    @Id
    private Integer standard_column_id; // 标准字段id

    private String standard_column_name; // 标准字段名

    private String original_column_name; // 源库字段名

    private String column_comment; // 字段备注

    private String table_schema; // 源库名
}
