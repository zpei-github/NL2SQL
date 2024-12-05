package com.graph;
import com.node.Node;
import com.node.entity.FieldNode;
import com.node.entity.GranularityNode;
import com.node.entity.TableNode;


/** 数据库图接口
 * 图是无向有权图, 用于求取最小斯坦纳树
 * @Author zpei
 * @Date 2024/12/5
 */

public interface Graph {
    // 字段与所属表之间的权值定为1L
    public final static Long FIELD_TABLE_WEIGHT = 1L;

    // 如果该粒度就是表的粒度，或者比表的粒度细，则权值定为5L
    public final static Long FULL_GRANULARITY_TABLE_WEIGHT = 3L;

    // 如果该粒度比表的粒度粗，则初始值定为10L
    public final static Long INITIAL_GRANULARITY_TABLE_WEIGHT = 10L;

    /** 连接两个节点并赋权值
     * 权值一定不能为空。如果成功连接两个节点并赋权值则返回true, 否则返回false
     * @param n1
     * @param n2
     * @param weight
     * @return 是否成功连接
     * @author xinggang
     * @create 2024/12/5
     **/
    public boolean link(Node n1, Node n2, Long weight) throws Exception;

    /** 修改两个节点间的权值
     * 如果成功修改两个节点则返回true, 否则返回false
     * @param n1
     * @param n2
     * @param weight
     * @return 是否成功修改
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean updateWeight(Node n1, Node n2, Long weight) throws Exception;

    /** 获取指定两个节点之间的权重
     *
     * @param n1
     * @param n2
     * @return 权重值, 如果没有连接则返回null
     * @author zpei
     * @create 2024/12/5
     **/
    public Long getWeight(Node n1, Node n2);


    /** 判断两个节点是否连接
     * 如果两个节点不是互为邻居或不存在权重，则看作两个节点没有连接
     * @param n1
     * @param n2
     * @return 是否连接
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean isLinked(Node n1, Node n2);
}
