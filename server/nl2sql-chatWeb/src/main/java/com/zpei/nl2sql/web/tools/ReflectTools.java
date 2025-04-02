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
package com.zpei.nl2sql.web.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Id;

/**
 * 用于反射的工具类
 * @Author zpei
 * @date 2025/4/1
 */


public class ReflectTools {

    // 反射获取类中的所有属性名称
    public static List<String> outputFieldsNameList(Class entityClass){
        Field[] fields = entityClass.getDeclaredFields();
        List<String> outputFields = new ArrayList<>();
        for(Field field : fields){
            outputFields.add(field.getName());
        }
        return outputFields;
    }


    // 反射获取类被org.springframework.data.annotation.Id注解的属性
    public static String getIDInEntity(Class entityClass){
        Field[] fields = entityClass.getDeclaredFields();
        String name = null;
        for(Field field : fields){
            if(field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                name = field.getName();
                break;
            }
        }
        return name;
    }
}
