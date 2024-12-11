package com.graph_maker;

import com.graph.DBGraph;
import com.node.NodeFactory;
import lombok.Getter;

/**
 * @Author zpei
 * @Date 2024/12/11
 */

public class GraphMaker {

    @Getter
    private NodeFactory nodeFactory;
    @Getter
    private DBGraph graph;

    public GraphMaker() {
        nodeFactory = new NodeFactory();
        graph = new DBGraph();
    }


}
