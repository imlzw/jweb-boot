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

import cc.jweb.boot.utils.db.DbHelper;
import cc.jweb.boot.utils.db.model.DbDriver;
import cc.jweb.boot.utils.db.model.OracleRole;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Oracle数据库连接辅助类
 * <p>
 * 需求ojdbc6.jar(本工具包不自带)
 * 该jar包为最新版本,以下为关联:
 * ojdbc14.jar - for Java 1.4 and 1.5
 * ojdbc5.jar - for Java 1.5
 * ojdbc6.jar - for Java 1.6
 * </p>
 *
 * @author ag777
 * @version create on 2018年04月24日,last modify at 2019年08月20日
 */
public class OracleConnection extends BaseDbConnectionUtils {

    public static final String ROLE_NORMAL = "normal";
    public static final String ROLE_SYSDBA = "sysdba";
    public static final String ROLE_SYSOPER = "sysoper";

    private OracleConnection() {
    }

    /**
     * @param ip       ip
     * @param port     port
     * @param user     user
     * @param password password
     * @param dbName   dbName
     * @param role     role
     * @return
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static Connection connect(String ip, int port, String user, String password, String dbName, OracleRole role) throws ClassNotFoundException, SQLException {
        return connect(ip, port, user, password, dbName, role, null);
    }

    /**
     * @param ip       ip
     * @param port     port
     * @param user     user
     * @param password password
     * @param dbName   dbName
     * @param role     role
     * @param propMap  propMap
     * @return
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static Connection connect(String ip, int port, String user, String password, String dbName, OracleRole role, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
        return connect(ip, port, user, password, null, dbName, role, propMap);
    }

    /**
     * @param ip       ip
     * @param port     port
     * @param user     user
     * @param password password
     * @param sid      sid
     * @param role     role
     * @return
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static Connection connectBySid(String ip, int port, String user, String password, String sid, OracleRole role) throws ClassNotFoundException, SQLException {
        return connectBySid(ip, port, user, password, sid, role, null);
    }

    /**
     * @param ip       ip
     * @param port     port
     * @param user     user
     * @param password password
     * @param sid      sid
     * @param role     role
     * @param propMap  propMap
     * @return
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static Connection connectBySid(String ip, int port, String user, String password, String sid, OracleRole role, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
        return connect(ip, port, user, password, sid, null, role, propMap);
    }

    /**
     * ipv4 Driver URL:
     * jdbc:oracle:thin:@//127.0.0.1:3306/orcl
     * <p>
     * ipv6 Driver URL:
     * jdbc:oracle:thin:@(DESCRIPTION=
     * (ADDRESS=(PROTOCOL=tcp)(HOST=[fe80::5cf:72])(PORT=1521))
     * (CONNECT_DATA=(SERVICE_NAME=fnstdb1)))
     *
     * @param ip       ip
     * @param port     端口号
     * @param user     用户名
     * @param password 密码
     * @param sid      sid
     * @param dbName   数据库名称/这个字段更可能是服务名称
     * @param role     角色normal/sysdba/sysoper
     * @param propMap  其它参数
     * @return
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     * @see http://stackoverflow.com/questions/10647845/does-oracle-11gr2-actually-support-ipv6
     * <p>
     * Deiver package version
     * <dependency>
     * <groupId>com.oracle</groupId>
     * <artifactId>ojdbc14</artifactId>
     * <version>10.2.0.3.0</version>
     * </dependency>
     * <p>
     * {@code
     * ①username@[//]host[:port][/service_name][:server][/instance_name]
     * ②(DESCRIPTION=
     * (ADDRESS=(PROTOCOL=tcp)(HOST=host)(PORT=port))
     * (CONNECT_DATA=
     * (SERVICE_NAME=service_name)
     * (SERVER=server)
     * (INSTANCE_NAME=instance_name)))
     * }
     * 官方文档
     * @see https://docs.oracle.com/cd/E18283_01/network.112/e10836/naming.htm
     * <p>
     * {@code
     * [示例]
     * 通过SID: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=[2001:183:1:162::131])(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=orcl)))
     * 通过服务名称: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=[2001:183:1:162::131])(PORT=1521))(CONNECT_DATA=(SID=orcl)))
     * }
     */
    public static Connection connect(String ip, int port, String user, String password, String sid, String dbName,
                                     OracleRole role, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
        StringBuilder url = new StringBuilder().append("jdbc:oracle:thin:@");
        if (!isIpV6(ip)) {    //ipV4
            url.append(ip).append(':').append(port);
            if (sid != null) {
                url.append(":").append(sid);
            } else if (dbName != null) {
                url.append('/').append(dbName);
            }
        } else {
            ip = "[" + ip + "]";
            url.append("DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=")
                    .append(ip).append(")(PORT=").append(port).append("))");
            url.append("(CONNECT_DATA=");
            if (sid != null) {
                url.append("(SID=").append(sid).append(")");
            } else if (dbName != null) {
                url.append("(SERVICE_NAME=").append(dbName).append(")");
            }
            url.append(')');
            url.append(')');
        }
        return connect(url.toString(), user, password, role, propMap);
    }

    /**
     * @param url      url
     * @param user     user
     * @param password password
     * @param role     role
     * @param propMap  propMap
     * @return
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static Connection connect(String url, String user, String password, OracleRole role, Map<String, Object> propMap) throws ClassNotFoundException, SQLException {
        Properties props = getProperties(propMap);
        if (role != null) {
            props.put("internal_logon", role.getName());
        }

        return DbHelper.getConnection(url, user, password, DbDriver.ORACLE, props);
    }

    @Override
    public int getDefaultPort() {
        return 1521;
    }
}