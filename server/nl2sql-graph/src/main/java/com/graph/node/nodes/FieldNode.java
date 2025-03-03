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
    private String originalName;

    public FieldNode() {}

    public FieldNode(String fieldName) {
        this.fieldName = fieldName;
    }

    // 因为父类的equals方法在此处被覆写，所以被调用之后用的是子类的equals方法
    @Override
    public boolean equals(Object o) {
        // 对象类型不匹配
        if (o == null || !this.getClass().equals(o.getClass()) ) return false;

        FieldNode o1 = (FieldNode) o;

        return (this.fieldName != null && this.fieldName.equals(o1.getFieldName())) ||  o1.getFieldName() == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash("FiledNode" + this.fieldName);
    }

    @Override
    public String toString() {
        return "FieldNode@@" + this.fieldName;
    }
}
