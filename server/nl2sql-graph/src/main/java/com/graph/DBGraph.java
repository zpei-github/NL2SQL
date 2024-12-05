package com.graph;
import com.node.Node;


/** 数据库图接口
 * 图是无向有权图, 用于求取最小斯坦纳树
 * @Author zpei
 * @Date 2024/12/5
 */

public interface DBGraph {
    /** 连接两个节点并赋权值
     * 权值一定不能为空。如果成功连接两个节点并赋权值则返回true, 否则返回false
     * @param n1
     * @param n2
     * @param weight
     * @return 是否成功连接
     * @author xinggang
     * @create 2024/12/5
     **/
    public boolean link(Node n1, Node n2, Long weight);


    /** 修改两个节点间的权值
     * 如果成功修改两个节点则返回true, 否则返回false
     * @param n1
     * @param n2
     * @param weight
     * @return 是否成功修改
     * @author zpei
     * @create 2024/12/5
     **/
    public boolean updateWeight(Node n1, Node n2, Long weight);


    /** 获取指定两个节点之间的权重
     *
     * @param n1
     * @param n2
     * @return 权重值, 如果没有连接则返回null
     * @author zpei
     * @create 2024/12/5
     **/
    public Long getWeight(Node n1, Node n2);

    /** 向图索引中添加字段节点
    * @param n 节点
    * @return 是否成功添加
    * @Author zpei
    * @Date 2024/12/5
    */
    public boolean addFieldNode(Node n);

    /** 根据字段名获取字段节点
     *
     * @param name
     * @return
     * @author zpei
     * @create 2024/12/5
     **/
    public Node findFieldNode(String name);

    // 向图索引中添加表节点
    public boolean addTableNode(Node n);

    // 根据表名获取表节点
    public Node findTableNode(String name);

    // 向图索引中添加粒度节点

    // 根据粒度名获取粒度节点

}
