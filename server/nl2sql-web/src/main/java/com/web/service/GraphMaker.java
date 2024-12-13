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
package com.web.service;

import com.graph.DBGraph;
import com.minimal_steiner_tree.MSTree;
import com.node.NodeFactory;
import lombok.Getter;

/** 图生成
 *
 */
public class GraphMaker {

    @Getter
    private NodeFactory nodeFactory;

    @Getter
    private DBGraph graph;

    @Getter
    private MSTree treeSolver;



    public GraphMaker() {
        nodeFactory = new NodeFactory();
        graph = new DBGraph();
        treeSolver = new MSTree();
    }


}