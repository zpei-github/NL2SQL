package com.graph;


import com.graph.node.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class MatrixEdge implements Edge{
    private Node start;
    private Node end;

    public MatrixEdge(Node start, Node end) {
        this.start = start;
        this.end = end;
    }

    public MatrixEdge() {}

    // 因为父类的equals方法在此处被覆写，所以被调用之后用的是子类的equals方法
    @Override
    public boolean equals(Object o) {
        // 对象类型不匹配
        if (o == null || !this.getClass().equals(o.getClass()) ) return false;

        MatrixEdge o1 = (MatrixEdge) o;
        if (this.start == null){
            if(this.end == null){
                return o1.getStart() == null && o1.getEnd() == null;
            }else {
                return o1.getStart() == null && this.end.equals(o1.getEnd());
            }
        }
        if(this.end == null){
            return this.start.equals(o1.getStart()) && o1.getEnd() == null;
        }
        return this.start.equals(o1.getStart()) && this.end.equals(o1.getEnd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(start.hashCode(), end.hashCode());
    }

    @Override
    public String toString() {
        return "from " + start.toString() + " to " + end.toString();
    }
}
