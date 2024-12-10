package com.graph;

import com.node.Node;
import com.node.entity.FieldNode;
import com.node.entity.GranularityNode;
import com.node.entity.TableNode;
import java.util.*;


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

    /** 字段-表map
     * key 为字段，List为存在该字段的表的集合
     */
    private final Map<Node, List<Node>> field_tables;


    public DBGraph() {
        super();
        fieldIndex = new HashMap<>();
        granularityIndex = new HashMap<>();
        tableIndex = new HashMap<>();
        field_tables = new HashMap<>();
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
    public boolean link(Node n1, Node n2 , Integer weight){
        if (n1 == null || n2 == null || weight == null) return false;

        if (n1.getClass().equals(FieldNode.class) && n2.getClass().equals(TableNode.class)) {
            return link_field_table((TableNode)n2,  (FieldNode) n1, weight);
        } else if (n2.getClass().equals(FieldNode.class)  && n1.getClass().equals(TableNode.class)) {
            return link_field_table((TableNode)n1,  (FieldNode) n2, weight);
        } else if (n2.getClass().equals(GranularityNode.class) && n1.getClass().equals(TableNode.class)) {
            return link_table_granularity((TableNode)n1,  (GranularityNode)n2, weight);
        } else if (n1.getClass().equals(GranularityNode.class) && n2.getClass().equals(TableNode.class)) {
            return link_table_granularity((TableNode)n2,  (GranularityNode)n1, weight);
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
    private boolean link_field_table(TableNode table, FieldNode field , Integer weight){
        if(!field_tables.containsKey(field) || !node2index.containsKey(table)
        ) return false;

        // 添加映射
        field_tables.get(field).add(table);
        return true;
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
    private boolean link_table_granularity(TableNode table, GranularityNode gran, Integer weight){
        // 当粒度没有设置包含字段或者表没有定义粒度时，将不会被添加至图中
        if(gran.fieldCount() == 0 || table.getGranularity() == null
        ){
            return false;
        }
        return super.link(table, gran, weight);
    }


    /** 向图索引中添加字段节点
     * 如果该节点为空，或者已经存在该节点则添加失败
     *
     * @param n
     * @return 是否成功添加
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean addFieldNode(Node n) {
        if (n == null || !n.getClass().equals(FieldNode.class)) return false;

        FieldNode node = (FieldNode)n;
        if(node.getFieldName() == null ) return false;
        fieldIndex.put(node.getFieldName(), node);

        // 给字段新建一个映射List
        field_tables.put(node, new ArrayList<>());
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


    /**
     * 向图索引中添加表节点
     * 如果已经存在该节点，或者该节点为空则添加失败
     *
     * @param n
     * @return 是否成功添加
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean addTableNode(Node n) {
        if (n == null || !n.getClass().equals(TableNode.class)) return false;

        TableNode node = (TableNode)n;
        if(node.getTableName() == null ) return false;

        tableIndex.put(node.getTableName(), node);
        return allocateIndex(n) >= 0;
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
     * @param n
     * @return 是否成功添加
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean addGranularityNode(Node n) {
        if (n == null || !n.getClass().equals(GranularityNode.class)) return false;

        GranularityNode node = (GranularityNode)n;
        if(node.getGranularityName() == null ) return false;

        granularityIndex.put(node.getGranularityName(), node);
        return allocateIndex(n) >= 0;
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
    public boolean compute() {
        Map<TableNode, Integer> tableCounts = new HashMap<>();
        for (Map.Entry<String, GranularityNode> grans : granularityIndex.entrySet()) {
            int fieldCount = grans.getValue().fieldCount();
            GranularityNode gran = grans.getValue();

            // 获取粒度包含的字段连接的表
            Iterator<Node> fields = gran.getFields().iterator();
            while (fields.hasNext()) {
                List<Node> tables = field_tables.get(fields.next());
                for(Node node : tables){
                    TableNode table = (TableNode) node;

                    // 如果表的粒度和该粒度恰好是一样的，则进行满连接
                    if(table.getGranularity().equals(gran)){
                        link_table_granularity(table, gran, FULL_GRANULARITY_TABLE_WEIGHT);
                        continue;
                    }

                    if(tableCounts.containsKey(table)){
                        tableCounts.put(table, tableCounts.get(table) + 1);
                    }else
                        tableCounts.put(table, 1);
                }
            }

            // 将粒度节点与包含有相关字段的表进行连接
            for(Map.Entry<TableNode, Integer> tables : tableCounts.entrySet()){
                if(tables.getValue().equals(fieldCount) ){
                    link_table_granularity(tables.getKey(), gran, INITIAL_GRANULARITY_TABLE_WEIGHT + (2 << (6 - fieldCount)));
                }
            }
            tableCounts.clear();
        }
        return true;
    }
}
