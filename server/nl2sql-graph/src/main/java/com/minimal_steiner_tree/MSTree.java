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

import com.graph.Edge;
import com.graph.DBGraphEdge;
import com.graph.MatrixGraph;
import com.graph.node.Node;
import com.graph.node.nodes.GranularityNode;
import com.graph.node.nodes.TableNode;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.min;

/** 通过最小斯坦纳树算法求出包含指定节点的最小斯坦纳树
 * @Author zpei
 * @Date 2024/12/5
 */
public class MSTree {
    // 图中节点总数
    private int n = -1;

    // 相邻矩阵图
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
        this.partCount = findConnectedComponents(graph, index2part, n);

        // 节点间最短距离计算
        floydWarshall(this.dist, this.nextNode, this.n);

        this.initialized = true;
    }


    /** initial()相关方法
     * 用于节点对之间最短距离，用于后面最小生成树算法
     * @param dist 节点对间最短距离
     * @param nextNode 最短路径记录
     * @param n 节点数量
     * @return
     * @author zpei
     * @create 2025/2/27
     **/
    private void floydWarshall(long[][] dist, int[][] nextNode, int n) {
        // Floyd-Warshall算法
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {

                        // 距离更新
                        dist[i][j] = dist[i][k] + dist[k][j];

                        // 如果最ij之间存在中间节点k使得ij路径减少，则nextNode记录下中间节点k
                        nextNode[i][j] = (nextNode[i][k] == -1 ? k : nextNode[i][k]);
                    }
                }
            }
        }
    }


    /** initial()相关方法
     *  初始化相关方法，连通分量求解方法,
     * @param graph 图
     * @param index2part 索引标记数组
     * @param n 图中节点数量
     * @return 连通分量的数量
     * @author zpei
     * @create 2025/2/27
     **/
    private int findConnectedComponents(MatrixGraph graph, int[] index2part, int n) {
        int partNum = 0;
        for (int i = 0; i < n; i++) {
            if (index2part[i] == -1) {
                partNum ++;
                this.dfsOfComponent(i, graph, index2part, partNum);
            }
        }
        return partNum;
    }

    // dfs求解连通分量
    private void dfsOfComponent(int node, MatrixGraph graph, int[] visited, int partNum){
        if(visited[node] != -1)return;
        visited[node] = partNum;
        for(Integer neighbor : graph.extractNeighborIndex(node)){
            dfsOfComponent(neighbor, graph, visited, partNum);
        }
    }






    /** keyNodesInitial()相关方法
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


    /** mstSolver()方法相关方法
     * dp数组和parent数组的初始化
     * @param keyNodes 关键节点集合
     * @param dp 最小斯坦纳树的dp数组
     * @param parent 路径记录数组
     * @return 是否初始化成功
     * @author zpei
     * @create 2025/2/26
     **/
    private boolean keyNodesInitial(Set<Node> keyNodes, Set<Edge> usedEdges,long[][] dp, int[][] parent) {
        if(!initialized || keyNodes == null || usedEdges == null) {
            return false;
        }

        if(!keyNodesCheck(keyNodes, usedEdges)) return false;

        int k = keyNodes.size();
        Iterator<Node> iter = keyNodes.iterator();


        // 初始化dp和parent
        for (int v = 0; v < n; v++) {
            Arrays.fill(dp[v], Integer.MAX_VALUE);
            Arrays.fill(parent[v], -1);
        }

        // 标记关键节点，并初始化关键节点的状态DP值
        for (int i = 0; i < k; i++) {
            Integer x = graph.node2index(iter.next());

            // 存在不属于图的节点就会初始化失败
            if(x == null) {return false;}

            // 初始化状态
            dp[x][1 << i] = 0;
        }
        return true;
    }

    /** mstSolver()方法相关方法
     * 图的边集合解
     * @param v 最小斯坦纳树权重
     * @param S 关键节点掩码值
     * @param usedEdges 最小斯坦纳树边集合
     * @param parent 路径记录数组
     * @return
     * @author zpei
     * @create 2025/2/26
     **/
    private void rebuildSolution(int v, int S, Set<Edge> usedEdges, int[][] parent) {
        int p = parent[v][S];
        if (p == -1) {
            // base case, no parent means either a single terminal or no improvement
            return;
        }
        if (p >= 0) {
            // p表示子集拆分点
            int S1 = p;
            int S2 = S ^ S1;
            // 再递归重建S1和S2的解
            rebuildSolution(v, S1, usedEdges, parent);
            rebuildSolution(v, S2, usedEdges, parent);
        } else {
            // p是负数，表示通过dp[v][S] = dp[u][S] + dist[u][v]的方式更新
            int u = ~p; // recover u
            rebuildSolution(u, S, usedEdges, parent);
            // 将u到v的最短路径加入解中
            addPath(u, v, usedEdges);
        }
    }


    /** rebuildSolution() 相关方法
     * 将节点索引转换为边并添加到边集合中
     * @param u
     * @param v
     * @param usedEdges
     * @return
     * @author zpei
     * @create 2025/2/27
     **/
    private void addPath(int u, int v, Set<Edge> usedEdges) {
        if (u == v) return;
        int w = nextNode[u][v];
        if (w == -1) {
            // 没有中间节点，说明(u,v)直接构成最短路径的一条边
            // 根据Floyd-Warshall的实现，这里通常意味着u和v之间是直接相连的最短路径边
            usedEdges.add(edge(u, v));
        } else {
            // 存在中间节点w，需要分解成两条路径: u->w 和 w->v
            addPath(u, w, usedEdges);
            addPath(w, v, usedEdges);
        }
    }


    //返回边，且按照表在前，粒度在后的顺序
    private Edge edge(int a, int b) {
        Node na = graph.index2node(a);
        Node nb = graph.index2node(b);
        Edge edge = new DBGraphEdge();
        if(na.getClass().equals(TableNode.class) && nb.getClass().equals(GranularityNode.class)){
            edge.setStart(na);
            edge.setEnd(nb);
            return edge;
        } else if (nb.getClass().equals(TableNode.class) && na.getClass().equals(GranularityNode.class))
        {
            edge.setStart(nb);
            edge.setEnd(na);
            return edge;
        }else
            return null;
    }


    /**
     * 在单个连通分量中解出最小斯坦纳树
     * @param keyNodes 关键节点
     * @return 返回最小斯坦纳树的边集合。其中边的格式为表节点在前，粒度节点在后
     * @author zpei
     * @create 2025/1/8
     **/
    public void mstSolver(Set<Node> keyNodes, Set<Edge> usedEdges) {
        if(keyNodes == null || usedEdges == null) throw new IllegalArgumentException("keyNodes and usedEdges must not be null");
        int k = keyNodes.size();

        // 状态码，通过二进制左移关键节点个数获取，可以通过一个状态码判断包含那些关键节点
        int fullMask = (1 << k) - 1;

        /** 状态压缩二维数组
         * dp[i][s] 表示以节点i为根，包含S点集合的最小权重和
         * 其中s是以二进制记录点集合的方式: 当 i = s时，代表节点i到自身的最小权重和0
         */
        long[][] dp = new long[n][1 << k];

        /** 记录dp
         *
         */
        int[][] parent = new int[n][1 << k];

        if(!keyNodesInitial(keyNodes, usedEdges, dp, parent)) {
            return;
        }

        // Dreyfus-Wagner DP
        for (int S = 1; S <= fullMask; S++) {
            // If S has only one bit set, it's already initialized
            // Otherwise, we need to do the splitting
            int subset = S;
            // Try splitting S into S1 and S2
            for (int S1 = (S - 1) & S; S1 > 0; S1 = (S1 - 1) & S) {
                int S2 = S ^ S1; // complement subset
                long val = -1L;
                // Merge dp states
                for (int v = 0; v < n; v++) {
                    val = dp[v][S1] + dp[v][S2];
                    if (val < dp[v][S]) {
                        dp[v][S] = val;
                        parent[v][S] = S1; // record splitting
                    }
                }
            }

            // Now try improving dp[v][S] by going through intermediate nodes u
            // dp[v][S] = min over u (dp[u][S] + dist[u][v])
            for (int v = 0; v < n; v++) {
                if (dp[v][S] < Integer.MAX_VALUE) {
                    for (int u = 0; u < n; u++) {
                        long val = dp[v][S] + dist[v][u];
                        if (val < dp[u][S]) {
                            dp[u][S] = val;
                            // record that we came from v
                            // to store more details, we might need another structure
                            // that says dp[u][S] from dp[v][S] and a shortest path from v to u
                            parent[u][S] = ~v;
                            // Using negative values to indicate it came from another node v by shortest path
                        }
                    }
                }
            }
        }
        // 计算完毕


        // Find the best endpoint
        long ans = Integer.MAX_VALUE;
        int bestV = -1;
        for (int v = 0; v < n; v++) {
            if (dp[v][fullMask] < ans) {
                ans = dp[v][fullMask];
                bestV = v;
            }
        }
        rebuildSolution(bestV, fullMask, usedEdges, parent);
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
        Set<Edge> usedEdges = new HashSet<>();

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
        if(k == 0) {
            mstSolver(keyNodes, usedEdges);
            return usedEdges;
        }else {
            // 对每个连通分支中的节点进行分别求解
            for(Map.Entry<Integer, Set<Node>> entry : partMark.entrySet()){
                mstSolver(entry.getValue(), usedEdges);
            }
        }
        return usedEdges;
    }

}

