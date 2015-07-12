package com.fd.dao.base.em;

/**
 * 条件类型
 * 
 * @author 符冬
 * 
 */
public enum CdType {
	/**
	 * 原生类型
	 * 
	 * set amount=amount+1
	 */
	OG,
	/**
	 * 值类型
	 * 
	 * where amount !<100
	 */
	VL,
	/**
	 * 函数
	 * 
	 * having count(amount)>20
	 */
	FUN
}
