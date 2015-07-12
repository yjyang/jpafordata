package com.fd.dao.base.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * 排序
 * 
 * @author 符冬
 * 
 */
public class OrderBy implements Serializable {
	private static final long serialVersionUID = -3764629560937076831L;
	// 排序的属性
	private String propertyName;
	// 如果以统计函数排序制定函数名称
	private String funName;
	// 是否降序排列
	private Boolean isDesc = true;
	// 排序的表名
	private String table;

	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((funName == null) ? 0 : funName.hashCode());
		result = prime * result + ((isDesc == null) ? 0 : isDesc.hashCode());
		result = prime * result
				+ ((propertyName == null) ? 0 : propertyName.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
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
		OrderBy other = (OrderBy) obj;
		if (funName == null) {
			if (other.funName != null)
				return false;
		} else if (!funName.equals(other.funName))
			return false;
		if (isDesc == null) {
			if (other.isDesc != null)
				return false;
		} else if (!isDesc.equals(other.isDesc))
			return false;
		if (propertyName == null) {
			if (other.propertyName != null)
				return false;
		} else if (!propertyName.equals(other.propertyName))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}

	public OrderBy(String propertyName, String funName, Boolean isDesc,
			String table) {
		super();
		this.propertyName = propertyName;
		this.funName = funName;
		this.isDesc = isDesc;
		this.table = table;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public OrderBy(String propertyName, Boolean isDesc, String table) {
		super();
		this.propertyName = propertyName;
		this.isDesc = isDesc;
		this.table = table;
	}

	public String getFunName() {
		return funName;
	}

	public void setFunName(String funName) {
		this.funName = funName;
	}

	public Boolean getIsDesc() {
		return isDesc;
	}

	public void setIsDesc(Boolean isDesc) {
		this.isDesc = isDesc;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public OrderBy(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	public OrderBy(String propertyName, Boolean isDesc) {
		super();
		this.propertyName = propertyName;
		this.isDesc = isDesc;
	}

	public OrderBy(String propertyName, String funName) {
		this.propertyName = propertyName;
		this.funName = funName;
	}

	public OrderBy(String propertyName, String funName, Boolean isDesc) {
		this.propertyName = propertyName;
		this.funName = funName;
		this.isDesc = isDesc;
	}

	public static LinkedHashSet<OrderBy> getSet(OrderBy... bies) {
		return new LinkedHashSet<>(Arrays.asList(bies));
	}
}
