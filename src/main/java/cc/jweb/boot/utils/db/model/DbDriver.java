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

package cc.jweb.boot.utils.db.model;

public enum DbDriver {

    //mysql-connector-java 6
    MYSQL {
        @Override
        public String getName() {
            return "com.mysql.cj.jdbc.Driver";
        }
    },
    //mysql-connector-java 5
    MYSQL_OLD {
        @Override
        public String getName() {
            return "com.mysql.jdbc.Driver";
        }
    },
    ORACLE {
        @Override
        public String getName() {
            return "oracle.jdbc.driver.OracleDriver";
        }
    },
    SQLSERVER {
        @Override
        public String getName() {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }
    },
    DB2 {
        @Override
        public String getName() {
            return "com.ibm.db2.jcc.DB2Driver";
        }
    },
    SQLITE {
        @Override
        public String getName() {
            return "org.sqlite.JDBC";
        }
    };

    public String getName() {
        return null;
    }
}
