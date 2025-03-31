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


public class MinimumDistance {

    /**
     * Floyd-Warshall算法 用于节点对之间最短距离
     * @param dist 节点对间最短距离
     * @param nextNode 最短路径记录
     * @param n 节点数量
     * @return
     * @author zpei
     * @create 2025/2/27
     **/
    public static void floydWarshall(long[][] dist, int[][] nextNode, int n) {
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
}
