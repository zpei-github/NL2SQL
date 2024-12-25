package com.web.entity.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardColumnIndex {
    private Integer standard_column_id;

    private String standard_column_name;

    private String column_comment; // 字段备注

    private String table_schema;
}
