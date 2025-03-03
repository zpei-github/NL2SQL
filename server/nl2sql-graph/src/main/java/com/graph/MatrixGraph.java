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
package com.graph;


import com.graph.node.Node;
import com.graph.self_exceptions.IndexOutOfBoundsException;
import com.graph.self_exceptions.NoIndexException;

import java.util.*;


// 可动态扩展的邻接矩阵图
public abstract class MatrixGraph implements Graph {
    // 节点索引信息map，节点总数等于map的size
    protected final Map<Node, Integer> node2index;

    // 索引-节点List
    protected final List<Node> index2nodes;

    // 空置的位置
    private final LinkedList<Integer> vacantIndexes;

    // 邻接矩阵
    protected final List<List<Integer>> graph;

    // 初始化
    public boolean initialized;


    public MatrixGraph() {
        node2index = new HashMap<>();

        // 当index2nodes[i]为null时，代表索引值i未被分配给某个节点
        index2nodes = new ArrayList<>();
        vacantIndexes = new LinkedList<>();
        graph = new ArrayList<>();
        initialized = false;
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
    public int allocateIndex(Node n){
        if (n == null) throw new NullPointerException("when you allocate a index to a node, you are allocating a null node");

        if(node2index.containsKey(n)) return node2index.get(n);

        int nodeIndex = -1;

        // 如果有空置索引，则将空置索引分配给该节点
        if(!vacantIndexes.isEmpty()){
            nodeIndex = vacantIndexes.removeLast();
            index2nodes.set(nodeIndex, n);

            // 将旧的连接解除
            graph.get(nodeIndex).replaceAll(ignored -> null);
            for(int x = 0; x < graph.size(); x ++){
                if(nodeIndex < graph.get(x).size()){
                    graph.get(x).set(nodeIndex, null);
                }
            }
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

        // 自己到自己的节点距离为0
        graph.get(nodeIndex).set(nodeIndex, 0);

        // 创建n的索引
        node2index.put(n, nodeIndex);
        initialized = false;
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
        if (n == null) return null;
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


    /** 连接两个节点并赋权值
     *
     * @param n1
     * @param n2
     * @param weight
     * @return
     * @author zpei
     * @create 2024/12/5
     **/
    @Override
    public boolean link(Node n1, Node n2, Integer weight) {
        if(n1 == null || n2 == null || weight == null) throw new NullPointerException("when you link two nodes, there is a null node");
        if(n1.equals(n2)) throw new IllegalArgumentException("when you link two nodes, you are linking the same nodes");

        // 节点需要有索引，否则无法连接两个节点
        Integer n1index = node2index(n1);
        Integer n2index = node2index(n2);
        return link(n1index, n2index, weight);
    }


    /** 根据索引连接两个节点并赋权值
     * 权值一定不能为空。如果成功连接两个节点并赋权值则返回true, 否则返回false
     * 图为无向有权图，所以需要双向赋权
     * @param n1index
     * @param n2index
     * @param weight
     * @return 是否成功连接
     * @author zpei
     * @create 2024/12/25
     **/
    private boolean link(Integer n1index, Integer n2index, Integer weight){

        if(n1index >= index2nodes.size() || n2index >= index2nodes.size() || n1index < 0 || n2index < 0) throw new IndexOutOfBoundsException("when you link two indexes, one index is out of index bounds");

        if(index2nodes.get(n1index) == null || index2nodes.get(n2index) == null) throw new NoIndexException("when you link two indexes, one node doesn't have index");

        if(isLinked(n1index, n2index)) return false;

        int n1neighborCount = graph.get(n1index).size();
        int n2neighborCount = graph.get(n2index).size();
        int indexLength = index2nodes.size();

        // 在连接节点时，扩展节点的邻接List
        for(; n1neighborCount < indexLength; n1neighborCount ++){
            graph.get(n1index).add(null);
        }
        for(; n2neighborCount < indexLength; n2neighborCount ++){
            graph.get(n2index).add(null);
        }

        // 无向图双向连接
        graph.get(n1index).set(n2index, weight);
        graph.get(n2index).set(n1index, weight);
        initialized = false;
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
        if(n1 == null || n2 == null) return null;

        Integer n1index = node2index(n1);
        Integer n2index = node2index(n2);

        // 判断是否添加到索引
        if (n1index == null || n2index == null ) return null;
        return getWeight(n1index, n2index);
    }


    /**
     * 查询索引间权重
     *
     * @param i1
     * @param i2
     * @return 节点间权重
     * @author zpei
     * @create 2024/12/7
     **/
    public Integer getWeight(int i1, int i2) {
        if(i1 >= graph.size() || i2 >= graph.get(i1).size() || i1 < 0 || i2 < 0) return null;
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
    public boolean isLinked(Node n1, Node n2){
        if(n1 == null || n2 == null) throw new NullPointerException("when you get if two nodes is linked, there is a null node");
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
    public boolean isLinked(int i1, int i2){
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
        if(n == null) return true;
        if( !node2index.containsKey(n)) throw new NoIndexException("when you remove a null node, the node does not have an index");
        return removeIndex(node2index(n));
    }


    /** 根据节点索引值移除该节点
     *
     * @param index
     * @return 返回是否移除成功
     * @author zpei
     * @create 2024/12/11
     **/
    public boolean removeIndex(int index){
        if(index >= index2nodes.size() || index < 0) throw new IndexOutOfBoundsException("when you remove a index, the index is out of index bounds");
        Node n = index2nodes.get(index);
        if(n == null) throw new NoIndexException("when you remove a index, the index does not belong to node2index");

        // 软删除，仅删除索引到节点和节点到索引的映射
        if(node2index.remove(n) != null){
            // 索引信息处理
            index2nodes.set(index, null);
            vacantIndexes.add(index);
            initialized = false;
            return true;
        }
        return false;
    }

    /**
     * 获取指定节点的度数
     * @param n
     * @return
     * @author zpei
     * @create 2024/12/25
     **/
    @Override
    public Integer getDegree(Node n) {
        if(n == null || !node2index.containsKey(n)) return null;

        Integer nIndex = node2index.get(n);
        if(nIndex == null) return null;

        return getDegree(nIndex);
    }


    /**
     * 获取某个索引值节点的度
     * @param nodeIndex
     * @return 度
     * @author zpei
     * @create 2024/12/25
     **/
    public Integer getDegree(int nodeIndex) {
        if(nodeIndex >= index2nodes.size() || nodeIndex < 0 || index2nodes.get(nodeIndex) == null) return null;

        Integer degree = 0;
        for(int i = 0; i < graph.get(nodeIndex).size(); i++){
            if(graph.get(nodeIndex).get(i) != null && i != nodeIndex) degree++;
        }
        return degree;
    }


    /**
     * 获取指定节点的邻居节点List
     *
     * @param n
     * @return 邻居节点
     * @author zpei
     * @create 2024/12/5
     **/
    @Override
    public List<Node> extractNeighbors(Node n) {
        if(n == null) return null;
        Integer nodeIndex = node2index.get(n);

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
    public List<Integer> extractNeighborIndex(int n) {
        if(n >= index2nodes.size() || n < 0 || index2nodes.get(n) == null) return null;

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
    public boolean updateWeight(Node n1, Node n2, Integer weight) {
        if(n1 == null || n2 == null) throw new NullPointerException("when you update the weight between two nodes, there is a null node");

        if(weight == null ) throw new NullPointerException("when you update the weight between two nodes, the weight is null");

        if(n1.equals(n2)) throw new IllegalArgumentException("when you update the weight between two nodes, you are updated two same nodes weight");

        Integer node1Index = node2index(n1);
        Integer node2Index = node2index(n2);

        if(node1Index == null || node2Index == null) throw new NoIndexException("when you update the weight between two nodes, a node does not have index");
        return updateWeight(node1Index, node2Index, weight);
    }


    /** 根据两节点索引更新节点间权重
     *
     * @param n1
     * @param n2
     * @param weight
     * @return
     * @author zpei
     * @create 2024/12/25
     **/
    public boolean updateWeight(int n1, int n2, Integer weight){
        if(weight == null) throw new NullPointerException("when you update the weight between two nodes, the weight is null");

        if (n1 == n2 || !isLinked(n1, n2)) return false;
        // 无向图需要双向修改
        graph.get(n1).set(n2, weight);
        graph.get(n2).set(n1, weight);

        initialized = false;
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


    /** 当图添加节点完毕之后需要计算之后生成关键数据才能使用,或者图修改之后未初始化
     *
     * @return
     * @author zpei
     * @create 2024/12/9
     **/
    @Override
    public boolean initialize(){
        return true;
    }


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
        try{
            for(int i = 0; i < index2nodes.size(); i++){
                System.out.println( index2node(i) + "(" + i +")");
            }
            System.out.println();
        } catch (Exception e){

            e.printStackTrace();
        }

    }
}
