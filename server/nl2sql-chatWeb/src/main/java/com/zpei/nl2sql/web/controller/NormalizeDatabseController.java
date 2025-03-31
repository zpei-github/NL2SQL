package com.zpei.nl2sql.web.controller;


import com.zpei.nl2sql.web.service.graph.GraphComponent;
import com.zpei.nl2sql.web.service.milvus.ColumnMilvusService;
import com.zpei.nl2sql.web.service.milvus.TableMilvusService;
import com.zpei.nl2sql.web.service.mysql_database.ColumnService;
import com.zpei.nl2sql.web.service.mysql_database.TableService;
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
