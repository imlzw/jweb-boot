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

/**
 * 数据库字段信息存放pojo
 * 
 * @author ag777
 * @version 
 */
public class ColumnPojo implements Cloneable{

	private String name;				//字段名
	private Integer sqlType;		//sql类型
	private String typeName;		//数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
	private Integer size;					//字段长度
	private Integer decimalDigits;	//小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null
	private String remarks;			//注释说明
	private Object def;			//默认值
	private Integer charOctetLength;	//对于 char 类型，该长度是列中的最大字节数
	private Boolean isNotNull;	//是否不能为空
	private Boolean isAutoIncrement;	//是否自增
	private Integer ordinalPosition;	//表中的列的索引（从 1 开始）
	private Boolean isPK;			//是否是主键
	
	private TypePojo typePojo;	//通过SHOW COLUMNS FROM xxx获取的额外信息
	
	
	public String getName() {
		return name;
	}
	public ColumnPojo setName(String name) {
		this.name = name;
		return this;
	}
	public Integer getSqlType() {
		return sqlType;
	}
	public ColumnPojo setSqlType(Integer sqlType) {
		this.sqlType = sqlType;
		return this;
	}
	public String getTypeName() {
		return typeName;
	}
	public ColumnPojo setTypeName(String typeName) {
		this.typeName = typeName;
		return this;
	}
	public Integer getSize() {
		return size;
	}
	public ColumnPojo setSize(Integer size) {
		this.size = size;
		return this;
	}
	public Integer getDecimalDigits() {
		return decimalDigits;
	}
	public ColumnPojo setDecimalDigits(Integer decimalDigits) {
		this.decimalDigits = decimalDigits;
		return this;
	}
	public String getRemarks() {
		return remarks;
	}
	public ColumnPojo setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
	public Object getDef() {
		return def;
	}
	public ColumnPojo setDef(Object def) {
		this.def = def;
		return this;
	}
	public Integer getCharOctetLength() {
		return charOctetLength;
	}
	public ColumnPojo setCharOctetLength(Integer charOctetLength) {
		this.charOctetLength = charOctetLength;
		return this;
	}
	public Boolean isNotNull() {
		return isNotNull;
	}
	public ColumnPojo isNotNull(Boolean isNotNull) {
		this.isNotNull = isNotNull;
		return this;
	}
	public Boolean isAutoIncrement() {
		return isAutoIncrement;
	}
	public ColumnPojo isAutoIncrement(Boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
		return this;
	}
	public Integer getOrdinalPosition() {
		return ordinalPosition;
	}
	public ColumnPojo setOrdinalPosition(Integer ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
		return this;
	}
	public Boolean isPK() {
		return isPK;
	}
	public ColumnPojo isPK(Boolean isPK) {
		this.isPK = isPK;
		return this;
	}
	
	public TypePojo getTypePojo() {
		return typePojo;
	}
	public ColumnPojo setTypePojo(TypePojo typePojo) {
		this.typePojo = typePojo;
		return this;
	}
	
	@Override
	public ColumnPojo clone() {
		ColumnPojo result = new ColumnPojo()
				.setCharOctetLength(charOctetLength)
				.setDecimalDigits(decimalDigits)
				.setDef(def)
				.setName(name)
				.setOrdinalPosition(ordinalPosition)
				.setRemarks(remarks)
				.setSize(size)
				.setSqlType(sqlType)
				.setTypeName(typeName)
				.isAutoIncrement(isAutoIncrement)
				.isNotNull(isNotNull)
				.isPK(isPK);
		if(typePojo != null) {
			result.setTypePojo(
					new TypePojo()
						.setExtra(typePojo.getExtra())
						.setField(typePojo.getField())
						.setKey(typePojo.getKey())
						.setNullAble(typePojo.getNullAble())
						.setType(typePojo.getType()));
		}
		return result;		
	}
}
