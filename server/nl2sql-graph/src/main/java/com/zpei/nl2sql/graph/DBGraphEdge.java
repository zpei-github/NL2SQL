package com.zpei.nl2sql.graph;


import com.zpei.nl2sql.graph.node.Node;
import com.zpei.nl2sql.graph.node.nodes.GranularityNode;
import com.zpei.nl2sql.graph.node.nodes.TableNode;

import java.util.Objects;

public class DBGraphEdge implements Edge{
    private TableNode start;
    private GranularityNode end;

    public DBGraphEdge(Node start, Node end) {
        if(start instanceof TableNode) {this.start = (TableNode)start;}
        if(end instanceof GranularityNode) {this.end = (GranularityNode)end;}
    }

    public DBGraphEdge() {}

    // 因为父类的equals方法在此处被覆写，所以被调用之后用的是子类的equals方法
    @Override
    public boolean equals(Object o) {
        // 对象类型不匹配
        if (o == null || !this.getClass().equals(o.getClass()) ) return false;

        DBGraphEdge o1 = (DBGraphEdge) o;
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

    public void setStart(Node start) {
        if(start instanceof TableNode) {this.start = (TableNode)start;}
    }

    public void setEnd(Node end) {
        if(end instanceof GranularityNode) {this.end = (GranularityNode) end;}
    }

    public Node getStart() {
        return this.start;
    }

    public Node getEnd() {
        return this.end;
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
