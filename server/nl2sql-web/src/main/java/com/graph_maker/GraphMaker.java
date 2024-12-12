package com.graph_maker;

import com.graph.DBGraph;
import com.minimal_steiner_tree.MSTree;
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

    @Getter
    private MSTree treeSolver;

    public GraphMaker() {
        nodeFactory = new NodeFactory();
        graph = new DBGraph();
        treeSolver = new MSTree();
    }


}
