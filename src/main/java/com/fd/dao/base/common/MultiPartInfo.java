package com.fd.dao.base.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 多表的查询信息
 * 
 * @author 符冬
 * 
 */
public class MultiPartInfo implements Serializable {
	private static final long serialVersionUID = 5686623742962286034L;
	// 表名
	private String tables;
	// 数据库字段名
	private String[] columns;
	// 查询条件
	private Condition[] conditions;
	// 外键知道名
	private String midcolumn;
	// 排序字段和排序规则
	private Map<String, Object> orderbys = new LinkedHashMap<String, Object>();

	public MultiPartInfo() {
	}

	public MultiPartInfo(String tables, String[] columns,
			Condition[] conditions, String midcolumn,
			Map<String, Object> orderbys) {
		this.tables = tables;
		this.columns = columns;
		this.conditions = conditions;
		this.midcolumn = midcolumn;
		this.orderbys = orderbys;
	}

	public String getTables() {
		return tables;
	}

	public void setTables(String tables) {
		this.tables = tables;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String... columns) {
		this.columns = columns;
	}

	public Condition[] getConditions() {
		return conditions;
	}

	public void setConditions(Condition... conditions) {
		this.conditions = conditions;
	}

	public void setConditionList(Set<Condition> conditionlist) {
		this.conditions = new Condition[conditionlist.size()];
		Iterator<Condition> ite = conditionlist.iterator();
		int i = 0;
		while (ite.hasNext()) {
			this.conditions[i++] = ite.next();
		}
	}

	public String getMidcolumn() {
		return midcolumn;
	}

	public void setMidcolumn(String midcolumn) {
		this.midcolumn = midcolumn;
	}

	public Map<String, Object> getOrderbys() {
		return orderbys;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tables == null) ? 0 : tables.hashCode());
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
		MultiPartInfo other = (MultiPartInfo) obj;
		if (tables == null) {
			if (other.tables != null)
				return false;
		} else if (!tables.equals(other.tables))
			return false;
		return true;
	}

	public void setOrderbys(Map<String, Object> orderbys) {
		this.orderbys = orderbys;
	}

	/****
	 * 多表链接查询关联信息
	 * 
	 * @param mpis
	 * @return
	 */
	public final static List<MultiPartInfo> getMultiPartInfos(
			MultiPartInfo... mpis) {
		return Arrays.asList(mpis);
	}
}
