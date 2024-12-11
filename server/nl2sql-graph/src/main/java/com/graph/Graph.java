package com.graph;


import com.node.Node;

import java.util.List;


/** 数据库图接口
 * 图是无向有权图, 用于求取最小斯坦纳树
 * @Author zpei
 * @Date 2024/12/5
 */

public interface Graph {
    /** 将节点添加到图中，并给节点n分配唯一的索引值index
     * 在图中所有的节点需要有一个固定的索引值index，方便索引和创建邻接矩阵
     * @param n
     * @return 返回是否创建节点成功
     * @author zpei
     * @create 2024/12/7
     **/
    public int allocateIndex(Node n);


    /** 连接两个节点并赋权值
     * 权值一定不能为空。如果成功连接两个节点并赋权值则返回true, 否则返回false
     *
     * @param n1
     * @param n2
     * @param weight
     * @return 是否成功连接
     * @author xinggang
     * @create 2024/12/5
     **/
    public boolean link(Node n1, Node n2, Integer weight);


    /** 由节点获取其索引值
     *
     * @param n
     * @return 索引值
     * @author zpei
     * @create 2024/12/7
     **/
    public Integer node2index(Node n);


    /** 由索引值index获取对应的节点node
     *
     * @param index
     * @return 节点node
     * @author zpei
     * @create 2024/12/7
     **/
    public Node index2node(int index);


    /** 修改两个节点间的权值
     * 如果成功修改两个节点则返回true, 否则返回false
     * @param n1
     * @param n2
     * @param weight
     * @return 修改失败返回-1， 修改成功返回修改后的权重值
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean updateWeight(Node n1, Node n2, Integer weight);


    /** 获取图中节点总数
     *
     * @return 图中节点总数
     * @author zpei
     * @create 2024/12/7
     **/
    public int getNodeCount();


    /** 获取指定两个节点之间的权重
     *
     * @param n1
     * @param n2
     * @return 权重值, 如果没有连接则返回null
     * @author zpei
     * @create 2024/12/5
     **/
    public Integer getWeight(Node n1, Node n2);

    /** 根据两节点的索引查询权重
     *
     * @param i1
     * @param i2
     * @return 节点间权重
     * @author zpei
     * @create 2024/12/7
     **/
    public Integer getWeight(int i1, int i2);

    /** 判断两个节点是否连接
     * 如果两个节点不是互为邻居或不存在权重，则看作两个节点没有连接
     * @param n1
     * @param n2
     * @return 是否连接
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean isLinked(Node n1, Node n2);

    /** 根据索引值判断两个节点是否连接
     * 如果两个节点不是互为邻居或不存在权重，则看作两个节点没有连接
     * @param i1
     * @param i2
     * @return 是否连接
     * @author zpei
     * @create 2024/12/7
     **/
    public boolean isLinked(int i1, int i2);

    /** 获取指定节点的邻居节点List
     *
     * @param n
     * @return
     * @author zpei
     * @create 2024/12/5
     **/
    public List<Node> extractNeighbors(Node n);


    /** 获取指定节点的邻居节点索引值List
     *
     * @param n
     * @return 邻居节点的索引值List
     * @author zpei
     * @create 2024/12/5
     **/
    public List<Integer> extractNeighborIndex(int n);

    /** 移除某个节点
     *
     * @param n
     * @return 返回是否移除成功
     * @author zpei
     * @create 2024/12/11
     **/
    public boolean removeNode(Node n);

    /** 根据节点索引值移除该节点
     *
     * @param i
     * @return 返回是否移除成功
     * @author zpei
     * @create 2024/12/11
     **/
    public boolean removeNode(int i);

    /** 当图添加节点完毕之后需要计算之后生成关键数据才能使用
     *
     * @return
     * @author zpei
     * @create 2024/12/9
     **/
    public boolean compute();
}
