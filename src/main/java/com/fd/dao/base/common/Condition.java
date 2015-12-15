package com.fd.dao.base.common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fd.dao.base.em.CdType;
import com.fd.dao.base.em.Operators;
import com.fd.dao.base.em.SqlOp;
import com.fd.util.MyUtils;

/**
 * 查询条件对象
 * 
 * @author 符冬
 * 
 */
public final class Condition implements Serializable {

	private static final long serialVersionUID = 5388413972179570484L;
	// /属性名称查询条件名称
	private String property;
	// 条件规则
	private Operators oper = Operators.EQ;
	// 条件设置的值
	private Object value;
	// between查询条件的第一个参数
	private Object firstValue;
	// 条件类型
	private CdType cdType = CdType.VL;
	// 函数复合条件
	private String funName;
	// 子查询条件，如果不为空就会当成第次要查询条件
	private SqlWhere sw;
	// 表名或实体名
	private String table;
	// 重复字段查询自动混淆标识
	private String cplx = "";
	/**
	 * 是否自动加上字段限定前缀如果使用false 可以查询 SUM(1) 给 cname=1 fname=sum
	 */
	private Boolean isPre = true;

	public String getCplx() {
		return cplx;
	}

	/**
	 * between
	 * 
	 * @param value
	 * @param property
	 * @param firstValue
	 */
	public Condition(Object firstValue, String property, Object value) {
		super();
		this.property = property;
		this.value = value;
		this.firstValue = firstValue;
		this.oper = Operators.BETWEEN;
	}

	public Condition(String property, Operators oper, Object value,
			SqlWhere sw, String cplx) {
		super();
		this.property = property;
		this.oper = oper;
		likehandler(oper, value);
		this.sw = sw;
		this.cplx = cplx;
	}

	public Condition(String property, Operators oper, Object value,
			SqlWhere sw, String table, String cplx) {
		super();
		this.property = property;
		this.oper = oper;
		likehandler(oper, value);
		this.sw = sw;
		this.table = table;
		this.cplx = cplx;
	}

	public void setCplx(String cplx) {
		this.cplx = cplx;
	}

	public Condition(String property, Operators oper, Object value,
			Set<Condition> orConditions) {
		super();
		this.property = property;
		this.oper = oper;
		likehandler(oper, value);
		this.sw = new SqlWhere(SqlOp.OR, orConditions);
	}

	public SqlWhere getSw() {
		return sw;
	}

	public void setSw(SqlWhere sw) {
		this.sw = sw;
	}

	public Condition(String property, Operators oper, Object value,
			String funName, Set<Condition> orConditions, Boolean isPre) {
		super();
		this.property = property;
		this.oper = oper;
		likehandler(oper, value);
		this.funName = funName;
		this.sw = new SqlWhere(SqlOp.OR, orConditions);
		this.isPre = isPre;
	}

	public Boolean getIsPre() {
		return isPre;
	}

	public void setIsPre(Boolean isPre) {
		this.isPre = isPre;
	}

	public Condition(String property, Operators oper, Object value, String table) {
		super();
		this.property = property;
		this.oper = oper;
		likehandler(oper, value);
		this.table = table;
	}

	/***
	 * 函数复合类型条件
	 * 
	 * @param property
	 *            函数统计的属性
	 * @param oper
	 *            <请不要使用 like、 in、between,使用也没有意义，请用其他代替>
	 * @param value
	 *            比较的值
	 * @param funName
	 *            函数名称
	 */
	public Condition(String funName, String property, Operators oper,
			Object value) {
		super();
		this.property = property;
		this.oper = oper;
		likehandler(oper, value);
		this.cdType = CdType.FUN;
		this.funName = funName;
	}

	/**
	 * 除between之外
	 * 
	 * @param property
	 * @param oper
	 * @param value
	 */
	public Condition(String property, Operators oper, Object value) {
		this.property = property;
		this.oper = oper;
		likehandler(oper, value);
	}

	/**
	 * 模糊查询条件
	 * 
	 * @param property
	 * @param value
	 * @param isLeft
	 *            true 左边模糊，否则右模糊
	 */
	public Condition(String property, String value, boolean isLeft) {
		this.property = property;
		this.oper = Operators.LIKE;
		if (isLeft) {
			this.value = "%" + value;
		} else {
			this.value = value + "%";
		}
	}

	/***
	 * IN 条件集合
	 * 
	 * @param property
	 * @param values
	 */
	public <T> Condition(String property, List<T> values) {
		this.property = property;
		this.oper = Operators.IN;
		this.value = values;
	}

	/***
	 * 除between之外手动设置条件类型
	 * 
	 * @param property
	 * @param oper
	 * @param value
	 * @param cdType
	 */
	public Condition(String property, Operators oper, Object value,
			CdType cdType) {
		this.property = property;
		this.oper = oper;
		likehandler(oper, value);
		this.cdType = cdType;
	}

	private void likehandler(Operators oper, Object value) {
		if (Operators.LIKE.equals(oper)) {
			this.value = "%" + value + "%";
		} else {
			this.value = value;
		}
	}

	public String getFunName() {
		return funName;
	}

	public void setFunName(String funName) {
		this.funName = funName;
	}

	/**
	 * 得到数组
	 * 
	 * @param conditions
	 * @return
	 */
	public static Condition[] genArr(Condition... conditions) {
		return conditions;
	}

	public Condition() {
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Operators getOper() {
		return oper;
	}

	public void setOper(Operators oper) {
		this.oper = oper;
	}

	public CdType getCdType() {
		return cdType;
	}

	public void setCdType(CdType cdType) {
		this.cdType = cdType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(Object firstValue) {
		this.firstValue = firstValue;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cdType == null) ? 0 : cdType.hashCode());
		result = prime * result
				+ ((firstValue == null) ? 0 : firstValue.hashCode());
		result = prime * result + ((funName == null) ? 0 : funName.hashCode());
		result = prime * result + ((isPre == null) ? 0 : isPre.hashCode());
		result = prime * result + ((oper == null) ? 0 : oper.hashCode());
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		result = prime * result + ((sw == null) ? 0 : sw.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Condition other = (Condition) obj;
		if (cdType != other.cdType)
			return false;
		if (firstValue == null) {
			if (other.firstValue != null)
				return false;
		} else if (!firstValue.equals(other.firstValue))
			return false;
		if (funName == null) {
			if (other.funName != null)
				return false;
		} else if (!funName.equals(other.funName))
			return false;
		if (isPre == null) {
			if (other.isPre != null)
				return false;
		} else if (!isPre.equals(other.isPre))
			return false;
		if (oper != other.oper)
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		if (sw == null) {
			if (other.sw != null)
				return false;
		} else if (!sw.equals(other.sw))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/**
	 * 得到查询条件集合
	 * 
	 * @param conditions
	 * @return
	 */
	public final static Set<Condition> getConditions(Condition... conditions) {
		Set<Condition> condi = new HashSet<Condition>();
		if (MyUtils.isNotEmpty(conditions)) {
			for (Condition e : conditions) {
				condi.add(e);
			}
		}
		return condi;
	}
}
