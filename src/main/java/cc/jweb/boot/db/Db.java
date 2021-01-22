/*
 * Copyright  (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
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

package cc.jweb.boot.db;

import cc.jweb.boot.common.lang.Result;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import io.jboot.db.JbootDb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Db extends JbootDb {

    /**
     * 查询map结果集
     *
     * @param sql
     * @param paras
     * @return
     */
    public static List<Map<String, Object>> findForListMap(String sql, Object... paras) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Record> records = find(sql, paras);
        if (records != null) {
            for (Record record : records) {
                list.add(record.getColumns());
            }
        }
        return list;
    }

    public static List<Map<String, Object>> findForListMap(SqlPara sqlPara) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Record> records = find(sqlPara);
        if (records != null) {
            for (Record record : records) {
                list.add(record.getColumns());
            }
        }
        return list;
    }

    public static Result paginate(String listSqlTemplate, String countSqlTemplate, Map data) {
        Result result = new Result();
        SqlPara sqlPara = getSqlPara(listSqlTemplate, data);
        result.setListData(find(sqlPara));
        sqlPara = getSqlPara(countSqlTemplate, data);
        result.setListTotal(queryLong(sqlPara.getSql(), sqlPara.getPara()));
        return result;
    }
}
