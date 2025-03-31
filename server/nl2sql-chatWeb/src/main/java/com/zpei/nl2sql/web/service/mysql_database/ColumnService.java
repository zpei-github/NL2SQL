/*
 *   Copyright (c) 2024 zpei-github
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.zpei.nl2sql.web.service.mysql_database;


import com.zpei.nl2sql.web.entity.mysql.standard_database.StandardColumn;
import com.zpei.nl2sql.web.mapper.mysql.standard_database.StandardColumnMapper;
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
