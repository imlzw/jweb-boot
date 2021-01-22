/*
 * Copyright (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.jweb.boot.utils.db.interf;


import cc.jweb.boot.utils.db.DbHelper;

/**
 * 数据库事务接口
 *
 * @author ag777
 * @version create on 2017年10月16日,last modify at 2017年10月16日
 */
public interface DBTransactionInterf {
    public boolean doTransaction(DbHelper helper) throws Exception;
}
