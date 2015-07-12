package com.fd.dao.base.bean;

import java.io.Serializable;

/**
 * 关联字段
 * 
 * @author 符冬
 * 
 */
public class Midcm implements Serializable {
	private static final long serialVersionUID = 8965576785959237311L;
	/***
	 * 关联表
	 */
	private String[] tables;
	/**
	 * 表之间的关联字段对
	 */
	private String[] cnames;

	public String[] getTables() {
		return tables;
	}

	public void setTables(String[] tables) {
		this.tables = tables;
	}

	public String[] getCnames() {
		return cnames;
	}

	public void setCnames(String[] cnames) {
		this.cnames = cnames;
	}

	public Midcm(String[] tables, String[] cnames) {
		super();
		this.tables = tables;
		this.cnames = cnames;
	}

	public static String[] getStrs(String... strings) {
		return strings;
	}

	public static Midcm getMidCm(String... strings) {
		if (strings != null && strings.length > 0 && strings.length % 2 == 0) {
			String[] tbs = new String[strings.length / 2];
			String[] cms = new String[strings.length / 2];
			int j = 0;
			for (int i = 0; i < strings.length; i++) {
				tbs[j] = strings[i];
				cms[j++] = strings[++i];
			}
			return new Midcm(tbs, cms);
		} else {
			throw new RuntimeException("参数错误，不能创建Midcm对象");
		}
	}
}
