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
package com.zpei.nl2sql.web.service.graph;

import com.zpei.nl2sql.graph.DBGraph;
import com.zpei.nl2sql.graph.Edge;
import com.zpei.nl2sql.minimal_steiner_tree.MSTree;
import com.zpei.nl2sql.graph.node.Node;
import com.zpei.nl2sql.graph.node.nodes.FieldNode;
import com.zpei.nl2sql.graph.node.nodes.GranularityNode;
import com.zpei.nl2sql.graph.node.nodes.TableNode;
import com.zpei.nl2sql.web.entity.mysql.standard_database.*;
import com.zpei.nl2sql.web.mapper.mysql.standard_database.*;
import com.zpei.nl2sql.web.my_exception.InitializeException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 图生成
 *
 */
@Service
@Component
public class GraphComponent {

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

    private static final Logger logger = LoggerFactory.getLogger(GraphComponent.class);

    // 通过构造函数注入
    @Autowired
    public GraphComponent(DBGraph graph, MSTree treeSolver) {
        logger.info("GraphService Constructor initializing...");
        this.graph = graph;
        this.treeSolver = treeSolver;
        logger.info("GraphService Constructor initialized...");
    }

    // 图初始化
    public void initialize() {
        logger.info("GraphService initializing...");

        // 为了方便直接结束，但是应该使用异常处理
        if(!addColumns()) throw new InitializeException("导入standard_column库出现异常");
        if(!addGranularity()) throw new InitializeException("导入standard_granularity库出现异常");
        if(!addTablesAndLinkGranTable()) throw new InitializeException("导入standard_table库和连接granularity_table出现异常");
        if(!linkColTables()) throw new InitializeException("连接column与table出现异常");

        try{
            graph.initialize();
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new InitializeException("图初始化异常");
        }
        treeSolver.initialize(graph);

        logger.info("GraphService initialized...");
    }

    // 服务启动之后初始化图
    @PostConstruct
    public void postConstruct() {
        logger.info("Initializing graph...");
        // 在bean初始化后自动调用initialize()方法
        initialize();
        logger.info("Initialized graph...");
    }

    // 添加字段节点
    private boolean addColumns() {
        List<StandardColumn> columns = columnMapper.getAllStandardColumn();
        if (columns == null || columns.isEmpty())  return false;
        for (StandardColumn column : columns) {
            if(column == null)  continue;
            FieldNode fieldNode = new FieldNode(column.getStandard_column_name());
            fieldNode.setOriginalName(column.getOriginal_column_name());
            // 图添加字段
            graph.add(fieldNode);
        }
        return true;
    }

    // 添加粒度节点，需要先添加字段才能再添加粒度
    private boolean addGranularity() {
        List<Granularity> grans = granularityMapper.getAllGranularity();
        List<StandardGranularityColumn> granCols = granColMapper.getAllStandardGranularityColumn();
        if (grans == null|| granCols == null || grans.isEmpty() || granCols.isEmpty()) return false;

        for (Granularity gran : grans) {
            if(gran == null)  continue;
            graph.add(new GranularityNode(gran.getGranularity_name()));
        }

        for (StandardGranularityColumn granCol : granCols) {

            if(granCol == null)  continue;
            GranularityNode gran = graph.findGranularityNode(granCol.getGranularity_name());
            if(gran == null)  {
                continue;
            }
            graph.link_field_granularity(graph.findFieldNode(granCol.getStandard_column_name()), gran, 0);
        }
        return true;
    }

    // 添加表节点并连接粒度和表，需要先添加粒度才能再添加表
    private boolean addTablesAndLinkGranTable() {
        List<StandardTable> tables = tableMapper.getAllStandardTables();
        if (tables == null || tables.isEmpty()) return false;
        for (StandardTable table : tables) {
            if(table == null)  continue;
            // 图添加表
            TableNode tableNode = new TableNode(table.getStandard_table_name());
            tableNode.setOriginalName(table.getOriginal_table_name());
            tableNode.setGranularity(graph.findGranularityNode(table.getGranularity_name()));
            tableNode.setRowCount(table.getColumn_rows());
            graph.add(tableNode);

            try{
                // 连接粒度和表
                graph.link_table_granularity(tableNode, graph.findGranularityNode(table.getGranularity_name()), DBGraph.FULL_GRANULARITY_TABLE_WEIGHT);
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }
        return true;
    }

    // 连接字段和表
    private boolean linkColTables() {
        List<StandardColumnTable> colTables = colTableMapper.getAllStandardColumnTable();

        if (colTables == null || colTables.isEmpty()) return false;
        for (StandardColumnTable colTable : colTables) {
            if(colTable == null)  continue;
            try{
                graph.link_field_table(graph.findTableNode(colTable.getStandard_table_name()), graph.findFieldNode(colTable.getStandard_column_name()), 0);
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }
        return true;
    }

    /** 根据节点类型和节点名称，搜索出指定节点并添加到关键节点中
     *
     * @param keyNodes
     * @param type
     * @param nodeName
     * @return 返回添加后的关键节点集合
     * @author zpei
     * @create 2025/1/8
     **/
    public Set<Node> addKeyNodes(Set<Node> keyNodes, String type, String nodeName) {
        if(keyNodes == null) keyNodes = new HashSet<>();
        Node aim = null;
        if("table".equals(type)) {
            aim = graph.findTableNode(nodeName);
        }else if("column".equals(type)) {
            aim = graph.findFieldNode(nodeName);
        }
        keyNodes.add(aim);
        return keyNodes;
    }


    /**
     * 判断某个字段是否属于某个表
     * @param column 字段
     * @param table 表
     * @return 是否属于
     * @author zpei
     * @create 2025/4/10
     **/
    public boolean isFieldBelongToTable(StandardColumn column, StandardTable table) {
        if(column == null
                || table == null
                || column.getStandard_column_name() == null
                || table.getStandard_table_name() == null)  return false;

        TableNode tableNode = graph.findTableNode(table.getStandard_table_name());
        FieldNode fieldNode = graph.findFieldNode(column.getStandard_column_name());
        if(fieldNode == null
                || tableNode == null
                || fieldNode.getTables() == null
                || tableNode.getFields() == null) return false;
        return fieldNode.getTables().contains(tableNode) && tableNode.getFields().contains(fieldNode);
    }

    // 倒排索引获取需要求解的表
    private Set<Node> fieldInvertedTable(Set<Node> nodes){
        return graph.fieldInvertToTable(nodes);
    }

    /** 根据表名和字段名解出最小斯坦纳树
     *
     * @param  standardTables 标准表
     * @param  standardColumns 标准字段
     * @return 最小斯坦树的边集合。边的起点是表节点，终点是粒度节点
     * @author zpei
     * @create 2025/1/8
     **/
    public Map<List<String>, List<String>> getMSTree(List<StandardTable> standardTables, List<StandardColumn> standardColumns){
        // key值是表列表，value是key值中表连接所需的共同的字段
        Map<List<String>, List<String>> result = new HashMap<>();
        Set<Node> keyNodes = new HashSet<>();

        // 用于得出使用相同粒度的表
        Map<GranularityNode, List<TableNode>> filter = new HashMap<>();

        for(StandardTable table :standardTables){
            this.addKeyNodes(keyNodes, "table", table.getStandard_table_name());
        }

        for(StandardColumn column : standardColumns){
            this.addKeyNodes(keyNodes, "column", column.getStandard_column_name());
        }


        for(Edge edge : treeSolver.solve(this.fieldInvertedTable(keyNodes))){
            TableNode table = (TableNode)edge.getStart();
            GranularityNode gran = (GranularityNode)edge.getEnd();
            if(table.getTableName() == null && gran.getGranularityName() == null){
                continue;
            }
            if(!filter.containsKey(gran)){
                filter.put(gran, new ArrayList<>());
            }
            filter.get(gran).add(table);
        }

        // 将图信息整理为Map<List<String>, List<String>>格式
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
