package com.zpei.nl2sql.graph;


import com.zpei.nl2sql.graph.node.Node;

public interface Edge {
    public Node getStart();
    public Node getEnd();
    public void setStart(Node start);
    public void setEnd(Node end);
}
