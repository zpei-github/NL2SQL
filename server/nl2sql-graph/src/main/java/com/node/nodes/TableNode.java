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
package com.node.nodes;


import java.lang.Object;
import java.util.Objects;

import com.node.Node;
import lombok.Getter;
import lombok.Setter;

/** 表节点
 * 表节点用于存储表的信息, 包含表名和粒度信息
 *
 *
 *
 * */

@Setter
@Getter
public class TableNode extends AbstractNode {
    private String tableName;
    private Node granularity;

    public TableNode() {
        this(null);
    }

    public TableNode(String tableName) {
        this.tableName = tableName;
    }

    // 因为父类的equals方法在此处被覆写，所以被调用之后用的是子类的equals方法
    @Override
    public boolean equals(Object o) {
        // 对象类型不匹配
        if (null == o || !this.getClass().equals(o.getClass()) ) return false;

        TableNode o1 = (TableNode) o;

        if(this.tableName == null) {
            return o1.getTableName() == null;
        }
        return this.tableName.equals(o1.getTableName());
    }

    @Override
    public int hashCode() {
        return Objects.hash("TableNode" + this.tableName);
    }

    @Override
    public String toString() {
        return "TableNode@@" + this.tableName;
    }
}