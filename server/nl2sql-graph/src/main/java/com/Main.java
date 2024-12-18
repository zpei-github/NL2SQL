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

        graph.add(n0);
        graph.add(n1);
        graph.add(n2);
        graph.add(n3);
        graph.add(n4);
        graph.add(n5);
        graph.add(n6);


        graph.add(n7);
        graph.add(n8);
        graph.add(n9);
        graph.add(n10);
        graph.add(n11);

        graph.add(n12);
        graph.add(n13);
        graph.add(n14);
        graph.add(n15);
        graph.add(n16);
        graph.add(n17);


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


        graph.printFields();
        graph.printGraph();



        mst.initial(graph);

        Set<Node> keys2 = new HashSet<>();
        keys2.add(graph.findFieldNode("plan_id"));
        keys2.add(graph.findFieldNode("tea_id"));

        // System.out.println(graph.field2table(keys2));
        // mst.solve(keys1);
        mst.solve(graph.fieldInvertedTable(keys2));
    }
}
