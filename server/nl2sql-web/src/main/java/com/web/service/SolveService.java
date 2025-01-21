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
package com.web.service;

import com.graph.Edge;
import com.graph.node.Node;
import com.graph.node.nodes.FieldNode;
import com.graph.node.nodes.GranularityNode;
import com.graph.node.nodes.TableNode;
import com.web.service.elasticsearch.ElasticsearchService;
import com.web.service.graph.GraphComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class SolveService {
    @Autowired
    private  ElasticsearchService esService;

    @Autowired
    private  GraphComponent gComponent;


    private static final Logger logger = LoggerFactory.getLogger(GraphComponent.class);

    /** 通过传入的字段和表关键字，经过算法计算得出最适合的连接方式
     *
     * @param keyWords 传入的关键字，用于搜索关键表节点和关键字段节点。keywords["table"]中是用户输入的表的关键字，
     *                 keywords["column"]是用户输入的字段关键字，后续会使用关键字搜索表名和字段名
     * @return 返回一个Map，其中key值是粒度节点中的字段名List，value值是通过该粒度连接的表名List
     * @author zpei
     * @create 2025/1/11
     **/
    public Map<List<String>, List<String>> getMSTree(Map<String, List<String>> keyWords) {
        Map<List<String>, List<String>> result = new HashMap<>();
        Set<Node> keyNodes = new HashSet<>();

        // 用于得出使用相同粒度的表
        Map<GranularityNode, List<TableNode>> filter = new HashMap<>();

        List<String> tableKeyword = keyWords.get("table");
        List<String> columnKeyword = keyWords.get("column");

        for(String table :tableKeyword){
            String tableName = esService.searchByTableComment("standard_table_index", table).get(0).getStandard_table_name();
            logger.info(tableName);
            gComponent.addKeyNodes(keyNodes, "table", tableName);
        }

        for(String column :columnKeyword){
            String columnName = esService.searchByColumnComment("standard_column_index", column).get(0).getStandard_column_name();
            logger.info(columnName);
            gComponent.addKeyNodes(keyNodes, "column", columnName);
        }

        for(Edge edge : gComponent.getMSTree(gComponent.fieldInvertedTable(keyNodes))){
            logger.info(edge.toString());
            TableNode table = (TableNode)edge.getStart();
            GranularityNode gran = (GranularityNode)edge.getEnd();
            if(!filter.containsKey(gran)){
                filter.put(gran, new ArrayList<>());
            }
            filter.get(gran).add(table);
        }

        for(Map.Entry<GranularityNode, List<TableNode>> entry : filter.entrySet()){
            List<String> columns = new LinkedList<>();
            List<String> tables = new LinkedList<>();

            for(Node coulum : entry.getKey().getFields()){
                FieldNode field = (FieldNode)coulum;
                columns.add(field.getOriginalName());
            }

            for(TableNode table : entry.getValue()){
                tables.add(table.getOriginalName());
            }
            result.put(columns, tables);
        }
        return result;
    }
}
