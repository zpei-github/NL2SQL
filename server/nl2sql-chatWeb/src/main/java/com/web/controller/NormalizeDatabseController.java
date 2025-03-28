package com.web.controller;


import com.web.service.graph.GraphComponent;
import com.web.service.milvus.ColumnMilvusService;
import com.web.service.milvus.TableMilvusService;
import com.web.service.mysql_database.ColumnService;
import com.web.service.mysql_database.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/normalize")
public class NormalizeDatabseController {

    @Autowired
    private TableService tableService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private TableMilvusService tMilService;

    @Autowired
    private ColumnMilvusService cMilService;

    @Autowired
    private GraphComponent gComponent;


}
