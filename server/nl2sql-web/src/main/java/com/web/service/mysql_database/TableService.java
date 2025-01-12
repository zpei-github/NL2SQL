package com.web.service.mysql_database;

import com.web.entity.mysql.standard_database.StandardTable;
import com.web.mapper.mysql.standard_database.StandardTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableService {

    @Autowired
    StandardTableMapper stTableMapper;

    public StandardTable getTableByOriginalTableName(String tableName) {
        return stTableMapper.getStandardTableByOriginalTableName(tableName);
    }
}
