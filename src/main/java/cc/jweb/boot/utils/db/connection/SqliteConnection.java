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

package cc.jweb.boot.utils.db.connection;

import cc.jweb.boot.utils.db.model.DbDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Sqlite数据库连接辅助类
 * <p>
 * 需求sqlite-jdbc-xxx.jar(本工具包不自带)
 * </p>
 *
 * @author ag777
 * @version create on 2018年04月24日,last modify at 2018年04月25日
 */
public class SqliteConnection extends BaseDbConnectionUtils {

    private SqliteConnection() {
    }

    public static Connection connect(String filePath) throws ClassNotFoundException, SQLException {
        StringBuilder url = new StringBuilder()
                .append("jdbc:sqlite:")
                .append(filePath);
        // 加载驱动程序
        Class.forName(DbDriver.SQLITE.getName());
        return DriverManager.getConnection(url.toString());
    }

    @Override
    public int getDefaultPort() {    //不存在的，直接读取文件
        return -1;
    }

}
