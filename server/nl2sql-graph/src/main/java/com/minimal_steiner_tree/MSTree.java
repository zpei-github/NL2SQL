package com.minimal_steiner_tree;

import com.graph.DBGraph;
import com.graph.Graph;
import com.node.Node;

import java.util.*;

/** 通过最小斯坦纳树算法求出包含指定节点的最小斯坦纳树
 * @Author zpei
 * @Date 2024/12/5
 */
public class MSTree {
    private int n = -1;  // 图中节点总数
    private long[][] dist; // 节点对之间的最短距离
    private int[][] nextNode; // 用于重建原有的图

    /** 状态压缩二维数组
     * dp[i][s] 表示以节点i为根，包含S点集合的最小权重和
     * 其中s是以二进制记录点集合的方式: 当 i = s时，代表节点i到自身的最小权重和0
     */
    private Graph graph;

    public MSTree(){}

    public MSTree(Graph graph) {
        initial(graph);
    }


    /** 初始化操作
     * 当图发生变化就需要进行初始化。更新图和重算节点对之间的最短路径
     * @param graph
     * @return
     * @author zpei
     * @create 2024/12/8
     **/
    public void initial(Graph graph) {
        this.graph = graph;
        this.n = graph.getNodeCount();
        dist = new long[n][n];
        nextNode = new int[n][n];

        for (int i = 0; i < n; i++) {

            // 初始化节点对之间的距离为Integer.MAX_VALUE
            Arrays.fill(dist[i], Integer.MAX_VALUE);
            Arrays.fill(nextNode[i], -1);
            dist[i][i] = 0;
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (graph.getWeight(i, j) != null) {
                    dist[i][j] = graph.getWeight(i, j);
                    nextNode[i][j] = -1; // directly connected
                }
            }
        }

        // Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];

                        // 如果最ij之间存在中间节点k使得ij路径减少，则nextNode记录下中间节点k
                        nextNode[i][j] = (nextNode[i][k] == -1 ? k : nextNode[i][k]);
                    }
                }
            }
        }

    }


    private boolean keyNodesInitial(Set<Node> keyNodes, int k, long[][] dp, int[][] parent) {
        int[] terminals = new int[k];
        Iterator<Node> iter = keyNodes.iterator();
        for (int i = 0; i < k; i++) {
            Integer x = graph.node2index(iter.next());
            terminals[i] = x;
        }

        // 判断关键节点之间是否连通，不连通则直接结束初始化
        for(int i = 0; i < k; i++) {
            for(int j = i + 1; j < k; j++) {
                if(dist[i][j] == Integer.MAX_VALUE) {
                    return false;
                }
            }
        }

        // 初始化dp和parent
        for (int v = 0; v < n; v++) {
            Arrays.fill(dp[v], Integer.MAX_VALUE);
            Arrays.fill(parent[v], -1);
        }

        iter = keyNodes.iterator();
        // 标记关键节点，并初始化关键节点的状态DP值
        for (int i = 0; i < k; i++) {
            Integer x = graph.node2index(iter.next());

            // 存在不属于图的节点就会初始化失败
            if(x == null) {return false;}
            dp[x][1 << i] = 0; // 初始化状态
        }
        return true;
    }


    public boolean solve(Set<Node> keyNodes){
        int k = keyNodes.size();
        int fullMask = (1 << k) - 1;

        long[][] dp = new long[n][1 << k];
        int[][] parent = new int[n][1 << k];

        if(!keyNodesInitial(keyNodes, k, dp, parent)) return false;



        // Dreyfus-Wagner DP
        for (int S = 1; S <= fullMask; S++) {
            // If S has only one bit set, it's already initialized
            // Otherwise, we need to do the splitting
            int subset = S;
            // Try splitting S into S1 and S2
            for (int S1 = (S - 1) & S; S1 > 0; S1 = (S1 - 1) & S) {
                int S2 = S ^ S1; // complement subset
                // Merge dp states
                for (int v = 0; v < n; v++) {
                    long val = dp[v][S1] + dp[v][S2];
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


        System.out.println("Minimum Steiner Tree weight: " + ans);

        Set<String> usedEdges = new HashSet<>();
        rebuildSolution(bestV, fullMask, usedEdges, parent);

        // usedEdges中存放的边表示最终的斯坦纳树边集，实际需要根据重建路径函数生成(u,v)的edge对
        System.out.println("Edges in the Steiner Tree:");
        for (String e : usedEdges) {
            System.out.println(e);
        }
        return true;
    }

    private void rebuildSolution(int v, int S, Set<String> usedEdges, int[][] parent) {
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

    private void addPath(int u, int v, Set<String> usedEdges) {
        if (u == v) return;
        int w = nextNode[u][v];
        if (w == -1) {
            // 没有中间节点，说明(u,v)直接构成最短路径的一条边
            // 根据Floyd-Warshall的实现，这里通常意味着u和v之间是直接相连的最短路径边
            usedEdges.add(edgeKey(u, v));
        } else {
            // 存在中间节点w，需要分解成两条路径: u->w 和 w->v
            addPath(u, w, usedEdges);
            addPath(w, v, usedEdges);
        }
    }

    private String edgeKey(int a, int b) {
        Node na = graph.index2node(a);
        Node nb = graph.index2node(b);
        return (a < b) ?
                na + "(" + a + ")" + " ----> " + nb + "(" + b + ")" : nb + "(" + b + ")" + " ----> " + na + "(" + a + ")";
    }
}

