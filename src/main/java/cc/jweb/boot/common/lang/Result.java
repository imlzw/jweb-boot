package cc.jweb.boot.common.lang;


import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * 结果集对象
 * @author LinCH
 *
 */
@SuppressWarnings("unchecked")
public class Result extends LinkedHashMap<String,Object> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4605513506305192535L;
	/**
	 * 定义静态变量
	 */
	/* 成功标识 */
	public static String SUCCESS_KEY = "success";
	/* 失败标识 */
	public static String ERROR_KEY = "error";
	/* 列表的数据存储key */
	public static String LIST_DATA_KEY = "data";
	/* 列表的数据总量key */
	public static String LIST_TOTAL_KEY = "total";
	/* 消息提示key */
	public static String MESSAGE_KEY = "message";
	/* 内容主体key */
	public static String DATA_KEY = "data";
	/* 特殊的key用于记录系统日志 */
	public static String SYSLOG_KEY = "_syslog_";
	
	public Result(Map<String,Object> map){
		this.putAll(map);
	}
	
	/**
	 * 默认构造函数
	 */
	public Result(){
		this.put(SUCCESS_KEY, true);
		this.put(MESSAGE_KEY, "");
	}

	public Result(boolean success){
		this.put(SUCCESS_KEY, success);
	}

	/**
	 * 简单提示性构造函数
	 * @param success	成功标识（true | false）
	 * @param message	提示消息
	 */
	public Result(boolean success,String message){
		this.put(SUCCESS_KEY, success);
		this.put(MESSAGE_KEY, message);
//		if(success == false){
//			this.put("error", new Error(message));
//		}
	}
	
	/**
	 * 简单错误提示
	 * @param error （club.codeapes.web.springmvc.model.Error）
	 */
	public Result(Error error){
		this.put(SUCCESS_KEY, false);
		this.put(ERROR_KEY, error);
		this.put(MESSAGE_KEY, error.getMessage());
	}
	/**
	 * 设置错误
	 * @param error
	 * @return
	 */
	public Result setError(Error error){
		this.put(SUCCESS_KEY, false);
		this.put(ERROR_KEY, error);
		this.put(MESSAGE_KEY, error.getMessage());
		return this;
	}

	public Result setException(Throwable t) {
		this.put(ERROR_KEY,  t.getMessage());
		return this;
	}
	
	/**
	 * 设置数据
	 * @param data
	 * @return
	 */
	public Result setData(Object data){
		this.put(DATA_KEY, data);
		return this;
	}
	
	/**
	 * 简单设置列表结果集方法
	 * @param list
	 * @return
	 */
	public Result setListData(Object list){
		this.put(LIST_DATA_KEY, list);
		if(list.getClass().isArray()){
			this.put(LIST_TOTAL_KEY, list != null ? ((Object[])list).length : 0);
		}else if(list instanceof List){
			this.put(LIST_TOTAL_KEY, list != null ? ((List<Object>)list).size() : 0);
		}
		return this;
	}
	/**
	 * 设置列表结果集合方法
	 * @param list
	 * @return
	 */
	public Result setListData(List<Object> list){
		this.put(LIST_DATA_KEY, list);
		this.put(LIST_TOTAL_KEY, list!=null?list.size():0);
		return this;
	}
	/**
	 * 设置数据结果集
	 * @param list
	 * @return
	 */
	public Result setArrayData(Object[] list){
		this.put(LIST_DATA_KEY, list);
		this.put(LIST_TOTAL_KEY, list!=null?list.length:0);
		return this;
	}
	/**
	 * 设置空列表对象
	 * @return
	 */
	public Result setEmptyListData(){
		this.put(LIST_DATA_KEY, new ArrayList<Object>());
		this.put(LIST_TOTAL_KEY, 0);
		return this;
	}
	/**
	 * 简单设置列表记录数量方法
	 * @param total
	 * @return
	 */
	public Result setListTotal(int total){
		this.put(LIST_TOTAL_KEY, total);
		return this;
	}
	/**
	 * 简单设置列表记录数量方法
	 * @param total
	 * @return
	 */
	public Result setListTotal(long total){
		this.put(LIST_TOTAL_KEY, total);
		return this;
	}
	/**
	 * 简单设置列表记录数量方法
	 * @param total
	 * @return
	 */
	public Result setListTotal(BigDecimal total){
		this.put(LIST_TOTAL_KEY, total == null ? 0 : total);
		return this;
	}
	
	public Error getError(){
		return (Error) this.get(ERROR_KEY);
	}
	
	public Map<String,Object> getData(){
		Map<String,Object> data = (Map<String, Object>) this.get(DATA_KEY);
		if(data == null){
			this.put(DATA_KEY, new HashMap<String,Object>());
		}
		return (Map<String, Object>) this.get(DATA_KEY);
	}
	
	public List<Map<String,Object>> getListData(){
		return (List<Map<String, Object>>) this.get(LIST_DATA_KEY);
	}
	/**
	 * 链式方式设置其他值
	 * @param key
	 * @param value
	 * @return
	 */
	public Result set(String key, Object value){
		this.put(key, value);
		return this;
	}
	
	public Result setSuccess(boolean bl){
		this.put(SUCCESS_KEY, bl);
		return this;
	}
	
	public boolean isSuccess(){
		return MapUtils.getBooleanValue(this, SUCCESS_KEY,false);
	}
	
	public Result setMessage(String message){
		this.put(MESSAGE_KEY, message);
		return this;
	}
	
	public String getMessage(){
		return getMessage(MESSAGE_KEY);
	}
	
	public String getMessage(String messageKey){
		if(this.get(messageKey)!=null){
			return this.get(messageKey).toString();
		}else{
			return "";
		}
	}
	
	public Result removeError(){
		this.remove(ERROR_KEY);
		return this;
	}
	
	public Result setSyslog(){
		return setSyslog(this.isSuccess(), this.getMessage());
	}
	
	public Result setSyslog(boolean success){
		return setSyslog(success, this.getMessage());
	}
	
	public Result setSyslog(String message){
		return setSyslog(this.isSuccess(), message);
	}

	/**
	 * 设置message到syslog里
	 * @return 自身
	 */
	public Result setSyslogAsMessage() {
		return setSyslog(this.getMessage());
	}
	
	public Result setSyslog(boolean success, String message){
		Object obj = this.get(SYSLOG_KEY);
		if(obj == null){
			this.put(SYSLOG_KEY, new HashMap<String,Object>());
			obj = this.get(SYSLOG_KEY);
		}
		Map<String,Object> syslog = (Map<String,Object>)obj;
		syslog.put("success", success);
		syslog.put("message", message);
		return this;
	}
	
	public static void main(String[] args) {
		
		/*Result result = new Result(false,"错误了").set("a", "a").set("cc", "v").set("ss", "sdf");
		result.put("other", 1);
		//System.out.println(new Gson().toJson(result.setListData(new ArrayList()).setListTotal(2222)));
		result.clear();
		
		Object[] a = new Object[]{};
		System.out.println(a.getClass());*/
		Result result = new Result();
	}
}
