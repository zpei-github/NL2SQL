package com.web.entity.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardTableIndex {
    private Integer standard_table_id; // 标准规范表id

    private String standard_table_name; // 标准规范表名

    private String table_comment; // 表备注

    private String table_schema;
}
