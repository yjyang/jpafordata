package com.fd.dao.base.em;

/**
 * SELECT语句类型
 * 
 * @author 符冬
 * 
 */
public enum SType {
	/**
	 * 子查询
	 */
	SUBQUERY,
	/***
	 * 字段
	 */
	FIELD,
	/**
	 * 聚集函数
	 */
	FUNTION
}
