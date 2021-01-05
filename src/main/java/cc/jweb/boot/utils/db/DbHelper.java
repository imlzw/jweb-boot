package cc.jweb.boot.utils.db;

import cc.jweb.boot.utils.db.connection.MysqlConnection;
import cc.jweb.boot.utils.db.connection.OracleConnection;
import cc.jweb.boot.utils.db.connection.SqlServerConnection;
import cc.jweb.boot.utils.db.connection.SqliteConnection;
import cc.jweb.boot.utils.db.interf.DBTransactionInterf;
import cc.jweb.boot.utils.db.model.*;
import cc.jweb.boot.utils.lang.StringUtils;
import cc.jweb.boot.utils.lang.interf.Disposable;
import cc.jweb.boot.utils.lang.reflection.ReflectionUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库操作辅助类
 *
 * @author ag777
 * @version create on 2017年07月28日,last modify at 2020年10月09日
 */
public class DbHelper implements Disposable, Closeable {

    //控制控制台输出开关
    private static boolean MODE_DEBUG = false;
    //执行完sql后关闭数据库连接,一旦开启则该工具类不可重复使用(连接不存在了)
    private static boolean MODE_CLOSE_AFTER_EXECUTE = false;
    private Connection conn;
    private String dbType;    //数据库类型(mysql/oracle/sqlite等)

    public DbHelper(Connection conn) {
        this.conn = conn;
        dbType = dbInfo().getName();
    }

    public static void setModeDebug(boolean debugMode) {
        DbHelper.MODE_DEBUG = debugMode;
    }

    public static void setModeCloseAfterExecute(boolean closeAfterExecuteMode) {
        DbHelper.MODE_CLOSE_AFTER_EXECUTE = closeAfterExecuteMode;
    }

    /**
     * 连接数据库
     * <p>
     * 默认连接mysql
     * <p>
     *
     * @param ip       ip
     * @param port     端口
     * @param dbName   数据库名称
     * @param user     用户名
     * @param password 密码
     * @return DbHelper
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static DbHelper connectDB(String ip, int port, String dbName, String user, String password) throws ClassNotFoundException, SQLException {
        return connectMysql(ip, port, dbName, user, password);
    }

    //--mysql
    public static DbHelper connectMysql(String ip, int port, String dbName, String user, String password) throws ClassNotFoundException, SQLException {
        return new DbHelper(
                MysqlConnection.connect(ip, port, user, password, dbName));
    }

    public static DbHelper connectMysql(String url, String user, String password) throws ClassNotFoundException, SQLException {
        return new DbHelper(
                MysqlConnection.connect(url, user, password, null));
    }

    //--oracle
    public static DbHelper connectOracle(String ip, int port, String dbName, String user, String password, OracleRole role) throws ClassNotFoundException, SQLException {
        return new DbHelper(
                OracleConnection.connect(ip, port, user, password, dbName, role));
    }

    public static DbHelper connectOracleBySid(String ip, int port, String sid, String user, String password, OracleRole role) throws ClassNotFoundException, SQLException {
        return new DbHelper(
                OracleConnection.connectBySid(ip, port, user, password, sid, role));
    }

    public static DbHelper connectOracle(String url, String user, String password, OracleRole role) throws ClassNotFoundException, SQLException {
        return new DbHelper(
                OracleConnection.connect(url, user, password, role, null));
    }

    //--sqlite
    public static DbHelper connectSqlite(String filePath) throws ClassNotFoundException, SQLException {
        return new DbHelper(SqliteConnection.connect(filePath));
    }

    //--sqlserver
    public static DbHelper connectSqlServer(String ip, int port, String dbName, String user, String password) throws ClassNotFoundException, SQLException {
        return new DbHelper(
                SqlServerConnection.connect(ip, port, user, password, dbName));
    }

    public static DbHelper connectSqlServer(String url, String user, String password) throws ClassNotFoundException, SQLException {
        return new DbHelper(
                SqlServerConnection.connect(url, user, password, null));
    }

    /**
     * 获取数据库连接
     *
     * @param url      url
     * @param user     user
     * @param password password
     * @param driver   driver
     * @param props    props
     * @return Connection
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    public static Connection getConnection(String url, String user, String password, DbDriver driver, Properties props) throws ClassNotFoundException, SQLException {
        if (props == null) {
            props = new Properties();
        }
        props.put(DbPropertieKey.COMMON_USER, user);    //账号
        props.put(DbPropertieKey.COMMON_PASSWORD, password);        //密码
        if (!props.containsKey(DbPropertieKey.COMMON_USEUNICODE)) {    //使用本地编码,value得是字符串类型,不然回报空指针异常
            props.put(DbPropertieKey.COMMON_USEUNICODE, "true");
        }
        if (!props.containsKey(DbPropertieKey.COMMON_ENCODING)) {    //编码
            props.put(DbPropertieKey.COMMON_ENCODING, "utf-8");
        }
        if (!props.containsKey("zeroDateTimeBehavior")) {
            props.put("zeroDateTimeBehavior", "convertToNull");    //CONVERT_TO_NULL
        }
        // 加载驱动程序
        Class.forName(driver.getName());
        return DriverManager.getConnection(url, props);
    }

    public static DbHelper connectDB(String url, String user, String password, DbDriver driver, Properties props) throws ClassNotFoundException, SQLException {
        return new DbHelper(
                getConnection(url, user, password, driver, props));
    }

    //--静态方法

    /**
     * 数据库类型转java类型(不全，只列出常用的，不在范围内返回null)
     * <p>
     * 参考http://blog.csdn.net/haofeng82/article/details/34857991<br>
     * 同时java类型受字段是否无符号影响:https://blog.csdn.net/weixin_42127613/article/details/84791794
     *
     * @param sqlType  字段的数据类型
     * @param size     字段长度
     * @param typeName 请使用ColumnPojo里的typeName
     * @return java类型
     */
    public static Class<?> toPojoType(int sqlType, int size, String typeName) {
        Class<?> clazz = null;

        boolean unsign = false;
        if (typeName != null && typeName.toUpperCase().contains("UNSIGNED")) {
            unsign = true;
        }

        switch (sqlType) {
            case Types.VARCHAR:    //12
            case Types.CHAR:            //1
            case Types.LONGVARCHAR:    //-1
            case Types.NVARCHAR:    //-9
                clazz = String.class;
                break;
            case Types.BLOB:            //2004
            case Types.VARBINARY:    //-3
            case Types.LONGVARBINARY:    //-4
                clazz = Byte[].class;
                break;
            case Types.INTEGER:    //4
            case Types.SMALLINT:    //5
                if (!unsign) {
                    clazz = Integer.class;
                } else {
                    clazz = Long.class;
                }
                break;
            case Types.TINYINT:        //-6
                if (size == 1) {
                    clazz = Boolean.class;
                } else {
                    clazz = Integer.class;
                }
                break;
            case Types.BIT:                //-7
                clazz = Boolean.class;
                break;
            case Types.BIGINT:        //-5
                if (!unsign) {
                    clazz = Long.class;
                } else {
                    clazz = BigInteger.class;
                }
                break;
            case Types.FLOAT:        //6
            case Types.REAL:            //7
                clazz = Float.class;
                break;
            case Types.DOUBLE:        //8
                clazz = Double.class;
                break;
            case Types.DECIMAL:    //3
            case Types.NUMERIC:    //2
                clazz = BigDecimal.class;
                break;
            case Types.DATE:            //91
                clazz = java.util.Date.class;    //java.sql.Date
                break;
            case Types.TIME:            //92
                clazz = java.util.Date.class;//java.sql.Time.class;
                break;
            case Types.TIMESTAMP:    //93
                clazz = java.util.Date.class;// java.sql.Timestamp.class;//java.util.Date是java.sql.Date/java.sql.Timestamp/java.sql.Time的父类
                break;
//			case Types.JAVA_OBJECT:	//2000
//			case Types.OTHER:	//1111
//				clazz = Object.class;
//				break;
            default:
                clazz = Object.class;
                break;
        }
        return clazz;
    }

    /**
     * @param sqlType sqlType
     * @return 字段类型最大长度(不准确)
     */
    @Deprecated
    public static Integer getMaxSize(int sqlType) {
        switch (sqlType) {
            case Types.INTEGER:
                return 255;
            case Types.VARCHAR:
                return 65535;
            default:
                return null;
        }
    }

    /**
     * @param sqlType sqlType
     * @return 数据库类型是否为字符串类型
     */
    public static boolean isSqlTypeVarchar(int sqlType) {
        switch (sqlType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.VARBINARY:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
            case Types.NVARCHAR:
                return true;
            default:
                return false;
        }
    }

    /**
     * @param sqlType sqlType
     * @return 数据库类型是否为日期类型
     */
    public static boolean isSqlTypeDate(int sqlType) {
        switch (sqlType) {
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
                return true;
            default:
                return false;
        }
    }

    /**
     * java类型转数据库类型(不全，只列出常用的，不在范围内返回varchar)
     *
     * @param clazz clazz
     * @return 数据库类型
     */
    public static Integer toSqlType(Class<?> clazz) {
        if (ReflectionUtils.isNumberType(clazz)) {
            if (Float.class == clazz || float.class == clazz) {
                return Types.FLOAT;
            } else if (Double.class == clazz || double.class == clazz) {
                return Types.DOUBLE;
            } else if (BigInteger.class == clazz) {
                return Types.BIGINT;
            } else if (BigDecimal.class == clazz) {
                return Types.DECIMAL;
            } else {
                return Types.INTEGER;
            }
        } else if (Boolean.class == clazz || boolean.class == clazz) {
            return Types.BOOLEAN;
        } else if (java.sql.Date.class == clazz || java.util.Date.class == clazz) {
            return Types.DATE;
        } else if (Timestamp.class == clazz) {
            return Types.TIMESTAMP;
        } else if (Time.class == clazz) {
            return Types.TIME;
        } else if (Byte[].class == clazz) {
            return Types.BLOB;
        } else {        //char,void
            return Types.VARCHAR;
        }
    }

    //--非静态方法

    /**
     * 将resultset转化为List&lt;Map&lt;String, Object&gt;&gt;
     * <p>
     * 171021改获取字段名称的方法getColumnName(i)为<br>
     * 获取别名getColumnLabel(i) 以免sql里写的别名不生效
     *
     * @param rs ResultSet
     * @return list
     * @throws SQLException SQLException
     */
    public static List<Map<String, Object>> convert2List(ResultSet rs) throws SQLException {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        ResultSetMetaData md = rs.getMetaData();

        int columnCount = md.getColumnCount(); // Map rowData;

        while (rs.next()) { // rowData = new HashMap(columnCount);

            Map<String, Object> rowData = new HashMap<String, Object>();

            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnLabel(i), rs.getObject(i));
            }

            list.add(rowData);

        }
        return list;

    }

    /**
     * 将resultSet转化为对象列表(方法有待验证及优化)
     *
     * @param rs    ResultSet
     * @param clazz 类型
     * @return list
     * @throws SQLException SQLException
     */
    public static <T> List<T> convert2List(ResultSet rs, Class<T> clazz) throws SQLException {
        try {
            List<T> list = new ArrayList<T>();

            ResultSetMetaData md = rs.getMetaData();

            int columnCount = md.getColumnCount(); // Map rowData;

            String[] cols = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                cols[i - 1] = StringUtils.underline2Camel(md.getColumnName(i), false);    //首字母大写，驼峰
            }

            while (rs.next()) { // rowData = new HashMap(columnCount);

                T rowData = ReflectionUtils.newInstace(clazz);

                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getName().equalsIgnoreCase(cols[i - 1])) {
                            boolean flag = field.isAccessible();
                            field.setAccessible(true);
                            field.set(rowData, value);
                            field.setAccessible(flag);
                        }
                    }


                }

                list.add(rowData);

            }
            return list;
        } catch (Exception ex) {
            err(ex);
            throw new SQLException("转换结果为对象列表失败");
        }
    }

    /**
     * @param clazz 类型
     * @return 一个类是否为基础类型
     */
    private static boolean isBasicClass(Class<?> clazz) {
        return ReflectionUtils.isBasicClass(clazz);
    }

    private static void err(Exception ex) {
        if (MODE_DEBUG) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取连接
     *
     * @return Connection
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * 测试连接
     *
     * @param timeoutSeconds timeoutSeconds
     * @return 是否成功
     */
    public boolean test(int timeoutSeconds) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.setQueryTimeout(timeoutSeconds);//单位秒
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }
        }
        return false;
    }

    /**
     * 执行数据库事务
     *
     * @param task 事务
     * @return 是否成功, 由DBTransactionInterf返回
     * @throws Exception Exception
     */
    public boolean doTransaction(DBTransactionInterf task) throws Exception {
        synchronized (this) {        //加锁
            try {
                conn.setAutoCommit(false);
                boolean result = task.doTransaction(this);
                conn.commit();
                return result;
            } catch (Exception ex) {
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }

    }

    /**
     * @return 是否为mysql数据库连接
     */
    public boolean isMysql() {
        return DbPojo.TYPE_MYSQL.equals(dbType);
    }

    /**
     * @return 是否为sqlite数据库连接
     */
    public boolean isSqlite() {
        return DbPojo.TYPE_SQLITE.equals(dbType);
    }

    /**
     * @return 是否为oracle数据库连接
     */
    public boolean isOracle() {
        return DbPojo.TYPE_ORACLE.equals(dbType);
    }

    /**
     * 重命名数据库表
     * <p>
     * 暂时只支持Mysql和Oracle数据库,
     * 对其他数据库执行该方法会抛出Runtime异常
     * </p>
     *
     * @param src 原表名
     * @param to  目标表名
     */
    public void reNameTable(String src, String to) {
        if (isMysql()) {
            update("RENAME TABLE " + src + " TO " + to + ";");
        } else if (isOracle()) {
            update("ALTER TABLE " + src + " RENAME TO " + to + ";");
        } else {
            throw new RuntimeException("暂只支持mysql和oracle数据库的表重命名操作");
        }

    }

    /**
     * @param sql sql
     * @return ResultSet
     * @see #getResultSetWithException(String)
     */
    public ResultSet getResultSet(String sql) {
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException ex) {
            err(ex);
        } finally {
            closeAfterExecute();
        }
        return null;
    }

    /**
     * 根据sql获取结果集
     *
     * @param sql sql
     * @return ResultSet
     * @throws SQLException SQLException
     */
    public ResultSet getResultSetWithException(String sql) throws SQLException {
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } finally {
            closeAfterExecute();
        }
    }

    /**
     * @param sql    sql
     * @param params 参数列表，按顺序写入sql
     * @return ResultSet
     * @see #getResultSetWithException(String, Object[])
     */
    public ResultSet getResultSet(String sql, Object[] params) {
        try {
            return getResultSetWithException(sql, params);
        } catch (SQLException e) {
            err(e);
        } finally {
            closeAfterExecute();
        }
        return null;
    }

    /**
     * 根据sql和参数获取结果集，如果参数数组为空或者为null，则调通过Statement获取结果集，反之通过PreparedStatement获取
     *
     * @param sql    sql
     * @param params 参数列表，按顺序写入sql
     * @return ResultSet
     * @throws SQLException sql执行异常
     */
    public ResultSet getResultSetWithException(String sql, Object[] params) throws SQLException {
        if (isNullOrEmpty(params)) {
            return getResultSet(sql);
        }
        try {
            PreparedStatement ps = getPreparedStatement(sql, params);
            return ps.executeQuery();
        } finally {
            closeAfterExecute();
        }
    }

    /**
     * @param sql sql
     * @return list
     * @see #queryList(String)
     */
    public List<Map<String, Object>> queryList(String sql) {
        try {
            return queryListWithException(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询多行记录, 通过Statement执行
     *
     * @param sql sql
     * @return list
     * @throws SQLException SQLException
     */
    public List<Map<String, Object>> queryListWithException(String sql) throws SQLException {
        ResultSet rs = getResultSet(sql);
        return convert2List(rs);
    }

    /**
     * @param sql    sql
     * @param params 参数
     * @return list
     * @see #queryList(String, Object[])
     */
    public List<Map<String, Object>> queryList(String sql, Object[] params) {
        try {
            return queryListWithException(sql, params);
        } catch (SQLException e) {
            err(e);
        }
        return null;
    }

    /**
     * 查询多行记录，带参数，通过PreparedStatement执行
     *
     * @param sql    sql
     * @param params 参数
     * @return list
     * @throws SQLException SQLException
     */
    public List<Map<String, Object>> queryListWithException(String sql, Object[] params) throws SQLException {
        ResultSet rs = getResultSetWithException(sql, params);
        return convert2List(rs);
    }

    /**
     * @param sql    sql
     * @param params 参数
     * @return 数据列表
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> queryObjectList(String sql, Object[] params, Class<T> clazz) {
        try {
            return queryObjectListWithException(sql, params, clazz);
        } catch (Exception ex) {
            err(ex);
        }
        return null;
    }

    /**
     * 获取指定类型的数据列表
     *
     * @param sql    sql
     * @param params 参数
     * @param clazz  类型
     * @param <T>    类型T
     * @return list
     * @throws SQLException SQLException
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> queryObjectListWithException(String sql, Object[] params, Class<T> clazz) throws SQLException {

        List<T> list = null;
        ResultSet rs = getResultSetWithException(sql, params);
        if (isBasicClass(clazz)) {
            list = new ArrayList<>();
            while (rs.next()) {
                list.add((T) rs.getObject(1));
            }
        } else {
            list = convert2List(rs, clazz);
        }
        return list;

    }

    /**
     * @param sql sql
     * @return map
     * @see #getMapWithException(String)
     */
    public Map<String, Object> getMap(String sql) {
        try {
            return getMapWithException(sql);
        } catch (SQLException e) {
            err(e);
        }
        return null;
    }

    /**
     * 查询单行, 通过Statement执行
     *
     * @param sql sql
     * @return map
     */
    public Map<String, Object> getMapWithException(String sql) throws SQLException {
        List<Map<String, Object>> list = queryListWithException(sql);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * @param sql    sql
     * @param params 参数
     * @return map
     * @see #getMapWithException(String, Object[])
     */
    public Map<String, Object> getMap(String sql, Object[] params) {
        try {
            return getMapWithException(sql, params);
        } catch (SQLException e) {
            err(e);
        }
        return null;
    }

    /**
     * 查询单行,带参数，通过PreparedStatement执行
     *
     * @param sql    sql
     * @param params 参数
     * @return map
     * @throws SQLException SQLException
     */
    public Map<String, Object> getMapWithException(String sql, Object[] params) throws SQLException {
        if (isNullOrEmpty(params)) {
            return getMapWithException(sql);
        }
        List<Map<String, Object>> list = queryListWithException(sql, params);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取第一条记录的第一个值(待测)
     *
     * @param sql    sql
     * @param params 参数
     * @param clazz  类型
     * @return map
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(String sql, Object[] params, Class<T> clazz) {
        try {
            return getObjectWitchException(sql, params, clazz);
        } catch (Exception ex) {
            err(ex);
        }

        return null;
    }

    /**
     * 获取第一条记录的第一个值(待测)
     *
     * @param sql    sql
     * @param params 参数
     * @param clazz  类型
     * @param <T>    类型T
     * @return 查询结果
     * @throws SQLException SQLException
     */
    @SuppressWarnings("unchecked")
    public <T> T getObjectWitchException(String sql, Object[] params, Class<T> clazz) throws SQLException {
        ResultSet rs = getResultSetWithException(sql, params);
        if (rs.next()) {
            if (isBasicClass(clazz)) {
                return (T) rs.getObject(1);
            } else if (clazz == Object.class) {
                return (T) rs.getObject(1);
            } else {
                List<T> list = convert2List(rs, clazz);
                if (!list.isEmpty()) {
                    return list.get(0);
                }
            }
        }
        return null;
    }

    /**
     * @param sql    sql
     * @param params 参数
     * @return Integer
     * @see #getIntWitchException(String, Object[])
     */
    public Integer getInt(String sql, Object[] params) {
        try {
            return getIntWitchException(sql, params);
        } catch (Exception ex) {
            err(ex);
        }
        return null;
    }

    /**
     * 获取int类型的结果
     *
     * @param sql    sql
     * @param params 参数
     * @return Integer
     * @throws SQLException SQLException
     */
    public Integer getIntWitchException(String sql, Object[] params) throws SQLException {
        Object value = getObjectWitchException(sql, params, Object.class);
        if (value != null) {
            if (value instanceof Integer) {
                return (Integer) value;
            } else {
                return Integer.parseInt(value.toString());
            }
        }
        return null;
    }

    /**
     * @param sql    sql
     * @param params 参数
     * @return Double
     * @see #getDoubleWitchException(String, Object[])
     */
    public Double getDouble(String sql, Object[] params) {
        try {
            return getDoubleWitchException(sql, params);
        } catch (Exception ex) {
            err(ex);
        }
        return null;
    }

    /**
     * 获取Double类型的结果
     *
     * @param sql    sql
     * @param params 参数
     * @return Double
     * @throws SQLException SQLException
     */
    public Double getDoubleWitchException(String sql, Object[] params) throws SQLException {
        Object value = getObjectWitchException(sql, params, Object.class);
        if (value != null) {
            if (value instanceof Double) {
                return (Double) value;
            } else {
                return Double.parseDouble(value.toString());
            }
        }
        return null;
    }

    /**
     * @param sql    sql
     * @param params 参数
     * @return String
     * @see #getStrWitchException(String, Object[])
     */
    public String getStr(String sql, Object[] params) {
        try {
            return getStrWitchException(sql, params);
        } catch (Exception ex) {
            err(ex);
        }
        return null;
    }

    /**
     * 获取String类型的结果
     *
     * @param sql    sql
     * @param params 参数
     * @return String
     * @throws SQLException SQLException
     */
    public String getStrWitchException(String sql, Object[] params) throws SQLException {
        Object value = getObjectWitchException(sql, params, Object.class);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    /**
     * update语句
     *
     * @param sql sql
     * @return int
     */
    public int update(String sql) {
        try {
            return updateWithException(sql);
        } catch (SQLException ex) {
            err(ex);
        }
        return -1;
    }

    /**
     * update语句
     *
     * @param sql sql
     * @return 影响记录数
     * @throws SQLException SQLException
     */
    public int updateWithException(String sql) throws SQLException {
        Statement stmt = null;
        int row = -1;
        try {
            stmt = conn.createStatement();
            row = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            closeAfterExecute();
        }

        return row;
    }

    /**
     * update语句(带参数)
     *
     * @param sql    sql
     * @param params 参数
     * @return 影响记录数
     */
    public int update(String sql, Object[] params) {
        try {
            return updateWithException(sql, params);
        } catch (SQLException ex) {
            err(ex);
        }
        return -1;
    }

    /**
     * update语句(带参数)
     *
     * @param sql    sql
     * @param params 参数
     * @return 影响记录数
     * @throws SQLException SQLException
     */
    public int updateWithException(String sql, Object[] params) throws SQLException {
        if (isNullOrEmpty(params)) {
            return updateWithException(sql);
        }
        try {
            PreparedStatement pstmt = getPreparedStatement(sql, params);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            closeAfterExecute();
        }
    }

    /**
     * 清空表数据(注意这个是ddl操作，不能回滚)
     *
     * @param tableName 表名
     * @return 是否成功?
     */
    public boolean truncate(String tableName) {
        String sql = "TRUNCATE TABLE " + tableName;
        return update(sql) != -1;
    }

    /**
     * 插入一条数据并获取对应的主键(自增长),如果失败，返回-1
     *
     * @param sql    sql
     * @param params 参数
     * @return 影响记录数
     */
    public int insertAndGetKey(String sql, Object[] params) {
        try {
            return insertAndGetKeyWithException(sql, params);
        } catch (SQLException ex) {
            err(ex);
        }
        return -1;
    }

    /**
     * 插入并获取自增的主键
     *
     * @param sql    sql
     * @param params 参数
     * @return 主键
     * @throws SQLException SQLException
     */
    public int insertAndGetKeyWithException(String sql, Object[] params) throws SQLException {
        try {
            PreparedStatement pstmt = getPreparedStatement(sql, params, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            int key = rs.getInt(1);
            return key;
        } catch (SQLException ex) {
            throw ex;
        } finally {
            closeAfterExecute();
        }
    }

    /**
     * 批量update(sync方法)
     *
     * @param sql        sql
     * @param paramsList 参数列表
     * @return 执行结果
     */
    public int[] batchUpdate(String sql, List<Object[]> paramsList) {
        try {
            return batchUpdateWithException(sql, paramsList);
        } catch (SQLException ex) {
            err(ex);
        }
        return null;
    }

    /**
     * 批量update(sync方法)
     *
     * @param sql        sql
     * @param paramsList 参数列表
     * @return 执行结果
     * @throws SQLException SQLException
     */
    public synchronized int[] batchUpdateWithException(String sql, List<Object[]> paramsList) throws SQLException {
        if (paramsList == null || paramsList.isEmpty()) {
            return new int[]{};
        }
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = getBatchPreparedStatement(sql, paramsList);
            int[] results = pstmt.executeBatch(); //批量执行
            conn.commit();//提交事务
            return results;
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
            }
            throw ex;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                err(ex);
            }
            closeAfterExecute();
        }
    }

    /**
     * 通过sql和参数列表获取PreparedStatement(批量)
     *
     * @param sql        sql语句
     * @param paramsList 参数列表
     * @return PreparedStatement
     * @throws SQLException sql异常
     */
    public PreparedStatement getBatchPreparedStatement(String sql, List<Object[]> paramsList) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (Object[] list : paramsList) {
            for (int i = 0; i < list.length; i++) {
                pstmt.setObject(i + 1, list[i]);
            }
            pstmt.addBatch();
        }
        return pstmt;
    }

    /*-----数据库结构层面的工具方法-----*/

    /**
     * 通过sql和参数列表获取PreparedStatement
     *
     * @param sql    sql语句
     * @param params 参数数组
     * @return PreparedStatement
     * @throws SQLException sql异常
     */
    public PreparedStatement getPreparedStatement(String sql, Object[] params) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object item = params[i];
                pstmt.setObject(i + 1, item);
            }
        }
        return pstmt;
    }

    public PreparedStatement getPreparedStatement(String sql, Object[] params, int autoGeneratedKeys) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql, autoGeneratedKeys);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object item = params[i];
                pstmt.setObject(i + 1, item);
            }
        }
        return pstmt;
    }

    /**
     * 获取数据库信息
     * <p>
     * 参考:http://blog.csdn.net/anxinliu2011/article/details/7560511
     * </p>
     *
     * @return DbPojo
     */
    public DbPojo dbInfo() {
        try {
            DbPojo db = new DbPojo();
            DatabaseMetaData dbmd = conn.getMetaData();
            db.setName(dbmd.getDatabaseProductName());        //MySQL
            db.setVersion(dbmd.getDatabaseProductVersion());    //5.6.32
            db.setDriverVersion(dbmd.getDriverVersion());            //mysql-connector-java-5.1.44 ( Revision: b3cda4f864902ffdde495b9df93937c3e20009be )
            return db;
        } catch (Exception ex) {
            err(ex);
        } finally {    //构造函数调用，所以无论如何都不关闭连接
//			closeAfterExecute();
        }
        return null;
    }

    /**
     * 获取所有表的名称列表
     *
     * @return list
     */
    public List<String> tableNameList() {
        try {
            DatabaseMetaData dbmd = conn.getMetaData();

            ArrayList<String> tableNameList = new ArrayList<>();
            ResultSet rs = null;
            String[] typeList = new String[]{"TABLE"};
            rs = dbmd.getTables(null, "%", "%", typeList);
            for (boolean more = rs.next(); more; more = rs.next()) {
                String s = rs.getString("TABLE_NAME");
                String type = rs.getString("TABLE_TYPE");
                if (type.equalsIgnoreCase("table") && s.indexOf("$") == -1) {
//	            	System.out.println(rs.getString("TABLE_CAT"));
//	            	System.out.println(rs.getString("TABLE_SCHEM"));
//	            	System.out.println(rs.getString("TABLE_NAME"));
//	            	System.out.println(rs.getString("TABLE_TYPE"));
//	            	System.out.println(rs.getString("REMARKS"));
//	            	System.out.println(rs.getString("TYPE_CAT"));
//	            	System.out.println(rs.getString("TYPE_SCHEM"));
//	            	System.out.println(rs.getString("TYPE_NAME"));
//	            	System.out.println(rs.getString("SELF_REFERENCING_COL_NAME"));
//	            	System.out.println(rs.getString("REF_GENERATION"));
                    tableNameList.add(s);
                }
            }
            return tableNameList;
        } catch (Exception ex) {
            err(ex);
        } finally {
            closeAfterExecute();
        }
        return null;
    }

    /**
     * 通过表名获取每一个字段的信息
     *
     * <p>
     * 参考:http://blog.sina.com.cn/s/blog_707a9f0601014y1y.html
     * </p>
     *
     * @param tableName 表名
     * @return list
     */
    public List<ColumnPojo> columnList(String tableName) {
        List<ColumnPojo> columns = new ArrayList<>();

        try {
            List<String> primaryKeyList = primaryKeyList(tableName);    //主键列表
            Map<String, TypePojo> typeMap = typeMap(tableName);
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet columnSet = dbmd.getColumns(null, "%", tableName, "%");

            while (columnSet.next()) {
                ColumnPojo column = new ColumnPojo();
                String columnName = columnSet.getString("COLUMN_NAME");
                if (primaryKeyList.contains(columnName)) {    //是否在主键列表里
                    column.isPK(true);
                } else {
                    column.isPK(false);
                }
                column.setName(columnName);
                column.setSqlType(columnSet.getInt("DATA_TYPE"));        //来自 java.sql.Types 的 SQL 类型
                column.setTypeName(columnSet.getString("TYPE_NAME"));    //数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
                column.setSize(columnSet.getInt("COLUMN_SIZE"));            //长度
                column.setDecimalDigits(columnSet.getInt("DECIMAL_DIGITS"));    //小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null
                column.setRemarks(columnSet.getString("REMARKS"));            //注释
                column.setDef(columnSet.getObject("COLUMN_DEF"));    //默认值，可以为null
                column.setCharOctetLength(columnSet.getInt("CHAR_OCTET_LENGTH"));    // 对于 char 类型，该长度是列中的最大字节数
                if (column.isPK()) {        //主键不允许为空
                    column.isNotNull(true);
                } else {
                    column.isNotNull(!columnSet.getBoolean("NULLABLE"));
                }
                column.isAutoIncrement(columnSet.getBoolean("IS_AUTOINCREMENT"));    //是否自增长
                column.setOrdinalPosition(columnSet.getInt("ORDINAL_POSITION"));        //表中的列的索引（从 1 开始）
                /*其他信息*/
                if (typeMap.containsKey(columnName)) {
                    column.setTypePojo(typeMap.get(columnName));
                }

                columns.add(column);
            }

            return columns;
        } catch (Exception ex) {
            err(ex);
        } finally {
            closeAfterExecute();
        }
        return null;
    }

    /**
     * 通过表名获取所有主键
     *
     * @param tableName 表名
     * @return list
     */
    public List<String> primaryKeyList(String tableName) {
        List<String> list = new ArrayList<>();
        try {
            ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                list.add(rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException ex) {
            err(ex);
        }
        return list;
    }

    /**
     * 获取字段类型列表
     * <p>
     * 包含类型/自增/主键等信息
     * </p>
     *
     * @param tableName 表名
     * @return map
     */
    public Map<String, TypePojo> typeMap(String tableName) {
        switch (dbType) {
            case DbPojo.TYPE_MYSQL:
                return typeMap_Mysql(tableName);
            case DbPojo.TYPE_SQLITE:
                return typeMap_Sqlite(tableName);
            case DbPojo.TYPE_ORACLE:
            default:
                return new HashMap<>();
        }
    }

    /**
     * @param tableName 表名
     * @return 字段类型列表(mysql)
     */
    private Map<String, TypePojo> typeMap_Mysql(String tableName) {
        Map<String, TypePojo> typeMap = new HashMap<>();
        List<Map<String, Object>> typeList = queryList("SHOW COLUMNS FROM `" + tableName + "`");    //有些表名带关键字会报错
        for (Map<String, Object> map : typeList) {
            String field = (String) map.get("Field");
            String type = (String) map.get("Type");
            Boolean nullAble = "YES".equals(map.get("Null"));
            String extra = (String) map.get("Extra");
            String key = (String) map.get("Key");
            typeMap.put(field, new TypePojo().setField(field).setType(type).setNullAble(nullAble).setExtra(extra).setKey(key));
        }
        return typeMap;
    }

    /**
     * @param tableName 表名
     * @return 字段类型列表(mysql)
     */
    private Map<String, TypePojo> typeMap_Sqlite(String tableName) {
        Map<String, TypePojo> typeMap = new HashMap<>();
        List<Map<String, Object>> typeList = queryList("pragma table_info( " + tableName + ");");
        for (Map<String, Object> map : typeList) {
            String field = (String) map.get("name");
            String type = (String) map.get("type");
            Boolean nullAble = map.get("notnull").equals(0);
//			String extra = (String) map.get("Extra");
//			String key = (String) map.get("Key");
            typeMap.put(field, new TypePojo().setField(field).setType(type).setNullAble(nullAble));
        }
        return typeMap;
    }

    /**
     * 获取所有表结构
     *
     * @return 字段类型列表
     */
    public Map<String, List<ColumnPojo>> tableColumnMap() {
        return tableNameList().stream()
                .collect(Collectors.toMap(tableName -> tableName, tableName -> columnList(tableName)));
    }

    /*----内部工具方法------*/

    /**
     * 获取表中的索引信息
     * <p>
     * 包括主键索引,索引名为为PRIMARY
     * 参考:http://blog.csdn.net/uikoo9/article/details/39926687
     * </p>
     *
     * @param tableName 表名
     * @return list
     */
    public List<DBIPojo> dbiList(String tableName) {
        List<DBIPojo> dbiList = new ArrayList<>();
        try {
            ResultSet rs = conn.getMetaData().getIndexInfo(null, null, tableName, false, false);
            Map<String, Integer> tempMap = new HashMap<>();
            int index = -1;
            while (rs.next()) {
                // 获取索引名
                String name = rs.getString("INDEX_NAME");                //索引名称
                String colName = rs.getString("COLUMN_NAME");    //索引列名称
//				if("PRIMARY".equals(name)) {	//过滤主键索引
//					continue;
//				}

                if (tempMap.containsKey(name)) {
                    dbiList.get(tempMap.get(name)).getColumnNameList().add(colName);
                    continue;
                } else {
                    int type = rs.getInt("TYPE");

                    DBIPojo dbi = new DBIPojo();
                    dbi.setName(name);
                    dbi.setType(type);    //索引类型
                    switch (type) {
                        case 2:
                            dbi.setTypeName("HASH");
                            break;
                        case 3:
                            dbi.setTypeName("BTREE");
                            break;
                        default:
                            break;
                    }
                    dbi.setUnique(!rs.getBoolean("NON_UNIQUE"));

                    List<String> colNameList = new ArrayList<>();
                    colNameList.add(colName);
                    dbi.setColumnNameList(colNameList);
                    dbiList.add(dbi);

                    index++;
                    tempMap.put(name, index);
                }

            }
            tempMap = null;
            return dbiList;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return dbiList;
    }

    /**
     * @param tableName 表名
     * @return 数据库里是否存在某张表
     * @throws SQLException 可能是连接数据库异常,所以不能确定是否存在表
     */
    public boolean isTableExisted(String tableName) throws SQLException {
        ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null);
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param params 参数
     * @return 是否为空指针或空字符串
     */
    private boolean isNullOrEmpty(Object[] params) {
        return params == null || params.length == 0;
    }

    /**
     * 执行完sql后关闭数据库连接
     */
    private void closeAfterExecute() {
        if (MODE_CLOSE_AFTER_EXECUTE) {
            dispose();
        }

    }

    /**
     * 关闭数据库连接.释放资源
     * <p>
     * 和dispose()的作用相同
     * </p>
     */
    @Override
    public void close() throws IOException {
        dispose();
    }

    /**
     * 关闭数据库连接.释放资源
     * <p>
     * 和close()的作用相同
     * </p>
     */
    @Override
    public void dispose() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn = null;
    }
}