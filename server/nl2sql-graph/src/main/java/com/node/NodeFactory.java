package com.node;

import com.node.entity.FieldNode;
import com.node.entity.GranularityNode;
import com.node.entity.TableNode;

public class NodeFactory {
    public NodeFactory() {}

    public Node createNode(String label) {
        if("table".equals(label)) {
            return new TableNode();
        }
        if("field".equals(label)) {
            return new FieldNode();
        }
        if("granularity".equals(label)) {
            return new GranularityNode();
        }
        return null;
    }
}
