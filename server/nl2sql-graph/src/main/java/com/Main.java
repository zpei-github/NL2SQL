package com;


import com.node.Node;
import com.node.NodeFactory;
import com.node.entity.FieldNode;
import com.node.entity.TableNode;

import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        NodeFactory factory = new NodeFactory();

        Node n1 = factory.createNode("table");
        Node n2 = factory.createNode("table");
        Node n3 = factory.createNode("field");
        n1.equals(n2);
        Set<Node> nodes = new HashSet<Node>();
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);

        System.out.println(nodes.contains(null));
    }
}
