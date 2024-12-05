package com.graph;

import com.node.Node;
import com.node.entity.FieldNode;
import com.node.entity.GranularityNode;
import com.node.entity.TableNode;

import java.util.HashMap;
import java.util.Map;

public class DBGrapgh implements Graph {
    private Map<String, FieldNode> fieldIndex;
    private Map<String, GranularityNode> granularityIndex;
    private Map<String, TableNode> tableIndex;

    public DBGrapgh() {
        fieldIndex = new HashMap<>();
        granularityIndex = new HashMap<>();
        tableIndex = new HashMap<>();
    }

    /**
     * 连接两个节点并赋权值
     * 权值一定不能为空。如果成功连接两个节点并赋权值则返回true, 否则返回false
     *
     * @param n1
     * @param n2
     * @param weight
     * @return 是否成功连接
     * @author xinggang
     * @create 2024/12/5
     **/
    @Override
    public boolean link(Node n1, Node n2, Long weight) throws Exception{
        if(n1 == null || n2 == null || weight == null) return false;

        // 只要存在一个节点与另一个节点无法连接上，则连接失败
        if(!n1.addNeighbour(n2, weight)) return false;

        // n2连接n1失败后，需要将n2从n1邻居中删除，确保一致性
        if(!n2.addNeighbour(n1, weight)) {
            if(!n1.removeNeighbour(n2)){
                throw new Exception("节点添加无法回滚");
            }
            return false;
        }
        return true;
    }
    /**
     * 获取指定两个节点之间的权重
     *
     * @param n1
     * @param n2
     * @return 权重值, 如果没有连接则返回null
     * @author zpei
     * @create 2024/12/5
     **/
    @Override
    public Long getWeight(Node n1, Node n2) {
        if(n1 == null || n2 == null) return null;
        return n2.getWeight(n1);
    }

    /**
     * 判断两个节点是否连接
     * 如果两个节点不是互为邻居或不存在权重，则看作两个节点没有连接
     *
     * @param n1
     * @param n2
     * @return 是否连接
     * @author zpei
     * @create 2024/12/5
     **/
    @Override
    public boolean isLinked(Node n1, Node n2) {
        if(n1 == null || n2 == null) return false;
        return getWeight(n1, n2) != null;
    }


    /**
     * 修改两个节点间的权值
     * 如果成功修改两个节点则返回true, 否则返回false
     *
     * @param n1
     * @param n2
     * @param weight
     * @return 是否成功修改
     * @author zpei
     * @create 2024/12/5
     **/
    @Override
    public boolean updateWeight(Node n1, Node n2, Long weight) throws Exception{
        if(n1 == null || n2 == null || weight == null || !isLinked(n1, n2)) return false;
        Long oldWeight = n1.getWeight(n2);
        // 只要存在一个节点与另一个节点更新权重失败，则更新失败
        if(!n1.setWeight(n2, weight)) return false;

        if(!n2.setWeight(n1, weight)) {
            if(!n1.setWeight(n2, oldWeight)){
                throw new Exception("权值无法回滚");
            }
            return false;
        }
        return true;
    }



    /**
     * 向图索引中添加字段节点
     * 如果已经存在该节点，或者该节点为空则添加失败
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
        return true;
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
        return true;
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
}
