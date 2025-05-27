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
package com.zpei.nl2sql.graph;
import com.zpei.nl2sql.graph.node.Node;
import com.zpei.nl2sql.graph.node.nodes.FieldNode;
import com.zpei.nl2sql.graph.node.nodes.GranularityNode;
import com.zpei.nl2sql.graph.node.nodes.TableNode;
import com.zpei.nl2sql.graph.self_exceptions.IndexOutOfBoundsException;
import com.zpei.nl2sql.graph.self_exceptions.NoIndexException;
import com.zpei.nl2sql.graph.self_exceptions.TwoNodeOperateException;

import java.util.*;


/**
 * 依据关系型数据库结构构建的数据库图，将表、字段和粒度等概念进行抽象后构建无向带权图，同时实现相关功能。
 * 继承MatrixGraph创建DBGraph
 * @Author zpei
 * @date 2025/3/3
 */

public class DBGraph extends MatrixGraph {
    // 字段与所属表之间的权值定为1L
    public final static Integer FIELD_TABLE_WEIGHT = 1;

    // 如果该粒度就是表的粒度，或者比表的粒度细，则权值定为5L
    public final static Integer FULL_GRANULARITY_TABLE_WEIGHT = 2;

    // 如果该粒度比表的粒度粗，则初始值定为10L
    public final static Integer INITIAL_GRANULARITY_TABLE_WEIGHT = 5;


    // 名字索引节点map
    private final Map<String, FieldNode> fieldIndex;
    private final Map<String, GranularityNode> granularityIndex;
    private final Map<String, TableNode> tableIndex;


    public DBGraph() {
        super();
        fieldIndex = new HashMap<>();
        granularityIndex = new HashMap<>();
        tableIndex = new HashMap<>();
    }

    /** 连接节点时会进行区分
     * 只能进行粒度节点和表节点相连；还有字段节点和表节点相连
     * @param n1
     * @param n2
     * @param weight
     * @return
     * @author zpei
     * @create 2024/12/9
     **/
    @Override
    public boolean link(Node n1, Node n2 , Integer weight) throws IndexOutOfBoundsException, NoIndexException, TwoNodeOperateException {
        if (n1 == null || n2 == null || weight == null) return false;

        int n1_mark = -1;
        int n2_mark = -1;


        // 两数个集合数相加需要得出不一样的结果
        if((n1 instanceof FieldNode)){
            n1_mark = 1;
        }else if((n1 instanceof TableNode)){
            n1_mark = 3;
        } else if((n1 instanceof GranularityNode)){
            n1_mark = 8;
        }

        if((n2 instanceof FieldNode)){
            n2_mark = 2;
        } else if((n2 instanceof TableNode)){
            n2_mark = 5;
        } else if((n2 instanceof GranularityNode)){
            n2_mark = 13;
        }

        return switch (n1_mark + n2_mark) {
            case 5 -> link_field_table((TableNode) n1, (FieldNode) n2, weight);
            case 6 -> link_field_table((TableNode) n2, (FieldNode) n1, weight);
            case 10 -> link_field_granularity((FieldNode) n2, (GranularityNode) n1, weight);
            case 13 -> link_table_granularity((TableNode) n2, (GranularityNode) n1, weight);
            case 14 -> link_field_granularity((FieldNode) n1, (GranularityNode) n2, weight);
            case 16 -> link_table_granularity((TableNode) n1, (GranularityNode) n2, weight);
            default -> false;
        };
    }


    /** 添加一个节点
     * 在添加时会进行区分
     * @param n
     * @return
     * @author zpei
     * @create 2024/12/11
     **/
    public boolean add(Node n){
        if (n == null) return false;
        if(n instanceof FieldNode){
            return addFieldNode((FieldNode) n);
        }else if(n instanceof TableNode){
            return addTableNode((TableNode) n);
        }else if(n instanceof GranularityNode){
            return addGranularityNode((GranularityNode) n);
        }else return false;
    }

    /** 移除一个节点
     * 会进行区分
     * @param n
     * @return
     * @author zpei
     * @create 2024/12/11
     **/
    public boolean remove(Node n) throws NoIndexException, IndexOutOfBoundsException{
        if (n == null) return false;
        if(n instanceof FieldNode){
            return removeFieldNode((FieldNode) n);
        } else if(n instanceof TableNode){
            return removeTableNode((TableNode) n);
        } else if(n instanceof GranularityNode){
            return removeGranularityNode((GranularityNode) n);
        }
        return false;
    }

    /** 连接表和字段
     * 当连接的是表和字段时，需要特殊处理。具体来说字段节点不会allocateIndex分配索引，而是仅做保存；
     * 但是会将对应的表节点map存储
     * @param table
     * @param field
     * @param weight
     * @return
     * @author zpei
     * @create 2024/12/8
     **/
    public boolean link_field_table(TableNode table, FieldNode field , Integer weight){
        if(!fieldIndex.containsKey(field.getFieldName()) || !node2index.containsKey(table)) return false;

        // 添加映射
        if(field.addTable(table)){
            if(!table.addField(field)){
                field.removeTable(table);
                return false;
            }else {
                return true;
            }
        }else {
            return false;
        }
    }


    /** 连接表和粒度
     * 当粒度没有设置包含字段或者表没有定义粒度时，将不会被添加至图中
     * @param table
     * @param gran
     * @param weight
     * @return
     * @author zpei
     * @create 2024/12/9
     **/
    public boolean link_table_granularity(TableNode table, GranularityNode gran, Integer weight) throws IndexOutOfBoundsException, NoIndexException, TwoNodeOperateException {
        return super.link(table, gran, weight);
    }



    /** 将字段添加至粒度的Set
     *
     * @param field
     * @param gran
     * @param weight
     * @return
     * @author zpei
     * @create 2025/3/28
     **/
    public boolean link_field_granularity(FieldNode field, GranularityNode gran, Integer weight){
        return gran.addField(field);
    }


    /** 向图索引中添加字段节点
     * 如果该节点为空，或者已经存在该节点则添加失败
     *
     * @param node
     * @return 是否成功添加
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean addFieldNode(FieldNode node) {
        if (node == null) return false;

        if(node.getFieldName() == null || fieldIndex.containsKey(node.getFieldName())) return false;

        //已经添加过的字段节点不再添加
        fieldIndex.put(node.getFieldName(), node);
        return true;
    }


    /**
     * 根据字段名获取字段节点
     *
     * @param name
     * @return 目标Node
     * @author zpei
     * @create 2024/12/5
     **/
    public FieldNode findFieldNode(String name) {
        return fieldIndex.get(name);
    }


    /** 移除一个字段节点
     *
     * @param node
     * @return
     * @author zpei
     * @create 2024/12/11
     **/
    public boolean removeFieldNode(FieldNode node) {
        if (node == null || node.getFieldName() == null) return false;
        return fieldIndex.remove(node.getFieldName()) != null;
    }


    /**
     * 向图索引中添加表节点
     * 如果已经存在该节点，或者该节点为空则添加失败
     *
     * @param node
     * @return 是否成功添加
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean addTableNode(TableNode node){
        if (node == null || node.getTableName() == null) return false;
        if(tableIndex.containsKey(node.getTableName())) return true;

        allocateIndex(node);

        tableIndex.put(node.getTableName(), node);
        return true;
    }

    /** 移除一个表节点
     *
     * @param node
     * @return
     * @author zpei
     * @create 2024/12/11
     **/
    public boolean removeTableNode(TableNode node) throws NoIndexException, IndexOutOfBoundsException {
        if (node == null || node.getTableName() == null) return false;

        if(removeNode(node)) {
                return tableIndex.remove(node.getTableName()) != null;
        }
        return false;

    }


    /**
     * 根据表名获取表节点
     *
     * @param name
     * @return 目标Node
     * @author zpei
     * @create 2024/12/5
     **/
    public TableNode findTableNode(String name) {
        return tableIndex.get(name);
    }


    /**
     * 向图索引中添加粒度节点
     * 如果已经存在该节点，或者该节点为空则添加失败
     *
     * @param node
     * @return 是否成功添加
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean addGranularityNode(GranularityNode node){
        if (node == null) return false;
        if(node.getGranularityName() == null || granularityIndex.containsKey(node.getGranularityName())) return false;

        if(allocateIndex(node) >= 0){
            granularityIndex.put(node.getGranularityName(), node);
            return true;
        }else {
            return false;
        }
    }

    /** 移除一个粒度节点
     *
     * @param node
     * @return
     * @author zpei
     * @create 2024/12/11
     **/
    public boolean removeGranularityNode(GranularityNode node) throws NoIndexException, IndexOutOfBoundsException{
        if (node == null || node.getGranularityName() == null) return false;
        if(removeNode(node)){
            return granularityIndex.remove(node.getGranularityName()) != null;
        }
        return false;
    }

    /**
     * 根据粒度名获取粒度节点
     *
     * @param name
     * @return 目标Node
     * @author zpei
     * @create 2024/12/5
     **/
    public GranularityNode findGranularityNode(String name) {
        return granularityIndex.get(name);
    }


    /** 当图添加节点完毕之后需要连接所有粒度和具有相关字段的表
     * 若粒度g1，g1包含f1和f2两个字段。需要将g1连接至所有包含f1和f2的表t2, t3, t4...
     * 连接时的权重定义需要动态定义。如g1和t2的粒度相同，则权重很低；若t1和t2根据g1关联之后产生表规模扩张很大，则权重较高
     * @return
     * @author zpei
     * @create 2024/12/9
     **/
    @Override
    public boolean initialize() {
        super.initialize();

        // 表-粒度字段信息映射：value[0]判断表字段与粒度字段的交集个数, value[1]判断粒度字段属于表的联合主键字段的个数
        Map<TableNode, int[]> tableCounts = new HashMap<>();

        for (Map.Entry<String, GranularityNode> grans : granularityIndex.entrySet()) {
            GranularityNode gran = grans.getValue();
            int fieldCount = gran.fieldCount();

            // 获取粒度包含的字段连接的表
            for (Node field : gran.getFields()) {

                Set<Node> tables = ((FieldNode) field).getTables();
                for(Node node : tables){
                    TableNode table = (TableNode) node;
                    GranularityNode t_gran = (GranularityNode) table.getGranularity();

                    // 如果表的粒度和该粒度恰好是一样的，则进行满连接
                    if(table.getGranularity().equals(gran)){
                        link_table_granularity(table, gran, FULL_GRANULARITY_TABLE_WEIGHT);
                        continue;
                    }

                    if(tableCounts.containsKey(table)){
                        tableCounts.get(table)[0] ++;
                    }else
                        tableCounts.put(table, new int[]{1,0});

                    if(t_gran.getFields().contains(field)){
                        tableCounts.get(table)[1] += 1;
                    }
                }
            }

            // 将粒度节点与包含有粒度字段的表进行连接
            for(Map.Entry<TableNode, int[]> tables : tableCounts.entrySet()){
                int[] counts = tables.getValue();
                if(counts[0] == fieldCount ){
                    link_table_granularity(tables.getKey(), gran,  (int)(FULL_GRANULARITY_TABLE_WEIGHT + 1 + Math.exp(9 - fieldCount -counts[1])));
                }
            }
            tableCounts.clear();
        }
        return true;
    }


    /** 倒排索引确认需要求解的表
     * 在建立好的图中，求解最小斯坦纳树应该主要关注表节点和粒度节点，没有关联关系的字段都需要通过倒排索引聚集到数量较少的几张表中
     * 多个字段节点可能指向同一张表，因此通过倒排索引递归得出包含字段最多的单张表，从而大大降低斯坦树求解的关键节点数量
     * @param nodes
     * @return 求解的表set
     * @author zpei
     * @create 2024/12/12
     **/
    public Set<Node> fieldInvertToTable(Set<Node> nodes){
        if(nodes == null || nodes.isEmpty()) return null;

        // 倒排索引后得到的表节点集合
        Set<Node> keyNodes = new HashSet<>();

        // 倒排索引-打分表
        HashMap<Node, Double> score = new HashMap<>();

        // 挑出关键字集合中的表和粒度节点
        for(Node node : nodes){
            if(node instanceof TableNode || node instanceof GranularityNode){
                keyNodes.add(node);
            }
        }

        for(Node node : keyNodes){
            nodes.remove(node);
            if(node instanceof TableNode t){
                if(t.getFields() == null || t.getFields().isEmpty()) continue;
                nodes.removeIf(t.getFields()::contains);
            }
        }

        return invertedIndex(keyNodes, nodes, fieldIndex, score);
    }


    /** 递归倒排索引打分获取目标表节点
     * 倒排索引打分可以自定义
     * @param keyNodes 输出的关键节点
     * @param nodes 输入的源节点
     * @param fieldIndex 字段名索引
     * @param score 分数暂存记录
     * @return 关键节点集合
     * @author zpei
     * @create 2025/3/9
     **/
    private Set<Node> invertedIndex(Set<Node> keyNodes,  Set<Node> nodes, Map<String, FieldNode> fieldIndex,  HashMap<Node, Double> score){
        if(nodes.isEmpty()) return keyNodes;

        double max = 0d;
        Node maxTable = null;

        Iterator<Node> iter = nodes.iterator();
        while (iter.hasNext()) {
            Node f = iter.next();
            if(!(f instanceof FieldNode field)) continue;

            if(field.getFieldName() == null
                    || field.getTables() == null
                    || !fieldIndex.containsKey(field.getFieldName())
                    || field.getTables().isEmpty()) {
                iter.remove();
                continue;
            }

            // 将字段节点涉及到的表添加到打分表中
            for(Node table : field.getTables()){
                if(!score.containsKey(table)){
                    score.put(table, 0d);
                }
                //数量提升的基础分10分
                double s = 10d;

                TableNode t1 = (TableNode) table;

                // 倒排索引打分策略1: 如果字段属于表粒度，则加2分
                GranularityNode granOfT1 = (GranularityNode) t1.getGranularity();
                if(granOfT1.getFields() != null && granOfT1.getFields().contains(field)) s += 2d;

                // 倒排索引打分策略2: 数据量评价

                s += t1.getRowCount() == null ? 0d : Math.log10(t1.getRowCount()) / 10;

                score.put(table, score.get(table) + s);

                if(score.get(table) > max){
                    max = score.get(table);
                    maxTable = table;
                }
            }
        }

        if(maxTable != null){
            Set<Node> fieldOfTable = ((TableNode)maxTable).getFields();
            if(fieldOfTable != null && !fieldOfTable.isEmpty()){
                nodes.removeIf(fieldOfTable::contains);
                keyNodes.add(maxTable);
            }
        }

        score.clear();
        return invertedIndex(keyNodes, nodes, fieldIndex, score);
    }


    /** 打印字段与表关系
     *
     * @return
     * @author zpei
     * @create 2024/12/12
     **/
    public void printFields(){
        for(Map.Entry<String, FieldNode> entry : fieldIndex.entrySet()){
            FieldNode field =  entry.getValue();
            System.out.print(field.getFieldName() + " <--> ");
            for(Node node : field.getTables()){
                TableNode table = (TableNode) node;
                System.out.print(table + " ");
            }
            System.out.println();
        }
    }
}
