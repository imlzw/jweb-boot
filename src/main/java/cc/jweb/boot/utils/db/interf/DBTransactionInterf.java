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
