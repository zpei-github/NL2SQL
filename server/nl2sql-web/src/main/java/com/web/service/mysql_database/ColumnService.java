package com.web.service.mysql_database;


import com.web.entity.mysql.standard_database.StandardColumn;
import com.web.mapper.mysql.standard_database.StandardColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnService {

    @Autowired
    StandardColumnMapper stdColMapper;

    public List<StandardColumn> getAllStandardColumn(){
        return stdColMapper.getAllStandardColumn();
    }
    public StandardColumn getStandardColumnByStandardColumnName(String name) {
        return stdColMapper.getStandardColumnByStandardColumnName(name);
    }
    public StandardColumn getStandardColumnByStandardColumnId(Integer standardColumnId) {
        return stdColMapper.getStandardColumnByStandardColumnId(standardColumnId);
    }
}
