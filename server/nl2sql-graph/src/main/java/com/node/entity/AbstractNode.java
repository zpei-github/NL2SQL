package com.node.entity;

import com.node.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/** 节点抽象类
 * 节点使用HashMap储存邻居节点的信息，包括指向的节点与距离该节点的距离
 *
 * */
public abstract class AbstractNode implements Node {
    // 相邻节点与其权重
    protected Map<Node, Long> neighbours;

    public AbstractNode() {
        this.neighbours = new HashMap<>();
    }

    @Override
    public int getDegree() {
        return neighbours.size();
    }

    @Override
    public boolean isNeighbour(Node n) {
        return neighbours.containsKey(n);
    }

    @Override
    public Set<Node> getNeighbours() {
        return neighbours.keySet();
    }

    @Override
    public boolean addNeighbour(Node n, Long weight) {
        if (null == n || weight == null) return false;

        if(neighbours.containsKey(n)) {
            return false;
        }
        neighbours.put(n, weight);
        return true;
    }

    @Override
    public boolean setWeight(Node n, Long weight) {
        if (null == n || weight == null) return false;
        if(!neighbours.containsKey(n)) {
            return false;
        }
        neighbours.replace(n, weight);
        return true;
    }

    @Override
    public Long getWeight(Node n) {
        if(n == null || !neighbours.containsKey(n)) {
            return null;
        }
        return neighbours.get(n);
    }

    @Override
    public boolean removeNeighbour(Node n) {
        if (null == n) return false;
        if(!neighbours.containsKey(n)) {
            return false;
        }
        neighbours.remove(n);
        return false;
    }
}
