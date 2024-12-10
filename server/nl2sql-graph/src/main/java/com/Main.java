package com;


import com.graph.DBGraph;
import com.minimal_steiner_tree.MSTree;
import com.node.Node;
import com.node.NodeFactory;
import com.node.entity.FieldNode;
import com.node.entity.GranularityNode;
import com.node.entity.TableNode;

import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        DBGraph graph = new DBGraph();
        NodeFactory factory = new NodeFactory();

        MSTree mst = new MSTree();

        TableNode n0 = (TableNode) factory.createNode("table", "teacher");
        TableNode n1 = (TableNode) factory.createNode("table", "dim_course");
        TableNode n2 = (TableNode) factory.createNode("table", "live_plans");
        TableNode n3 = (TableNode) factory.createNode("table", "stu");
        TableNode n4 = (TableNode) factory.createNode("table", "stu_course");
        TableNode n5 = (TableNode) factory.createNode("table", "stu_live_classes");
        TableNode n6 = (TableNode) factory.createNode("table", "stu_live_plans");
        FieldNode n7 = (FieldNode)factory.createNode("field", "tea_id");
        FieldNode n8 = (FieldNode)factory.createNode("field", "course_id");
        FieldNode n9 = (FieldNode)factory.createNode("field", "stu_id");
        FieldNode n10 = (FieldNode)factory.createNode("field", "plan_id");
        FieldNode n11 = (FieldNode)factory.createNode("field", "class_id");
        GranularityNode n12 = (GranularityNode)factory.createNode("granularity", "tea");
        GranularityNode n13 = (GranularityNode)factory.createNode("granularity", "course");
        GranularityNode n14 = (GranularityNode)factory.createNode("granularity", "stu");
        GranularityNode n15 = (GranularityNode)factory.createNode("granularity", "plan");
        GranularityNode n16 = (GranularityNode)factory.createNode("granularity", "stu_course");
        GranularityNode n17 = (GranularityNode)factory.createNode("granularity", "stu_course_plan_class");



        n0.setGranularity(n12);
        n1.setGranularity(n13);
        n2.setGranularity(n15);
        n3.setGranularity(n14);
        n4.setGranularity(n16);
        n5.setGranularity(n16);
        n6.setGranularity(n17);

        n12.addField(n7);
        n13.addField(n8);
        n14.addField(n9);
        n15.addField(n10);
        n16.addField(n8);
        n16.addField(n9);
        n17.addField(n8);
        n17.addField(n9);
        n17.addField(n10);
        n17.addField(n11);

        graph.addTableNode(n0);
        graph.addTableNode(n1);
        graph.addTableNode(n2);
        graph.addTableNode(n3);
        graph.addTableNode(n4);
        graph.addTableNode(n5);
        graph.addTableNode(n6);


        graph.addFieldNode(n7);
        graph.addFieldNode(n8);
        graph.addFieldNode(n9);
        graph.addFieldNode(n10);
        graph.addFieldNode(n11);

        graph.addGranularityNode(n12);
        graph.addGranularityNode(n13);
        graph.addGranularityNode(n14);
        graph.addGranularityNode(n15);
        graph.addGranularityNode(n16);
        graph.addGranularityNode(n17);


        graph.link(n0, n7, 1);
        graph.link(n0, n12, 2);

        graph.link(n1, n7, 1);
        graph.link(n1, n8, 1);
        graph.link(n1, n13, 2);

        graph.link(n2, n10, 1);
        graph.link(n2, n15, 2);

        graph.link(n3, n9, 1);
        graph.link(n3, n14, 2);

        graph.link(n4, n7, 1);
        graph.link(n4, n8, 1);
        graph.link(n4, n9, 1);
        graph.link(n4, n16, 2);

        graph.link(n5, n8, 1);
        graph.link(n5, n9, 1);
        graph.link(n5, n11, 1);
        graph.link(n5, n16, 2);

        graph.link(n6, n8, 1);
        graph.link(n6, n9, 1);
        graph.link(n6, n10, 1);
        graph.link(n6, n11, 1);
        graph.link(n6, n17, 2);
        graph.compute();


        mst.initial(graph);

        Set<Node> keys2 = new HashSet<>();
        keys2.add(graph.findTableNode("teacher"));
        keys2.add(graph.findTableNode("stu_live_plans"));

        // mst.solve(keys1);
        mst.solve(keys2);

    }
}
