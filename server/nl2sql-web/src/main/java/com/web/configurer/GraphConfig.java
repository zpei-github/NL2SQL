package com.web.configurer;

import com.graph.DBGraph;
import com.minimal_steiner_tree.MSTree;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// 将外部的没有加入到spring bean中管理的类进行bean配置
@Configuration
public class GraphConfig {
    @Bean
    public DBGraph dbGraph() {
        return new DBGraph();
    }

    @Bean
    public MSTree mstree() {
        return new MSTree();
    }

}
