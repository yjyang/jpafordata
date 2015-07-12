package com.fd.dao.base.bean;

import java.io.Serializable;

import com.fd.dao.base.em.SType;

/**
 * select 语句
 * 
 * @author 符冬
 * 
 */
public class Cuery implements Serializable{
	private static final long serialVersionUID = -8992318893438214132L;
	/**
	 * 类型
	 */
	private SType sType = SType.FIELD;
	/***
	 * 字段名称
	 */
	private String cname;
	/***
	 * 聚集函数名称
	 */
	private String fname;
	/**
	 * 是否自动加上字段限定前缀如果使用false 可以查询 SUM(1) 给 cname=1 fname=sum
	 */
	private Boolean isPre = true;
	/**
	 * 表名
	 */
	private String table;

	public Cuery(String cname) {
		super();
		this.cname = cname;
	}

	/**
	 * 
	 * @param cname
	 *            字段
	 * @param fname
	 *            函数
	 */
	public Cuery(String cname, String fname) {
		super();
		this.cname = cname;
		this.fname = fname;
		this.sType = SType.FUNTION;
	}

	/**
	 * 
	 * @param sType
	 *            类型
	 * @param cname
	 *            字段
	 * @param fname
	 *            函数
	 */
	public Cuery(SType sType, String cname, String fname) {
		super();
		this.sType = sType;
		this.cname = cname;
		this.fname = fname;
	}

	/**
	 * 
	 * @param sType
	 *            类型
	 * @param cname
	 *            字段
	 * @param fname
	 *            函数
	 * @param isPre
	 *            是否需要限定表名
	 */
	public Cuery(SType sType, String cname, String fname, Boolean isPre) {
		super();
		this.sType = sType;
		this.cname = cname;
		this.fname = fname;
		this.isPre = isPre;
	}

	/**
	 * 
	 * @param sType
	 *            类型
	 * @param cname
	 *            字段
	 * @param fname
	 *            函数
	 * @param isPre
	 *            是否需要限定表名
	 * @param table
	 *            表名
	 */
	public Cuery(SType sType, String cname, String fname, Boolean isPre,
			String table) {
		super();
		this.sType = sType;
		this.cname = cname;
		this.fname = fname;
		this.isPre = isPre;
		this.table = table;
	}

	public SType getsType() {
		return sType;
	}

	/**
	 * 
	 * @param sType
	 *            类型
	 * @param cname
	 *            字段
	 * @param fname
	 *            函数
	 * @param table
	 *            表名
	 */
	public Cuery(SType sType, String cname, String fname, String table) {
		super();
		this.sType = sType;
		this.cname = cname;
		this.fname = fname;
		this.table = table;
	}

	public void setsType(SType sType) {
		this.sType = sType;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Boolean getIsPre() {
		return isPre;
	}

	public void setIsPre(Boolean isPre) {
		this.isPre = isPre;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	/**
	 * 获取当前对象的的数组
	 * 
	 * @param cueries
	 * @return
	 */
	public static Cuery[] getCuerys(Cuery... cueries) {
		return cueries;
	}

	/**
	 * 获取查询字段数组
	 * 
	 * @param strings
	 *            字段名
	 * @return
	 */
	public static Cuery[] getCuerys(String... strings) {
		Cuery[] arr = new Cuery[strings.length];
		for (int i = 0; i < strings.length; i++) {
			arr[i] = new Cuery(strings[i]);
		}
		return arr;
	}
}
