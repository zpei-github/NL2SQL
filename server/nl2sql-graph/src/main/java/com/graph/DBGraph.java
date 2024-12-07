package com.graph;

import com.node.Node;
import com.node.entity.FieldNode;
import com.node.entity.GranularityNode;
import com.node.entity.TableNode;
import lombok.Getter;

import java.util.*;


public class DBGraph implements Graph {
    // 名字索引节点map
    private final Map<String, FieldNode> fieldIndex;
    private final Map<String, GranularityNode> granularityIndex;
    private final Map<String, TableNode> tableIndex;

    // 节点2位置信息map
    @Getter
    private final Map<Node, Integer> node2index;

    // 位置2节点List
    @Getter
    private List<Node> index2nodes;

    @Getter
    // 邻接矩阵
    List<List<Integer>> dbGraph;

    public DBGraph() {
        fieldIndex = new HashMap<>();
        granularityIndex = new HashMap<>();
        tableIndex = new HashMap<>();

        node2index = new HashMap<>();
        index2nodes = new ArrayList<>();
        dbGraph = new ArrayList<>();
    }


    /**
     * 给节点n分配索引值index
     * 在图中所有的节点需要有一个固定的索引值index，方便索引和创建邻接矩阵
     *
     * @param n
     * @return 返回 -1代表节点为空， 返回-2 代表节点已经存在
     * @author zpei
     * @create 2024/12/7
     **/
    @Override
    public int allocateIndex(Node n) {
        if (n == null) return -1;
        if(node2index.containsKey(n)) return -2;

        int nodeIndex = -1;

        // 将节点添加到node数组
        index2nodes.add(n);
        nodeIndex = index2nodes.size() - 1;

        // 创建n的索引
        node2index.put(n, nodeIndex);

        // 添加n的邻接矩阵
        dbGraph.add(new ArrayList<>());

        // 自己到自己的节点距离为0
        for(int i = 0; i <= nodeIndex; i ++){
            dbGraph.get(nodeIndex).add(null);
        }
        dbGraph.get(nodeIndex).set(nodeIndex, 0);

        return nodeIndex;
    }


    /**
     * 由节点获取其索引值
     *
     * @param n
     * @return 索引值
     * @author zpei
     * @create 2024/12/7
     **/
    @Override
    public Integer node2index(Node n) {
        if (n == null) return -1;
        return node2index.get(n);
    }


    /**
     * 由索引值index获取对应的节点node
     *
     * @param index
     * @return 节点node
     * @author zpei
     * @create 2024/12/7
     **/
    @Override
    public Node index2node(int index) {
        return index2nodes.get(index);
    }


    /**
     * 连接两个节点并赋权值
     * 权值一定不能为空。如果成功连接两个节点并赋权值则返回true, 否则返回false
     * 图为无向有权图，所以需要双向赋权
     * @param n1
     * @param n2
     * @param weight
     * @return 是否成功连接
     * @author xinggang
     * @create 2024/12/5
     **/
    @Override
    public boolean link(Node n1, Node n2, Integer weight){
        if(n1 == null || n2 == null || weight == null || n1.equals(n2)) return false;

        // 节点有索引，否则无法连接两个节点
        Integer n1index = node2index.get(n1);
        Integer n2index = node2index.get(n2);

        if (n1index == null || n2index == null) return false;

        int n1neighborCount = dbGraph.get(n1index).size();
        int n2neighborCount = dbGraph.get(n2index).size();
        int nodesCount = index2nodes.size();

        // 在连接节点时，扩展节点的邻接List
        for(; n1neighborCount < nodesCount; n1neighborCount ++){
            dbGraph.get(n1index).add(null);
        }
        for(; n2neighborCount < nodesCount; n2neighborCount ++){
            dbGraph.get(n2index).add(null);
        }

        // 无向图双向连接
        dbGraph.get(n1index).set(n2index, weight);
        dbGraph.get(n2index).set(n1index, weight);
        return true;
    }


    /**
     * 获取指定两个节点之间的权重
     *
     * @param n1
     * @param n2
     * @return -1 存在空值节点，-2 未建立索引，null 未连接
     * @author zpei
     * @create 2024/12/5
     **/
    @Override
    public Integer getWeight(Node n1, Node n2) {
        if(n1 == null || n2 == null) return -1;

        Integer n1index = node2index.get(n1);
        Integer n2index = node2index.get(n2);

        // 判断是否添加到索引以及是否越界
        if (n1index == null || n2index == null ) return -2;

        if(n1index >= dbGraph.size() || n2index >= dbGraph.get(n1index).size()) return null;

        return dbGraph.get(node2index.get(n1)).get(node2index.get(n2));
    }

    /**
     * 根据两节点的索引查询权重
     *
     * @param i1
     * @param i2
     * @return 节点间权重
     * @author zpei
     * @create 2024/12/7
     **/
    @Override
    public Integer getWeight(int i1, int i2) {
        if(i1 >= dbGraph.size() || i2 >= dbGraph.get(i1).size()) return null;
        return dbGraph.get(i1).get(i2);
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
        Integer result = getWeight(n1, n2);
        return result != null && result >= 0;
    }

    /**
     * 根据索引值判断两个节点是否连接
     * 如果两个节点不是互为邻居或不存在权重，则看作两个节点没有连接
     *
     * @param i1
     * @param i2
     * @return 是否连接
     * @author zpei
     * @create 2024/12/7
     **/
    @Override
    public boolean isLinked(int i1, int i2) {
        return getWeight(i1, i2) != null;
    }


    /**
     * 获取指定节点的邻居节点List
     *
     * @param n
     * @return
     * @author zpei
     * @create 2024/12/5
     **/
    @Override
    public List<Node> extractNeighbors(Node n) {
        if(n == null) return null;
        Integer nodeIndex = node2index.get(n);

        if(nodeIndex == null) return null;
        List<Node> neighbors = new ArrayList<>();

        for(int i = 0; i < dbGraph.get(nodeIndex).size(); i++){
            if(dbGraph.get(nodeIndex).get(i) != null && i != nodeIndex){ neighbors.add(index2node(i));}
        }
        return neighbors;
    }

    /** 获取指定节点的邻居节点索引值List
     *
     * @param n
     * @return 邻居节点的索引值List
     * @author zpei
     * @create 2024/12/5
     **/
    @Override
    public List<Integer> extractNeighborIndex(int n) {
        if(n >= index2nodes.size()) return null;

        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < dbGraph.get(n).size(); i++){
            if(dbGraph.get(n).get(i) != null && i != n){ result.add(i);}
        }
        return result;
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
    public boolean updateWeight(Node n1, Node n2, Integer weight){
        if(n1 == null
                || n2 == null
                || weight == null
                || n1.equals(n2)
                || !isLinked(n1, n2)
        ) return false;

        int node1Index = node2index.get(n1);
        int node2Index = node2index.get(n2);

        // 无向图需要双向修改
        dbGraph.get(node1Index).set(node2Index, weight);
        dbGraph.get(node2Index).set(node1Index, weight);
        return true;
    }


    /** 获取图中节点总数
     * @return 图中节点总数
     * @author zpei
     * @create 2024/12/7
     **/
    @Override
    public int getNodeCount() {
        return index2nodes.size();
    }


    /**
     * 向图索引中添加字段节点
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

        // 将节点添加至索引并判断返回的索引值
        return allocateIndex(n) >= 0;
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
}
