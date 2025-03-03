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
package com.minimal_steiner_tree.mst_solve_alogrithms;


import com.graph.DBGraphEdge;
import com.graph.Edge;
import com.graph.MatrixGraph;
import com.graph.node.Node;
import com.graph.node.nodes.GranularityNode;
import com.graph.node.nodes.TableNode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;


// dreyfus_wagner算法求解st
public class DreyfusWagner implements SteinerTreeSolver {
    private final MatrixGraph graph;
    private final long[][] dist;
    private final int[][] nextNode;
    private final int n;

    public DreyfusWagner(MatrixGraph graph, long[][] dist, int[][] nextNode, int n) {
        this.graph = graph;
        this.dist = dist;
        this.nextNode = nextNode;
        this.n = n;
    }


    /** dreyfus_wagner相关方法
     *
     * @param keyNodes  关键节点集合
     * @param usedEdges 最小斯坦纳树边集合
     * @param dp 最小斯坦纳树的dp数组
     * @param parent 路径记录数组
     * @return
     * @author zpei
     * @create 2025/2/28
     **/
    private boolean keyNodesInitial(Set<Node> keyNodes, Set<Edge> usedEdges, long[][] dp, int[][] parent) {
        if(keyNodes == null || usedEdges == null) {
            return false;
        }

        int k = keyNodes.size();
        Iterator<Node> iter = keyNodes.iterator();


        // 初始化dp和parent
        for (int v = 0; v < this.n; v++) {
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

    /** dreyfus_wagner()方法相关方法
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
            rebuildSolution( v, S1, usedEdges, parent);
            rebuildSolution( v, S2, usedEdges, parent);
        } else {
            // p是负数，表示通过dp[v][S] = dp[u][S] + dist[u][v]的方式更新
            int u = ~p; // recover u
            rebuildSolution(u, S, usedEdges, parent);
            // 将u到v的最短路径加入解中
            addPath(u, v, usedEdges);
        }
    }


    /**
     * 将节点索引转换为边并添加到边集合中
     * @param u
     * @param v
     * @param usedEdges 边集合
     * @return
     * @author zpei
     * @create 2025/2/28
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
     * @param usedEdges 最小斯坦纳树的边
     * @return
     * @author zpei
     * @create 2025/1/8
     **/
    public void solver(Set<Node> keyNodes, Set<Edge> usedEdges) {
        if(keyNodes == null || usedEdges == null) throw new IllegalArgumentException("keyNodes and usedEdges must not be null");
        int k = keyNodes.size();
        int n = graph.getNodeCount();

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
}
