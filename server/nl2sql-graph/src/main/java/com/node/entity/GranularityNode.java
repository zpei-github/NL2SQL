package com.node.entity;


import com.node.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
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

    // 因为父类的equals方法在此处被覆写，所以被调用之后用的是子类的equals方法
    @Override
    public boolean equals(Object o) {
        // 对象类型不匹配
        if (o == null || !this.getClass().equals(o.getClass()) ) return false;

        GranularityNode o1 = (GranularityNode) o;

        if(this.GranularityName == null){
            return o1.GranularityName == null;
        }

        return this.GranularityName.equals(o1.getGranularityName());
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
