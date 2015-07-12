package com.fd.dao.base.bean;

import java.io.Serializable;

/**
 * 分组
 * 
 * @author 符冬
 * 
 */
public class GroupBy implements Serializable {
	private static final long serialVersionUID = -234086099383337157L;
	/**
	 * 表
	 */
	private String table;
	/**
	 * 字段
	 */
	private String prop;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

	public GroupBy() {
		super();
	}

	public GroupBy(String table, String prop) {
		super();
		this.table = table;
		this.prop = prop;
	}

}
