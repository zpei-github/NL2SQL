/*
  Copyright (c) 2024 zpei-github
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.zpei.nl2sql.web.constant;


public interface Prompts {
    String SYSTEM_SENDER_KEYWORD_ANALYSE_PROMPT = "你是一个数据分析助手，我会向你提出SQL查询需求、修改和纠错要求。对于我提出的SQL查询需求，你需要找出查询需求的表名关键字和字段名关键字并按照指定格式输出，只能从需求中获取表名和字段名关键字，不可以额外添加思维链或者存在\"输出结果:{t1:f1}\"等这样的内容。对于修改和纠错要求，你需要按照我的要求修改之后严格按照格式要求重新输出结果。\n" +
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
            "输出结果: {学生:学生ID};{:寝室号};{:座位号}\n";

    String SQL_GENERATE_PROMPT = "你是一名专业数据分析师。后续我会提出数据库查询需求及相关修改意见，同时本地服务给出的表结构信息，表之间的连接信息等，最后需要根据需求生成准确的SQL查询语句";

    String LLM_EXPORT_WRONG_FORMAT_PROMPT = "输出结果格式不符合要求，重新输出";

    String SERVICE_RETRY_PROMPT = "服务出现异常，请检查查询需求后重试";

}
