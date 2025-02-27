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
package com.web.controller;

import com.web.constant.MessageMark;
import com.web.constant.ProjectConstant;
import com.web.constant.ReturnCode;
import com.web.data.ResultData;
import com.web.entity.mysql.standard_database.StandardColumn;
import com.web.entity.mysql.standard_database.StandardTable;
import com.web.service.LLMService;
import com.web.service.elasticsearch.ElasticsearchService;
import com.web.service.graph.GraphComponent;
import com.web.service.mysql_database.ColumnService;
import com.web.service.mysql_database.TableService;
import com.web.tools.StringTools;
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
    private TableService tableService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private GraphComponent gComponent;

    @Autowired
    private ElasticsearchService esService;

    private static final Logger logger = LoggerFactory.getLogger(GraphComponent.class);

    @PostMapping("/sendMessage")
    public ResultData sendMessage(@RequestBody SendMessageVO messages) throws Exception {
        MyMessage question = messages.getQuestion();
        question.setCreateTime(System.currentTimeMillis());
        question.setMessageMark(MessageMark.MM10002);
        List<MyMessage> history = messages.getExistMessages();

        // 关键字
        MyMessage keywords = null;

        // 返回消息
        MyMessage answer = new MyMessage();

        MyMessage prompt = new MyMessage();

        // 返回的聊天记录
        List<MyMessage> returnData = new ArrayList<>();

        // prompt具体内容
        StringBuilder promptContent = new StringBuilder();

        // 表关键字列表
        List<StandardTable> tables = new ArrayList<>();

        // 字段关键字列表
        List<StandardColumn> columns = new ArrayList<>();

        // 树搜索
        Set<String> tableSet = new HashSet<>();


        // prompt内容
        promptContent.append(
                "你是一个数据分析助手，我会向你提出SQL查询需求、修改和纠错要求。对于我提出的SQL查询需求，你需要找出查询需求的表名关键字和字段名关键字并按照指定格式输出，只能从需求中获取表名和字段名关键字，不可以添加额外的内容。对于修改和纠错要求，你需要按照我的要求修改之后严格按照格式要求重新输出结果。具体的输出格式如下:\n" +
                        "{表名关键字1,表名关键字2...};{字段名关键字1,字段名关键字2...}\n\n" +
                        "示例1:\n SQL查询需求: 需要计算出厂年份为2024年的商品价格平均值\n" +
                        "结果: {};{出厂年份,商品价格}\n\n" +
                        "示例2:\n SQL查询需求: 以学生表中的学生为准，得出年龄在10至15岁之间的学生的身高平均值\n" +
                        "结果: {学生};{年龄,学生身高}\n");

        //设置prompt
        prompt.setMessageId(0);
        prompt.setSender(ProjectConstant.SYSTEM_SENDER);
        prompt.setCreateTime(System.currentTimeMillis());
        prompt.setContent(promptContent.toString());
        prompt.setMessageMark(MessageMark.MM10002);

        history.add(0, prompt);
        for(int i = history.size() - 1; i >= 0; i--){
            if((!MessageMark.MM10002.equals(history.get(i).getMessageMark())) &&
                    (!MessageMark.MM10003.equals(history.get(i).getMessageMark()))){
                history.remove(i);
            }
        }

        logger.info(history.toString());
        keywords = llmService.response(question, history, "qwen-max");


        history.add(question);
        history.add(keywords);

        // 格式重复解析
        int retryTimes = 0;
        while (!StringTools.isValidFormat(keywords.getContent())) {
            MyMessage correct = new MyMessage();
            correct.setMessageId(question.getMessageId());
            correct.setCreateTime(System.currentTimeMillis());
            correct.setSender(ProjectConstant.USER_SENDER);
            correct.setContent("输出结果格式不符合要求，重新输出");

            keywords = llmService.response(correct, history, "qwen-max");


            history.add(correct);
            history.add(keywords);

            retryTimes++;
            if (retryTimes > 3) {
                correct.setMessageId(question.getMessageId()+1);
                correct.setCreateTime(System.currentTimeMillis());
                correct.setMessageMark(MessageMark.MM10000);
                correct.setSender(ProjectConstant.CLIENT_SENDER);
                correct.setContent("服务出现异常，请检查查询需求后重试");


                returnData.add(question);
                returnData.add(correct);
                return ResultData.success(returnData);
            }
        }


        returnData.add(question);
        keywords.setMessageId(question.getMessageId() + 1);
        keywords.setMessageMark(MessageMark.MM10003);
        returnData.add(keywords);

        // 组成输出结果
        answer.setCreateTime(System.currentTimeMillis());
        answer.setSender(ProjectConstant.CLIENT_SENDER);
        answer.setMessageId(keywords.getMessageId() + 1);
        answer.setContent(getDatabaseInfo(keywords.getContent(), true, true, true));
        if(answer.getContent().equals("当前本地数据库的数据无法满足您的SQL查询需求，请检查您的需求之后重试")){
            answer.setMessageMark(MessageMark.MM10000);
        }else {
            answer.setMessageMark(MessageMark.MM10001);
        }


        returnData.add(answer);

        return ResultData.success(returnData);
    }

    @PostMapping("/get_sql")
    public ResultData generateSQL(@RequestBody SendMessageVO messages) throws Exception {
        // 用户提问消息
        MyMessage question = messages.getQuestion();
        question.setMessageMark(MessageMark.MM10004);
        question.setCreateTime(System.currentTimeMillis());

        // 聊天历史记录
        LinkedList<MyMessage> history = messages.getExistMessages();

        // 返回值
        List<MyMessage> returnData = new ArrayList<>();

        //
        MyMessage localQuestion = new MyMessage();

        // 大模型返回结果
        MyMessage response = null;

        // prompt具体内容
        StringBuilder promptContent = new StringBuilder();

        promptContent.append("你是一名专业数据分析师。后续我会提出数据库查询需求及相关修改意见，同时本地服务给出的表结构信息，表之间的连接信息等，最后需要根据需求生成准确的SQL查询语句");

        // prompt
        MyMessage prompt = new MyMessage();
        prompt.setMessageId(0);
        prompt.setSender(ProjectConstant.SYSTEM_SENDER);
        prompt.setCreateTime(System.currentTimeMillis());
        prompt.setContent(promptContent.toString());
        prompt.setMessageMark(MessageMark.MM10004);


        localQuestion.setMessageId(question.getMessageId());
        localQuestion.setMessageMark(MessageMark.MM10004);
        localQuestion.setCreateTime(System.currentTimeMillis());
        localQuestion.setContent(question.getContent() + "\n现在需要根据我的需求和上下文给出SQL查询语句");
        localQuestion.setSender(ProjectConstant.USER_SENDER);


        boolean mark = false;
        for(int i = history.size() - 1; i >= 0; i--){
            if(MessageMark.MM10000.equals(history.get(i).getMessageMark()) || (mark && MessageMark.MM10001.equals(history.get(i).getMessageMark()))){
                history.remove(i);
            } else if (MessageMark.MM10001.equals(history.get(i).getMessageMark())) {
                mark = true;
            }
        }

        logger.info(history.toString());

        returnData.add(question);
        response = llmService.response(localQuestion, history, "qwen-max");
        response.setMessageMark(MessageMark.MM10005);
        returnData.add(response);

        return ResultData.success(returnData);
    }


    /**
     * 将关键字信息转换为表结构信息和表连接信息
     * @param keywords 关键字
     * @param isAddDDL 是否添加表结构信息
     * @param isAddTableConnectInfo 是否添加表连接信息
     * @param isAddKeywordMap 是否添加关键字映射字段信息
     * @return  信息
     * @author zpei
     * @create 2025/2/25
     **/
    public String getDatabaseInfo(String keywords, boolean isAddDDL, boolean isAddTableConnectInfo,boolean isAddKeywordMap) {
        StringBuilder content = new StringBuilder();

        List<StandardTable> tables = new ArrayList<>();
        List<StandardColumn> columns = new ArrayList<>();

        Set<String> tableSet = new HashSet<>();

        // 关键字提取
        String[] keyword = keywords.split(";");
        String[] tableKeywords = keyword[0].substring(1, keyword[0].length() - 1).split(",");
        String[] columnKeywords = keyword[1].substring(1, keyword[1].length() - 1).split(",");

        for (String table : tableKeywords) {
            if (table.equals("")) continue;
            tables.add(tableService.getTableByStandardTableId(esService.searchByTableComment("standard_table_index", table).get(0).getStandard_table_id()));
        }
        for (String column : columnKeywords) {
            if (column.equals("")) continue;
            columns.add(columnService.getStandardColumnByStandardColumnId(esService.searchByColumnComment("standard_column_index", column).get(0).getStandard_column_id()));
        }

        Map<List<String>, List<String>> tree = gComponent.getMSTree(tables, columns);

        if (tree.isEmpty()) {
            content.append("当前本地数据库的数据无法满足您的SQL查询需求，请检查您的需求之后重试");
        } else {
            content.append("根据关键字结合本地数据库数据得出以下信息: ");

            if(isAddTableConnectInfo){
                content.append("\n表连接条件如下:\n");
                for (Map.Entry<List<String>, List<String>> entry : tree.entrySet()) {
                    content.append("表");
                    for (String table : entry.getValue()) {
                        content.append(table).append(", ");
                        tableSet.add(table);
                    }
                    content.append(" 通过以下字段进行连接 ");

                    for (String column : entry.getKey()) {
                        content.append(column).append(",");
                    }
                    content.append(";\n\n");
                }
            }

            if(isAddDDL){
                content.append("\n各表的表结构如下:\n");
                for (String table : tableSet) {
                    content.append(tableService.getTableByOriginalTableName(table).getOriginalTableDDL()).append("\n\n");
                }
            }

            // 判断是否添加关键字映射
            if(isAddKeywordMap){
                content.append("表关键字映射如下:\n");
                for(int i = 0, j = 0; i < tables.size(); i++, j++) {
                    if(tableKeywords[j].equals("")) j ++;
                    content.append(tableKeywords[j]).append(":").append(tables.get(i).getOriginalTableName()).append(",");
                }
                content.append("\n\n字段关键字映射如下:\n");
                for (int i = 0, j = 0; i < columns.size(); i++, j++){
                    if(columnKeywords[j].equals("")) j ++;
                    content.append(columnKeywords[j]).append(":").append(columns.get(i).getOriginalColumnName()).append(",");
                }
            }
        }

        return content.toString();
    }
}
