package com.web.controller;

import com.web.constant.ProjectConstant;
import com.web.data.ResultData;
import com.web.service.LLMService;
import com.web.service.SolveService;
import com.web.service.graph.GraphComponent;
import com.web.service.mysql_database.TableService;
import com.web.vo.MyMessage;
import com.web.vo.SendMessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private LLMService llmService;

    @Autowired
    private SolveService solveService;

    @Autowired
    private TableService tableService;

    private static final Logger logger = LoggerFactory.getLogger(GraphComponent.class);

    @PostMapping("/sendMessage")
    public ResultData sendMessage(@RequestBody SendMessageVO messages) throws Exception {
        MyMessage question = messages.getQuestion();
        question.setCreateTime(System.currentTimeMillis());
        List<MyMessage> history = messages.getExistMessages();

        //设置prompt
        MyMessage prompt = new MyMessage();
        prompt.setMessageId(0);
        prompt.setSender(ProjectConstant.SYSTEM_SENDER);
        prompt.setCreateTime(System.currentTimeMillis());
        prompt.setContent("你是一个智能助手，我会向你提出SQL查询需求和修改要求。对于我提出的SQL查询需求，你需要找出查询的表名关键字和字段名关键字并按照指定格式输出，只能从需求中获取表名和字段名关键字，不可以添加额外的内容。对于修改要求，你需要按照我要求重新输出符合格式要求的结果。输出格式如下：" +
                "{表名关键字1,表名关键字2...}" +
                ";{字段名关键字1,字段名关键字2...}");

        history.add(0, prompt);

        return ResultData.success(llmService.response(question, history, "qwen-max"));
    }

    @PostMapping("/get_sql")
    public ResultData generateSQL(@RequestBody SendMessageVO messages) throws Exception {

        LinkedList<MyMessage> history = messages.getExistMessages();

        MyMessage question = messages.getQuestion();
        Map<String, List<String>> keywordsmap = new HashMap<>();
        Set<String> tableSet = new HashSet<>();
        keywordsmap.put("table", new ArrayList<>());
        keywordsmap.put("column", new ArrayList<>());

        // 关键字提取
        String[] keywords = history.getLast().getContent().split(";");
        String[] tables = keywords[0].replace("{","").replace("}","").split(",");
        String[] columns = keywords[1].replace("{","").replace("}","").split(",");

        for(String table : tables) {
            keywordsmap.get("table").add(table);
        }
        for (String column : columns) {
            keywordsmap.get("column").add(column);
        }

        logger.info(keywordsmap.toString());
        Map<List<String>, List<String>> tree = solveService.getMSTree(keywordsmap);

        StringBuilder content = new StringBuilder();
        content.append("你需要结合初始的SQL查询需求,以及下面的表连接条件和表的DDL语句,给出最终的SQL查询代码. ");
        content.append("表连接条件如下:\n");
        for(Map.Entry<List<String>, List<String>> entry : tree.entrySet()) {
            for(String table : entry.getValue()) {
                content.append(table + ",");
                tableSet.add(table);
            }
            content.append(" 通过以下字段进行连接 ");

            for (String column : entry.getKey()) {
                content.append(column + ",");
            }
            content.append(";\n");
        }

        logger.info("a------->\n" + content.toString());
        content.append("表的DDL语句如下:\n");


        for(String table : tableSet) {
            content.append(tableService.getTableByOriginalTableName(table).getOriginalTableDDL() + "\n");
        }

        question.setContent(content.toString());

        logger.info(content.toString());
        return ResultData.success(llmService.response(question, history, "qwen-max"));
    }

}
