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
package com.graph.node.nodes;


import com.graph.node.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;


/** 粒度节点
 * 粒度节点存储粒度的信息, 节点中有粒度覆盖的字段的信息。
 * 粒度节点的邻居节点都是表节点
 * */
@Setter
@Getter
public class GranularityNode extends AbstractNode {

    private String GranularityName;

    // 粒度包含那些字段
    private Set<Node> fields;

    public GranularityNode() {
        this(null);
    }

    public GranularityNode(String granularityName) {
        this.GranularityName = granularityName;
        fields = new HashSet<>();
    }

    public boolean addField(Node field) {
        if (field == null) {
            return false;
        }
        return fields.add(field);
    }

    public boolean removeField(Node field) {
        if (field == null) {
            return false;
        }
        return fields.remove(field);
    }

    public int fieldCount() {
        return fields.size();
    }

    public Iterator<Node> fieldIterator() {
        return fields.iterator();
    }

    // 因为父类的equals方法在此处被覆写，所以被调用之后用的是子类的equals方法
    @Override
    public boolean equals(Object o) {
        // 对象类型不匹配
        if (o == null || !this.getClass().equals(o.getClass()) ) return false;

        GranularityNode o1 = (GranularityNode) o;

        return (this.GranularityName != null && this.GranularityName.equals(o1.getGranularityName())) || o1.getGranularityName() == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash("GranularityNode" + this.GranularityName);
    }

    @Override
    public String toString() {
        return "GranularityNode@@" + this.GranularityName;
    }
}
