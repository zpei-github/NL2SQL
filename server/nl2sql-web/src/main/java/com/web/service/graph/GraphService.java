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
package com.web.service.graph;

import com.graph.DBGraph;
import com.minimal_steiner_tree.MSTree;
import com.node.nodes.FieldNode;
import com.node.nodes.GranularityNode;
import com.node.nodes.TableNode;
import com.web.entity.mysql.standard_database.*;
import com.web.mapper.elasticserach.StandardColumnRepository;
import com.web.mapper.elasticserach.StandardTableRepository;
import com.web.mapper.mysql.standard_database.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/** 图生成
 *
 */

@Service
public class GraphService {

    @Getter
    private final DBGraph graph;

    @Getter
    private final MSTree treeSolver;

    @Autowired
    private StandardColumnMapper columnMapper;

    @Autowired
    private StandardTableMapper tableMapper;

    @Autowired
    private GranularityMapper granularityMapper;

    @Autowired
    private StandardColumnTableMapper colTableMapper;

    @Autowired
    private StandardGranularityColumnMapper granColMapper;

    @Autowired
    private StandardTableRepository tableRepo;

    @Autowired
    private StandardColumnRepository colRepo;

    private GraphService() {
        graph = new DBGraph();
        treeSolver = new MSTree();
    }

    private volatile static GraphService instance;

    // 单例模式保证全局只有一个图服务
    public static GraphService getInstance() {
        if (instance == null) {
            synchronized (GraphService.class) {
                if (instance == null) {
                    instance = new GraphService();
                }
            }
        }
        return instance;
    }

    // 图初始化
    public void initialize() {
        if(!addColumns()) return;
        if(!addGranularity()) return;
        if(!addTablesAndLinkGranTable()) return;
        if(!linkColTables()) return;

        graph.compute();
        treeSolver.initial(graph);
    }

    @PostConstruct
    public void postConstruct() {
        // 在bean初始化后自动调用initialize()方法
        initialize();
    }

    private boolean addColumns() {
        List<StandardColumn> columns = columnMapper.getAllStandardColumn();
        if (columns == null || columns.isEmpty()) { return false; }
        for (StandardColumn column : columns) {
            // 图添加字段
            graph.add(new FieldNode(column.getStandardColumnName()));

        }
        return true;
    }

    // 需要先添加字段才能再添加粒度
    private boolean addGranularity() {
        List<Granularity> grans = granularityMapper.getAllGranularity();
        List<StandardGranularityColumn> granCols = granColMapper.getAllStandardGranularityColumn();
        if (grans == null|| granCols == null || grans.isEmpty() || granCols.isEmpty()) return false;

        for (Granularity gran : grans) {
            graph.add(new GranularityNode(gran.getGranularitName()));
        }
        for (StandardGranularityColumn granCol : granCols) {
            graph.findGranularityNode(granCol.getGranularityName()).addField(new FieldNode(granCol.getStandardColumnName()));
        }
        return true;
    }

    // 需要先添加粒度才能再添加表
    private boolean addTablesAndLinkGranTable() {
        List<StandardTable> tables = tableMapper.getAllStandardTables();
        if (tables == null || tables.isEmpty()) return false;
        for (StandardTable table : tables) {
            // 图添加表
            TableNode tableNode = new TableNode(table.getStandardTableName());
            tableNode.setGranularity(graph.findGranularityNode(table.getGranularityName()));
            graph.add(tableNode);

            // 连接粒度和表
            graph.link(tableNode, graph.findGranularityNode(table.getGranularityName()), DBGraph.FULL_GRANULARITY_TABLE_WEIGHT);
        }
        return true;
    }

    private boolean linkColTables() {
        List<StandardColumnTable> colTables = colTableMapper.getAllStandardColumnTable();

        if (colTables == null || colTables.isEmpty()) return false;
        for (StandardColumnTable colTable : colTables) {
            graph.link(graph.findFieldNode(colTable.getStandardColumnName()), graph.findTableNode(colTable.getStandardTableName()), 0);
        }
        return true;
    }
}
