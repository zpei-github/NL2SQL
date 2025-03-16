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

import java.util.*;


// RobinsZelikovsky算法求解ST 暂时无法使用
public class RobinsZelikovsky implements SteinerTreeSolver {
    // 图
    private final MatrixGraph graph;

    // 由Floyd-Warshall算法得到的节点对间最短距离，dist[i][j]表示节点i到节点j的最短距离
    private final long[][] dist;

    // 由Floyd-Warshall算法得到的中间节点数组，后续用于回溯最短路径
    private final int[][] nextNode;
    
    // 图中节点总数
    private final int n;

    public RobinsZelikovsky(MatrixGraph graph, long[][] dist, int[][] nextNode, int n) {
        this.graph = graph;
        this.dist = dist;
        this.nextNode = nextNode;
        this.n = n;
    }


    public void solver(Set<Node> keyNodes, Set<Edge> usedEdges) {
        if(keyNodes == null || usedEdges == null) throw new IllegalArgumentException("keyNodes and usedEdges must not be null");
        Set<Integer> terminals = new HashSet<>();
        for(Node node : keyNodes) {
            Integer index = graph.node2index(node);
            if(index == null) continue;
            terminals.add(index);
        }

        if(terminals.isEmpty()) return;

        // 步骤1: 预处理
        Map<Integer, Map<Integer, Long>> metric = new HashMap<>();
        for (int u : terminals) {
            metric.put(u, new HashMap<>());
            for (int v : terminals) {
                if (u != v) {
                    metric.get(u).put(v, dist[u][v]);
                }
            }
        }

        // 步骤2: 初始化组件集合
        List<Set<Integer>> components = new ArrayList<>();
        for (int t : terminals) {
            Set<Integer> comp = new HashSet<>();
            comp.add(t);
            components.add(comp);
        }

        // 步骤3: 贪心合并组件
        while (components.size() > 1) {
            long maxGain = Long.MIN_VALUE;
            int bestI = -1, bestJ = -1;
            int bestSteiner = -1;

            for (int i = 0; i < components.size(); i++) {
                for (int j = i + 1; j < components.size(); j++) {
                    Set<Integer> compI = components.get(i);
                    Set<Integer> compJ = components.get(j);

                    // 遍历所有非关键节点作为候选Steiner点
                    for (int v = 0; v < n; v++) {
                        if (!terminals.contains(v)) {
                            // 计算通过Steiner点的增益（复用dist矩阵）
                            long costI = getMinDistance(compI, v, metric);
                            long costJ = getMinDistance(compJ, v, metric);
                            long currentCost = getMinDistance(compI, compJ, metric);
                            
                            // 修正增益计算：当前成本减去使用Steiner点的成本
                            if(costI == Long.MAX_VALUE || costJ == Long.MAX_VALUE || currentCost == Long.MAX_VALUE) continue;
                            long gain = currentCost - (costI + costJ);

                            if (gain > maxGain) {
                                maxGain = gain;
                                bestI = i;
                                bestJ = j;
                                bestSteiner = v;
                            }
                        }
                    }
                }
            }

            if (bestI != -1 && maxGain > 0) {
                // 合并组件并添加Steiner点
                Set<Integer> merged = new HashSet<>(components.get(bestI));
                merged.addAll(components.get(bestJ));
                merged.add(bestSteiner);
                components.remove(bestJ);
                components.set(bestI, merged);
            } else {
                // 如果没有找到有效的Steiner点或增益为负，直接合并两个最近的组件
                long minCost = Long.MAX_VALUE;
                bestI = -1;
                bestJ = -1;
                
                for (int i = 0; i < components.size(); i++) {
                    for (int j = i + 1; j < components.size(); j++) {
                        long cost = getMinDistance(components.get(i), components.get(j), metric);
                        if (cost < minCost) {
                            minCost = cost;
                            bestI = i;
                            bestJ = j;
                        }
                    }
                }
                
                if (bestI != -1) {
                    Set<Integer> merged = new HashSet<>(components.get(bestI));
                    merged.addAll(components.get(bestJ));
                    components.remove(bestJ);
                    components.set(bestI, merged);
                } else {
                    // 如果无法合并，退出循环
                    break;
                }
            }
        }

        if (!components.isEmpty()) {
            buildSteinerEdges(components.get(0), usedEdges);
        }
    }


    // 辅助方法：获取组件到某节点的最小距离
    private long getMinDistance(Set<Integer> comp, int v, Map<Integer, Map<Integer, Long>> metric) {
        long min = Long.MAX_VALUE;
        for (int u : comp) {
            min = Math.min(min, metric.get(u).getOrDefault(v, Long.MAX_VALUE));
        }
        return min;
    }

    // 辅助方法：获取两组件间的最小距离
    private long getMinDistance(Set<Integer> comp1, Set<Integer> comp2, Map<Integer, Map<Integer, Long>> metric) {
        long min = Long.MAX_VALUE;
        for (int u : comp1) {
            for (int v : comp2) {
                min = Math.min(min, metric.get(u).get(v));
            }
        }
        return min;
    }

    // 辅助方法：通过nextNode回溯路径生成边
    private void buildSteinerEdges(Set<Integer> component, Set<Edge> usedEdges) {
        for (int u : component) {
            for (int v : component) {
                if (u < v && dist[u][v] < Long.MAX_VALUE) {
                    // 通过nextNode回溯路径
                    int current = u;
                    while (current != v) {
                        int next = nextNode[current][v];
                        if (next == -1) {
                            // 如果没有找到下一个节点，说明路径不完整
                            break;
                        }
                        Edge e = edge(current, next);
                        if (e != null) {
                            usedEdges.add(e);
                        }
                        current = next;
                    }
                }
            }
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
}
