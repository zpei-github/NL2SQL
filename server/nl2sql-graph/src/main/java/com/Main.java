package com;


import com.graph.DBGraph;
import com.minimal_steiner_tree.MSTree;
import com.node.Node;
import com.node.NodeFactory;
import com.node.entity.FieldNode;
import com.node.entity.GranularityNode;
import com.node.entity.TableNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        DBGraph graph = new DBGraph();
        NodeFactory factory = new NodeFactory();

        MSTree mst = new MSTree();

        TableNode n0 = (TableNode) factory.createNode("table", "class_location");
        TableNode n1 = (TableNode) factory.createNode("table", "stu_class");
        TableNode n2 = (TableNode) factory.createNode("table", "teacher_class");
        FieldNode n3 = (FieldNode)factory.createNode("field", "student_name");
        FieldNode n4 = (FieldNode)factory.createNode("field", "teacher_name");
        FieldNode n5 = (FieldNode)factory.createNode("field", "class_name");
        GranularityNode n6 = (GranularityNode)factory.createNode("granularity", "class");
        GranularityNode n7 = (GranularityNode)factory.createNode("granularity", "stu");
        GranularityNode n8 = (GranularityNode)factory.createNode("granularity", "teacher");

        n0.setGranularity(n6);
        n1.setGranularity(n7);
        n2.setGranularity(n8);
        n6.addField(n5);
        n7.addField(n3);
        n8.addField(n4);

        graph.addTableNode(n0);
        graph.addTableNode(n1);
        graph.addTableNode(n2);
        graph.addFieldNode(n3);
        graph.addFieldNode(n4);
        graph.addFieldNode(n5);
        graph.addGranularityNode(n6);
        graph.addGranularityNode(n7);
        graph.addGranularityNode(n8);

        graph.link(n1, n3, 5);
        graph.link(n1, n5, 5);
        graph.link(n2, n4, 5);
        graph.link(n2, n5, 5);
        graph.link(n0, n4, 5);

        mst.initial(graph);

        List<Node> keys1 = new ArrayList<>();
        keys1.add(n3);
        keys1.add(n2);

        Set<Node> keys2 = new HashSet<>();
        keys2.add(graph.findFieldNode("teacher_name"));
        keys2.add(graph.findFieldNode("student_name"));
        keys2.add(graph.findTableNode("class_location"));

        // mst.solve(keys1);
        mst.solve(keys2);
    }
}
