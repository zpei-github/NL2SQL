package com.graph;


import com.graph.node.Node;

public interface Edge {
    public Node getStart();
    public Node getEnd();
    public void setStart(Node start);
    public void setEnd(Node end);
}
