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


public interface ServerConstant {
    String DATABASE_SERVICE_ERROR = "数据库服务出现异常，请重新尝试";

    String STEINER_TREE_SERVICE_ERROR = "当前本地数据库的数据无法满足您的SQL查询需求，请检查您的需求之后重试";
}
