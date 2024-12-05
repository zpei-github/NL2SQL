package com.node.entity;


import lombok.Getter;
import lombok.Setter;
import java.util.Objects;



/** 字段节点
 * 用于存储字段信息的节点, 其邻居节点都是表节点
 *
 *
 * */


@Setter
@Getter
public class FieldNode extends AbstractNode {
    private String fieldName;

    public FieldNode() {}

    // 因为父类的equals方法在此处被覆写，所以被调用之后用的是子类的equals方法
    @Override
    public boolean equals(Object o) {
        // 对象类型不匹配
        if (o == null || !this.getClass().equals(o.getClass()) ) return false;

        FieldNode o1 = (FieldNode) o;
        if (this.fieldName == null){
            return o1.getFieldName() == null;
        }
        return this.fieldName.equals(o1.getFieldName());
    }

    @Override
    public int hashCode() {
        return Objects.hash("FiledNode" + this.fieldName);
    }

    @Override
    public String toString() {
        return this.fieldName;
    }
}
