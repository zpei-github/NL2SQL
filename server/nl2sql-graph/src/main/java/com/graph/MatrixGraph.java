package com.graph;

import com.node.Node;
import com.node.entity.FieldNode;
import com.node.entity.GranularityNode;
import com.node.entity.TableNode;
import com.sun.xml.internal.ws.util.StringUtils;
import lombok.Getter;

import java.util.*;

// 可动态扩展的邻接矩阵图
public abstract class MatrixGraph implements Graph {
    // 节点位置信息map，节点总数等于map的size
    protected final Map<Node, Integer> node2index;

    // 位置节点List
    protected final List<Node> index2nodes;

    // 空置的索引
    private final LinkedList<Integer> vacantIndexes;

    // 邻接矩阵
    protected List<List<Integer>> graph;

    public MatrixGraph() {
        node2index = new HashMap<>();
        index2nodes = new ArrayList<>();
        vacantIndexes = new LinkedList<>();
        graph = new ArrayList<>();
    }


    /**
     * 给新建节点n分配索引值index
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

        // 如果有空置索引，则将空置索引分配给该节点
        if(!vacantIndexes.isEmpty()){
            nodeIndex = vacantIndexes.removeLast();
            index2nodes.set(nodeIndex, n);
        }else {
            // 将节点添加到node数组
            index2nodes.add(n);

            nodeIndex = index2nodes.size() - 1;

            // 添加n的邻接矩阵并进行扩展
            graph.add(new ArrayList<>());
            for(int i = 0; i <= nodeIndex; i ++){
                graph.get(nodeIndex).add(null);
            }
        }

        // 创建n的索引
        node2index.put(n, nodeIndex);

        // 自己到自己的节点距离为0
        graph.get(nodeIndex).set(nodeIndex, 0);

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
     * @author zpei
     * @create 2024/12/5
     **/
    @Override
    public boolean link(Node n1, Node n2, Integer weight){
        if(n1 == null || n2 == null || weight == null || n1.equals(n2)) return false;

        // 节点需要有索引，否则无法连接两个节点
        Integer n1index = node2index(n1);
        Integer n2index = node2index(n2);

        if (n1index == null || n2index == null || isLinked(n1index, n2index)) return false;

        int n1neighborCount = graph.get(n1index).size();
        int n2neighborCount = graph.get(n2index).size();
        int nodesCount = index2nodes.size();

        // 在连接节点时，扩展节点的邻接List
        for(; n1neighborCount < nodesCount; n1neighborCount ++){
            graph.get(n1index).add(null);
        }
        for(; n2neighborCount < nodesCount; n2neighborCount ++){
            graph.get(n2index).add(null);
        }

        // 无向图双向连接
        graph.get(n1index).set(n2index, weight);
        graph.get(n2index).set(n1index, weight);
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

        Integer n1index = node2index(n1);
        Integer n2index = node2index(n2);

        // 判断是否添加到索引以及是否越界
        if (n1index == null || n2index == null ) return -2;
        return getWeight(n1index, n2index);
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
        if(i1 >= graph.size() || i2 >= graph.get(i1).size()) return null;
        return graph.get(i1).get(i2);
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


    /** 移除某个节点
     *
     * @param n
     * @return 返回是否移除成功
     * @author zpei
     * @create 2024/12/11
     **/
    @Override
    public boolean removeNode(Node n) {
        if(n == null || !node2index.containsKey(n)) return false;
        return removeNode(node2index(n));
    }


    /** 根据节点索引值移除该节点
     *
     * @param i
     * @return 返回是否移除成功
     * @author zpei
     * @create 2024/12/11
     **/
    @Override
    public boolean removeNode(int i) {
        if(i >= index2nodes.size() || index2node(i) == null) return false;

        // 将连接解除
        graph.get(i).replaceAll(ignored -> null);
        for(int x = 0; x < graph.size(); x ++){
            if(i < graph.get(x).size()){
                graph.get(x).set(i, null);
            }
        }

        if(node2index.remove(index2node(i)) != null){
            index2nodes.set(i, null);
            vacantIndexes.add(i);
            return true;
        }
        return false;
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
        Integer nodeIndex = node2index(n);

        if(nodeIndex == null) return null;
        List<Node> neighbors = new ArrayList<>();

        for(int i = 0; i < graph.get(nodeIndex).size(); i++){
            if(graph.get(nodeIndex).get(i) != null && i != nodeIndex){ neighbors.add(index2node(i));}
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
        for(int i = 0; i < graph.get(n).size(); i++){
            if(graph.get(n).get(i) != null && i != n){ result.add(i);}
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

        int node1Index = node2index(n1);
        int node2Index = node2index(n2);

        // 无向图需要双向修改
        graph.get(node1Index).set(node2Index, weight);
        graph.get(node2Index).set(node1Index, weight);
        return true;
    }


    /** 获取图中节点总数
     * @return 图中节点总数
     * @author zpei
     * @create 2024/12/7
     **/
    @Override
    public int getNodeCount() {
        return node2index.size();
    }

    /** 当图添加节点完毕之后需要计算之后生成关键数据才能使用
     *
     * @return
     * @author zpei
     * @create 2024/12/9
     **/
    @Override
    public abstract boolean compute();

    public void printGraph(){
        int formatLength = 10;
        System.out.print(String.format("%" + formatLength + "s", " "));
        for(Integer i = 0; i < index2nodes.size(); i++){
            System.out.print(i + String.format("%" + (formatLength - i.toString().length()) + "s", " "));
        }
        System.out.println();
        System.out.println();

        for (Integer i = 0 ; i < graph.size() ; i++) {
            System.out.print(i + String.format("%" + (formatLength - i.toString().length()) + "s", " "));
            for (int j = 0 ;  j < graph.get(i).size(); j++) {
                if(graph.get(i).get(j) == null){
                    System.out.print("null" + String.format("%" + (formatLength - 4) + "s", " "));
                }else {
                    System.out.print(graph.get(i).get(j) + String.format("%" + (formatLength - graph.get(i).get(j).toString().length()) + "s", " ") );
                }

            }
            System.out.println();
            System.out.println();
        }
    }

    public void printNodeIndex(){
        for(int i = 0; i < index2nodes.size(); i++){
            System.out.println( index2node(i) + "(" + i +")");
        }
        System.out.println();
    }
}
