package com.fd.dao.base.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.fd.dao.base.em.SqlOp;
import com.fd.util.MyUtils;

/**
 * 自定义SQL语句where部分封装
 * 
 * @date 2013-5-31
 * @author 符冬
 * 
 */
public final class SqlWhere implements Serializable {
	private static final long serialVersionUID = 5596710585007815486L;
	/**
	 * 操作
	 */
	private SqlOp sqlOp = SqlOp.AND;
	/**
	 * 条件集
	 */
	private Condition[] conditions;

	public SqlOp getSqlOp() {
		return sqlOp;
	}

	/**
	 * 得到动态数组
	 * 
	 * @param sqlWheres
	 * @return
	 */
	public static SqlWhere[] gen(SqlWhere... sqlWheres) {
		return sqlWheres;
	}

	public Condition[] getConditions() {
		return conditions;
	}

	public List<Condition> getConditionList() {
		return Arrays.asList(conditions);
	}

	public void setConditions(Condition... conditions) {
		this.conditions = conditions;
	}

	public SqlWhere() {
		super();
	}

	public void setSqlOp(SqlOp sqlOp) {
		this.sqlOp = sqlOp;
	}

	/***
	 * 默认采用AND
	 * 
	 * @param conditions
	 */
	public SqlWhere(Condition... conditions) {
		super();
		this.conditions = conditions;
	}

	/***
	 * 默认采用AND
	 * 
	 * @param conditions
	 */
	public SqlWhere(Set<Condition> conditions) {
		if (MyUtils.isNotEmpty(conditions)) {
			Condition[] arr = new Condition[conditions.size()];
			conditions.toArray(arr);
			this.conditions = arr;
		}
	}

	/**
	 * 生成条件
	 * 
	 * @param sqlOp
	 * @param conditions
	 */
	public SqlWhere(SqlOp sqlOp, Condition... conditions) {
		this.sqlOp = sqlOp;
		this.conditions = conditions;
	}

	/**
	 * 生成条件
	 * 
	 * @param sqlOp
	 * @param conditions
	 */
	public SqlWhere(SqlOp sqlOp, Set<Condition> conditions) {
		if (MyUtils.isNotEmpty(conditions)) {
			Condition[] arr = new Condition[conditions.size()];
			conditions.toArray(arr);
			this.sqlOp = sqlOp;
			this.conditions = arr;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(conditions);
		result = prime * result + ((sqlOp == null) ? 0 : sqlOp.hashCode());
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
		SqlWhere other = (SqlWhere) obj;
		if (!Arrays.equals(conditions, other.conditions))
			return false;
		if (sqlOp != other.sqlOp)
			return false;
		return true;
	}

}
