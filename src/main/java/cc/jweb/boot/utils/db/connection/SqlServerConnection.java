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

package cc.jweb.boot.utils.db.connection;

import cc.jweb.boot.utils.db.DbHelper;
import cc.jweb.boot.utils.db.model.DbDriver;
import cc.jweb.boot.utils.lang.collection.MapUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;


/**
 * Mysql数据库连接辅助类
 * <p>
 * 需求sqljdbc-xxx.jar(本工具包不自带)
 * </p>
 *
 * @author ag777
 * @version create on 2018年04月24日,last modify at 2019年08月20日
 */
public class SqlServerConnection extends BaseDbConnectionUtils {

    private SqlServerConnection() {
    }

    /**
     * @param ip       ip
     * @param port     port
     * @param user     user
     * @param password password
     * @param dbName   dbName
     * @return
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static Connection connect(String ip, int port, String user, String password, String dbName) throws ClassNotFoundException, SQLException {
        return connect(ip, port, user, password, dbName, null);
    }

    /**
     * 连接sqlserver数据库
     * <p>连接数据库可以使用jtds这个驱动包，也可以使用sqljdbc4这个驱动包,这个方法使用后者
     * <p>
     * ipv4 Driver URL:
     * jdbc:sqlserver://127.0.0.1:1433/master
     * ipv6 Driver URL:
     * jdbc:sqlserver://
     *
     * @param ip       ip
     * @param port     port
     * @param user     user
     * @param password password
     * @param dbName   dbName
     * @param propMap  propMap
     * @return
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static Connection connect(String ip, int port, String user, String password, String dbName, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
        StringBuilder url = new StringBuilder()
                .append("jdbc:sqlserver://");
        if (!isIpV6(ip)) {    //ipV4
            url.append(ip)
                    .append(':')
                    .append(port)
                    .append(";");
            if (dbName != null) {
                url.append("databaseName=")
                        .append(dbName);
            }
        } else {    //ipV6
            if (propMap == null) {
                propMap = MapUtils.newHashMap();
            }
            propMap.put("portNumber", port);
            propMap.put("instanceName ", dbName);
            propMap.put("serverName", ip);
        }


        return connect(url.toString(), user, password, propMap);
    }

    /**
     * @param url      url
     * @param user     user
     * @param password password
     * @param propMap  propMap
     * @return
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static Connection connect(String url, String user, String password, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
        Properties props = getProperties(propMap);
        return DbHelper.getConnection(url, user, password, DbDriver.SQLSERVER, props);
    }

    @Override
    public int getDefaultPort() {
        return 1433;
    }

}