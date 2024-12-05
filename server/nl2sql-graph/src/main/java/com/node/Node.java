package com.node;

import java.util.Set;

/** 图的节点接口
 */

public interface Node {

    // 返回该节点的度
    public int getDegree();

    // 判断某个节点是否是邻居
    public boolean isNeighbour(Node n);

    // 获取所有的邻居节点
    public Set<Node> getNeighbours();

    // 添加一个邻居节点
    public boolean addNeighbour(Node n);

    public boolean addNeighbour(Node n, Long weight);

    // 设置与邻居节点的权重
    public boolean setWeight(Node n, Long weight);

    // 获取与邻居节点的权重
    public Long getWeight(Node n);

    // 删除一个邻居节点
    public boolean removeNeighbour(Node n);
}
