/*
 *   Copyright (c) 2024 zpei-github
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.graph.node;

import com.graph.node.nodes.FieldNode;
import com.graph.node.nodes.GranularityNode;
import com.graph.node.nodes.TableNode;

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

    public Node createNode(String label, String name) {
        if("table".equals(label)) {
            return new TableNode(name);
        }
        if("field".equals(label)) {
            return new FieldNode(name);
        }
        if("granularity".equals(label)) {
            return new GranularityNode(name);
        }
        return null;
    }
}
