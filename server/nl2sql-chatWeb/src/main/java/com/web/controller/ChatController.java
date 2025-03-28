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
import com.web.data.ResultData;
import com.web.entity.milvus.StandardColumnSchema;
import com.web.entity.milvus.StandardTableSchema;
import com.web.entity.mysql.standard_database.StandardColumn;
import com.web.entity.mysql.standard_database.StandardTable;
import com.web.service.llm.LLMService;
import com.web.service.graph.GraphComponent;
import com.web.service.milvus.ColumnMilvusService;
import com.web.service.milvus.TableMilvusService;
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
    private TableMilvusService tMilService;

    @Autowired
    private ColumnMilvusService cMilService;

    @Autowired
    private GraphComponent gComponent;


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


        // 返回的聊天记录
        List<MyMessage> returnData = new ArrayList<>();

        // prompt具体内容
        StringBuilder promptContent = new StringBuilder();


        // prompt内容
        promptContent.append(
                "你是一个数据分析助手，我会向你提出SQL查询需求、修改和纠错要求。对于我提出的SQL查询需求，你需要找出查询需求的表名关键字和字段名关键字并按照指定格式输出，只能从需求中获取表名和字段名关键字，不可以额外添加思维链或者存在\"输出结果:{t1:f1}\"等这样的内容。对于修改和纠错要求，你需要按照我的要求修改之后严格按照格式要求重新输出结果。\n" +
                "格式说明如下：\n" +
                "1.每个\"{}\"代表一个关联关系，关联关系之间以英文半角\";\"分割；\n" +
                "2.如果字段和表存在明确关联关系，则该字段和表存在同一个关联关系，表和字段以英文半角\":\"分割；\n" +
                "3.每个\"{}\"中只能存在一个表名关键字，而字段关键字可以有多个，且必须以英文半角逗号\",\"进行分割；\n" +
                "4.如果没有表和字段存在明确关联关系，则字段将单独占用一个\"{}\"，且表名关键字为空；\n" +
                "5.如果没有字段和表存在明确关联关系，表也可以单独占用一个\"{}\"，且字段名关键字为空；\n" +
                "6.不能存在字段名关键字和表名关键字都为空的情况。\n" +
                "\n" +
                "具体的输出格式如下:\n" +
                "{表名关键字1:字段名关键字1,字段名关键字2...};{表名关键字2:字段名关键字3,字段名关键字4...};...\n" +
                "\n" +
                "示例1:SQL查询需求:需要计算出厂年份为2024年的商品价格平均值 \n" +
                "思维链:需求中没有指向明确的表，所以没有表名关键字；需求明确了需要出厂年份为2024，以及需要商品价格平均值，所以字段关键名中存在出厂年份和商品价格。两个关键名没有明确是属于同一张表，所以分别存储在两个关联关系中。\n" +
                "输出结果: {:出厂年份};{:商品价格}\n" +
                "\n" +
                "示例2:SQL查询需求:从学生表中得出年龄在10至15岁之间的学生的身高平均值\n" +
                "思维链:需求中明确了指向学生表，因此有一个表名关键字学生；并且需求还说明需要学生表中的年龄在10至15岁，因此学生表关联的字段名关键字有年龄；同时还指明学生身高也在学生表中，因此和学生表关联的字段还有学生身高。\n" +
                "输出结果: {学生:年龄,学生身高}\n" +
                "\n" +
                "示例3:SQL查询需求:以学生表中的学生ID为基准，查询学生的寝室号以及座位号\n" +
                "思维链:需求中明确了学生ID是学生表的字段，因此有一个关联关系是学生表和学生ID；需求中还存在关键字寝室号和座位号，但是没有明确寝室号和座位号与哪张表有关联关系，因此这两个关键字分别占一个\"{}\"。\n" +
                "输出结果: {学生:学生ID};{:寝室号};{:座位号}\n");

        //设置prompt
        MyMessage prompt = new MyMessage(0,ProjectConstant.SYSTEM_SENDER, MessageMark.MM10002, promptContent.toString());


        history.add(0, prompt);
        for(int i = history.size() - 1; i >= 0; i--){
            if((!MessageMark.MM10002.equals(history.get(i).getMessageMark())) &&
                    (!MessageMark.MM10003.equals(history.get(i).getMessageMark()))){
                history.remove(i);
            }
        }

        keywords = llmService.response(question, history);
        keywords.setMessageId(question.getMessageId() + 1);
        keywords.setMessageMark(MessageMark.MM10003);

        returnData.add(question);

        history.add(question);
        history.add(keywords);

        // 格式重复解析
        int retryTimes = 0;
        MyMessage correct = new MyMessage(question.getMessageId(), ProjectConstant.USER_SENDER, null, "输出结果格式不符合要求，重新输出");
        while (!StringTools.isValidFormat(keywords.getContent())) {
            keywords = llmService.response(correct, history);
            correct.setMessageId(keywords.getMessageId() - 1);
            correct.setCreateTime(System.currentTimeMillis());

            history.add(correct);
            history.add(keywords);

            retryTimes++;
            if (retryTimes > 3) {
                correct.setMessageId(question.getMessageId()+1);
                correct.setCreateTime(System.currentTimeMillis());
                correct.setMessageMark(MessageMark.MM10000);
                correct.setSender(ProjectConstant.CLIENT_SENDER);
                correct.setContent("服务出现异常，请检查查询需求后重试");

                returnData.add(correct);
                return ResultData.success(returnData);
            }
        }

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

        // 大模型返回结果
        MyMessage response = null;

        // prompt具体内容
        StringBuilder promptContent = new StringBuilder();

        promptContent.append("你是一名专业数据分析师。后续我会提出数据库查询需求及相关修改意见，同时本地服务给出的表结构信息，表之间的连接信息等，最后需要根据需求生成准确的SQL查询语句");

        // prompt
        MyMessage prompt = new MyMessage(0,ProjectConstant.SYSTEM_SENDER,MessageMark.MM10004,promptContent.toString());

        // 缺省消息
        MyMessage localQuestion = new MyMessage(question.getMessageId(), ProjectConstant.USER_SENDER, MessageMark.MM10004, question.getContent() + "\n现在需要根据我的需求和上下文给出SQL查询语句");


        boolean mark = false;
        for(int i = history.size() - 1; i >= 0; i--){
            if(MessageMark.MM10000.equals(history.get(i).getMessageMark()) || (mark && MessageMark.MM10001.equals(history.get(i).getMessageMark()))){
                history.remove(i);
            } else if (MessageMark.MM10001.equals(history.get(i).getMessageMark())) {
                mark = true;
            }
        }

        returnData.add(question);
        response = llmService.response(localQuestion, history);
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

        // 表关键字
        List<StandardTable> tables = new ArrayList<>();

        // 用于聚合的字段关键字
        List<StandardColumn> columns = new ArrayList<>();

        // 表关键字与源表名映射记录[关键字, 源表名]
        List<String[]> tableNameMap = new ArrayList<>();

        // 字段关键字与源字段名映射记录[关键字, 源字段名]
        List<String[]> columnNameMap = new ArrayList<>();

        // 关联关系提取
        String[] relations = keywords.split(";");
        for(String relation : relations){
            String[] keyword = relation.substring(1, relation.length() - 1).split(":");
            String tableKeyword = keyword[0].trim();
            String[] columnKeyword = keyword[1].split(",");

            // 关键字引擎匹配到的表列表
            List<StandardTableSchema> stTable = null;

            // 关键字引擎匹配到的字段列表
            List<StandardColumnSchema> stColumn = null;

            if (!tableKeyword.equals("")
                    // 开始匹配表关键字
                    && (stTable = tMilService.hybridSearch(tableKeyword, 3)) != null
                    && !stTable.isEmpty()) {
                StandardTable stt = tableService.getTableByStandardTableId(stTable.get(0).getStandard_table_id());
                tables.add(stt);
                tableNameMap.add(new String[]{tableKeyword,stt.getOriginal_table_name()});

                for(String column : columnKeyword){
                    if (!column.equals("")
                            // 开始匹配字段关键字
                            && (stColumn = cMilService.hybridSearch(column, 3)) != null
                            && !stColumn.isEmpty()){
                        StandardColumn stc = columnService.getStandardColumnByStandardColumnId(stColumn.get(0).getStandard_column_id());
                        columnNameMap.add(new String[]{column,stc.getOriginal_column_name()});
                    }
                }
                continue;
            }

            for(String column : columnKeyword){
                if (!column.equals("")
                        && (stColumn = cMilService.hybridSearch(column, 3)) != null
                        && !stColumn.isEmpty()){
                    StandardColumn stc = columnService.getStandardColumnByStandardColumnId(stColumn.get(0).getStandard_column_id());
                    columns.add(stc);
                    columnNameMap.add(new String[]{column,stc.getOriginal_column_name()});
                }
            }
        }

        Map<List<String>, List<String>> tree = gComponent.getMSTree(tables, columns);

        Set<String> tableSet = new HashSet<>();
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
                    content.append(tableService.getTableByOriginalTableName(table).getOriginal_table_ddl()).append("\n\n");
                }
            }

            // 判断是否添加关键字映射
            if(isAddKeywordMap){
                content.append("\n\n表关键字映射如下:\n");
                for(String[] tMap: tableNameMap){
                    content.append(tMap[0]).append(":").append(tMap[1]).append(",\n");
                }
                content.append("\n\n字段关键字映射如下:\n");
                for(String[] tMap: columnNameMap){
                    content.append(tMap[0]).append(":").append(tMap[1]).append(",\n");
                }
            }
        }
        return content.toString();
    }
}
