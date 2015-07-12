package com.fd.dao.base.bean;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import com.fd.dao.base.common.Condition;
import com.fd.dao.base.common.OrderBy;
import com.fd.dao.base.em.MultiJoinTp;

/**
 * 多表联结查询信息
 * 
 * @author 符冬
 * 
 */
public class JoinQuery implements Serializable {
	private static final long serialVersionUID = -3956564770170496532L;
	/**
	 * 表名
	 */
	private String table;
	/***
	 * 联结类型
	 */
	private MultiJoinTp multiJoinTp = MultiJoinTp.INNER;
	/**
	 * select 语句
	 */
	private Set<Cuery> cuerys;
	/**
	 * 关联字段
	 */
	private String[] midcm;
	/**
	 * 条件
	 */
	private Set<Condition> conditions;
	/**
	 * 排序字段
	 */
	private LinkedHashSet<OrderBy> orderbys;
	/***
	 * 分组字段
	 */
	private String[] groupbys;
	/***
	 * 关联其他表信息
	 */
	private JoinQuery joinQuery;

	public String getTable() {
		return table;
	}

	public JoinQuery() {
		super();
	}

	public JoinQuery(String table, MultiJoinTp multiJoinTp, String[] midcm) {
		super();
		this.table = table;
		this.multiJoinTp = multiJoinTp;
		this.midcm = midcm;
	}

	public JoinQuery(String table) {
		super();
		this.table = table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public MultiJoinTp getMultiJoinTp() {
		return multiJoinTp;
	}

	public void setMultiJoinTp(MultiJoinTp multiJoinTp) {
		this.multiJoinTp = multiJoinTp;
	}

	public JoinQuery(String table, MultiJoinTp multiJoinTp, Set<Cuery> cuerys,
			String[] midcm, Set<Condition> conditions,
			LinkedHashSet<OrderBy> orderbys, String[] groupbys,
			JoinQuery joinQuery) {
		super();
		this.table = table;
		this.multiJoinTp = multiJoinTp;
		this.cuerys = cuerys;
		this.midcm = midcm;
		this.conditions = conditions;
		this.orderbys = orderbys;
		this.groupbys = groupbys;
		this.joinQuery = joinQuery;
	}

	public Set<Cuery> getCuerys() {
		return cuerys;
	}

	public void setCuerys(Set<Cuery> cuerys) {
		this.cuerys = cuerys;
	}

	public String[] getMidcm() {
		return midcm;
	}

	public void setMidcm(String[] midcm) {
		this.midcm = midcm;
	}

	public Set<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(Set<Condition> conditions) {
		this.conditions = conditions;
	}

	public LinkedHashSet<OrderBy> getOrderbys() {
		return orderbys;
	}

	public void setOrderbys(LinkedHashSet<OrderBy> orderbys) {
		this.orderbys = orderbys;
	}

	public String[] getGroupbys() {
		return groupbys;
	}

	public void setGroupbys(String[] groupbys) {
		this.groupbys = groupbys;
	}

	public JoinQuery getJoinQuery() {
		return joinQuery;
	}

	public void setJoinQuery(JoinQuery joinQuery) {
		this.joinQuery = joinQuery;
	}

}
