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
package com.zpei.nl2sql.minimal_steiner_tree.initialize;


import com.zpei.nl2sql.graph.MatrixGraph;

public class ConnectedComponent {

    /** dfs算法求解连通分支
     *
     * @param graph 图
     * @param index2part 索引标记数组
     * @param n 图中节点数量
     * @return 连通分量的数量
     * @author zpei
     * @create 2025/2/27
     **/
    public static int getComponentsByDFS(MatrixGraph graph, int[] index2part, int n) {
        int partNum = 0;
        for (int i = 0; i < n; i++) {
            if (index2part[i] == -1) {
                partNum ++;
                dfsOfComponent(i, graph, index2part, partNum);
            }
        }
        return partNum;
    }

    // dfs求解连通分量
    private static void dfsOfComponent(int node, MatrixGraph graph, int[] visited, int partNum){
        if(visited[node] != -1)return;
        visited[node] = partNum;
        for(Integer neighbor : graph.extractNeighborIndex(node)){
            dfsOfComponent(neighbor, graph, visited, partNum);
        }
    }
}
