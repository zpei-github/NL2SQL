package com;


import com.graph.DBGraph;
import com.minimal_steiner_tree.MSTree;
import com.node.Node;
import com.node.NodeFactory;
import com.node.entity.FieldNode;
import com.node.entity.GranularityNode;
import com.node.entity.TableNode;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DBGraph graph = new DBGraph();
        NodeFactory factory = new NodeFactory();

        List<Node> keys = new ArrayList<>();


        TableNode n1 = (TableNode) factory.createNode("table", "stu-class");
        TableNode n2 = (TableNode) factory.createNode("table", "teacher-class");
        FieldNode n3 = (FieldNode)factory.createNode("field", "student_name");
        FieldNode n4 = (FieldNode)factory.createNode("field", "teacher_name");
        FieldNode n5 = (FieldNode)factory.createNode("field", "class_name");
        GranularityNode n6 = (GranularityNode)factory.createNode("granularity", "class");
        GranularityNode n7 = (GranularityNode)factory.createNode("granularity", "stu");
        GranularityNode n8 = (GranularityNode)factory.createNode("granularity", "teacher");

        keys.add(n3);
        keys.add(n2);

        n1.setGranularity(n7);
        n2.setGranularity(n8);
        n6.addField(n5);
        n7.addField(n3);
        n8.addField(n4);

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


        MSTree mst = new MSTree(graph, keys);
        mst.getMSTree();






    }
}
