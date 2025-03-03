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
package com.minimal_steiner_tree;

import com.graph.DBGraphEdge;
import com.graph.Edge;
import com.graph.MatrixGraph;
import com.graph.node.Node;

import java.util.*;

import com.graph.node.nodes.GranularityNode;
import com.graph.node.nodes.TableNode;
import com.minimal_steiner_tree.initialize.ConnectedComponent;
import com.minimal_steiner_tree.initialize.MinimumDistance;
import com.minimal_steiner_tree.mst_solve_alogrithms.DreyfusWagner;
import com.minimal_steiner_tree.mst_solve_alogrithms.SteinerTreeSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 通过最小斯坦纳树算法求出包含指定节点的最小斯坦纳树
 * @Author zpei
 * @Date 2024/12/5
 */
public class MSTree {
    // 图中节点总数
    private int n = -1;

    // 邻接矩阵图
    private MatrixGraph graph;

    // 节点对之间的最短距离
    private long[][] dist;

    // 记录最短路径之间的节点，用于重建最小生成树
    private int[][] nextNode;

    // 记录索引属于哪个连通分支, 连通分支编号从1号开始
    private int[] index2part;

    // 记录连通分支数量
    int partCount;


    // 是否被初始化
    boolean initialized = false;

    private static final Logger logger = LoggerFactory.getLogger(MSTree.class);


    public MSTree(){
    }



    /** 初始化操作
     * 当图发生变化就需要进行初始化。更新图和计算节点对之间的最短路径，
     * @param graph
     * @return
     * @author zpei
     * @create 2024/12/8
     **/
    public void initial(MatrixGraph graph) {
        this.graph = graph;
        this.n = graph.getNodeCount();
        this.dist = new long[n][n];
        this.nextNode = new int[n][n];

        this.index2part = new int[n];
        this.partCount = 0;

        for (int i = 0; i < n; i++) {
            // 初始化索引到连通分支编号为-1
            this.index2part[i] = -1;
            for (int j = 0; j < n; j++) {
                // 初始化设置节点之间没有最短路径，所以不存在最短路径间节点
                this.nextNode[i][j] = -1;

                // 初始化设置点间距离
                if (graph.isLinked(i, j)) {
                    // 直接连接的节点间距离为权重
                    this.dist[i][j] = graph.getWeight(i, j);
                }else {
                    // 未直接连接的节点间距离为Integer.MAX_VALUE
                    this.dist[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        // 连通分支计算
        this.partCount = ConnectedComponent.getComponentsByDFS(graph, this.index2part, this.n);

        // 节点间最短距离计算
        MinimumDistance.floydWarshall(this.dist, this.nextNode, this.n);

        this.initialized = true;
    }


    /**
     * 检查关键节点，处理关键节点的边界值
     * @param keyNodes 关键节点集合
     * @param usedEdges 输出边集合
     * @return 是否节点集合数量大于 1
     * @author zpei
     * @create 2025/2/27
     **/
    private boolean keyNodesCheck(Set<Node> keyNodes, Set<Edge> usedEdges){
        if(keyNodes == null || usedEdges == null) return false;
        int k = keyNodes.size();

        // 先创建一个无意义填充边，用于边界条件返回特殊值
        Edge e = new DBGraphEdge();
        TableNode t = new TableNode();
        GranularityNode g = new GranularityNode();
        e.setStart(t);
        e.setEnd(g);

        if(k == 0) {
            // 没有关键节点输入
            usedEdges.add(e);
            return false;
        }
        if (k == 1) {
            // 关键节点数量为1
            for (Node node : keyNodes) {
                if (node instanceof TableNode) {
                    e.setStart(node);
                    e.setEnd(((TableNode) node).getGranularity());
                }
            }
            usedEdges.add(e);
            return false;
        }
        return true;
    }



    /**
     * 在整个图中求解关键节点的最小斯坦纳树
     * @param keyNodes 关键节点
     * @return 边集合
     * @author zpei
     * @create 2025/2/27
     **/
    public Set<Edge> solve(Set<Node> keyNodes){
        if (keyNodes == null) throw new IllegalArgumentException("keyNodes must not be null");

        int k = keyNodes.size();
        Map<Integer, Set<Node>> partMark = new HashMap<>();

        // 区分每个连通分支中的节点
        for(Node node : keyNodes){
            Integer index = graph.node2index(node);
            if(index == null) continue;
            if(!partMark.containsKey(index2part[index])){
                partMark.put(index2part[index], new HashSet<>());
            }
            partMark.get(index2part[index]).add(node);
        }


        Set<Edge> usedEdges = new HashSet<>();
        SteinerTreeSolver mst_solver = new DreyfusWagner(graph, dist, nextNode, n);


        if(k == 0) {
            keyNodesCheck(keyNodes, usedEdges);
            return usedEdges;
        }else {
            // 对每个连通分支中的节点进行分别求解
            for(Map.Entry<Integer, Set<Node>> entry : partMark.entrySet()){
                // 没有通过检测的keyNodes会被忽略
                if(!keyNodesCheck(keyNodes, usedEdges))continue;

                mst_solver.solver( entry.getValue(), usedEdges);
            }
        }
        return usedEdges;
    }
}

