package com.fd.dao.base.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.Table;

import com.fd.dao.base.IBaseDao;
import com.fd.dao.base.common.Condition;
import com.fd.dao.base.common.OrderBy;
import com.fd.dao.base.common.PageInfo;
import com.fd.dao.base.common.SqlWhere;
import com.fd.dao.base.em.CdType;
import com.fd.dao.base.em.Operators;
import com.fd.dao.base.em.Sentences;
import com.fd.dao.base.em.SqlOp;
import com.fd.util.MyUtils;

public abstract class BaseDao<POJO> extends CommonDao implements IBaseDao<POJO> {

	private static final long serialVersionUID = 3225031490731385710L;

	private String getWhereSql(SqlWhere... sws) {
		if (MyUtils.isNotEmpty(sws)) {
			StringBuilder sbd = new StringBuilder("  where  ");
			int ix = 0;
			for (int i = 0; i < sws.length; i++) {
				SqlWhere sw = sws[i];
				if (MyUtils.isNotEmpty(sw.getConditions())) {
					Iterator<Condition> ite = Arrays.asList(sw.getConditions())
							.iterator();
					if (sw.getSqlOp().equals(SqlOp.OR)) {

						sbd.append("(");

					}

					while (ite.hasNext()) {
						Condition c = ite.next();
						if (c.getOper().equals(Operators.IN)
								|| c.getOper().equals(Operators.NOT_IN)) {

							sbd.append(alias).append(".")
									.append(c.getProperty())
									.append(c.getOper().getValue())
									.append("(?").append(ix + 1).append(") ");

						} else if (c.getOper().equals(Operators.BETWEEN)) {
							sbd.append(alias).append(".")
									.append(c.getProperty())
									.append(c.getOper().getValue()).append("?")
									.append(ix + 1).append(" and ").append("?")
									.append(++ix + 1);
						} else {

							if ((c.getOper().equals(Operators.EQ) || c
									.getOper().equals(Operators.NOT_EQ))
									&& !MyUtils.isNotEmpty(c.getValue())) {
								if (c.getOper().equals(Operators.EQ)) {
									if (isStr(c.getProperty())) {
										sbd.append(" (");
									}
									sbd.append(alias)
											.append(".")
											.append(c.getProperty())
											.append(Sentences.IS_NULL
													.getValue());
									if (isStr(c.getProperty())) {
										sbd.append(Sentences.OR.getValue());
										sbd.append(alias).append(".")
												.append(c.getProperty())
												.append(c.getOper().getValue())
												.append("''");
									}
									if (isStr(c.getProperty())) {
										sbd.append(")  ");
									}

								} else {

									sbd.append(alias)
											.append(".")
											.append(c.getProperty())
											.append(Sentences.IS_NOT_NULL
													.getValue());
									if (isStr(c.getProperty())) {
										sbd.append(Sentences.AND.getValue());

										sbd.append(alias).append(".")
												.append(c.getProperty())
												.append(c.getOper().getValue())
												.append("''");
									}

								}

							} else {
								sbd.append(alias).append(".")
										.append(c.getProperty())
										.append(c.getOper().getValue())
										.append("?").append(ix + 1);
							}
						}

						if (ite.hasNext()) {
							sbd.append(sw.getSqlOp().getValue());
						}
						ix++;
					}

					if (sw.getSqlOp().equals(SqlOp.OR)) {
						sbd.append(")");
					}
					if (i < sws.length - 1) {
						sbd.append(" and ");
					}
				}

			}
			return sbd.toString();

		} else {

			return "";
		}
	}

	/******************************************************** 华丽的分界线 ************************************************************/

	@Override
	public <T> List<T> getGroupbyList(int curPage, int pageSize,
			Set<Condition> conditions, LinkedHashSet<OrderBy> orderbys,
			LinkedHashMap<String, String> funs, String... groupby) {

		if (groupby != null && groupby.length > 0) {
			StringBuilder sb = new StringBuilder(" select  ");

			if (funs != null && funs.size() > 0) {
				Iterator<Entry<String, String>> entryset = funs.entrySet()
						.iterator();
				while (entryset.hasNext()) {
					Entry<String, String> en = entryset.next();
					sb.append(en.getKey()).append("(").append(alias)
							.append(".").append(en.getValue()).append(")");
					if (entryset.hasNext()
							|| (groupby != null && groupby.length > 0)) {
						sb.append(",");
					}
				}
			}

			if (groupby != null && groupby.length > 0) {

				for (int i = 0; i < groupby.length; i++) {

					sb.append(alias).append(".").append(groupby[i]);

					if (i < groupby.length - 1) {
						sb.append(",");
					}
				}

			}

			Query qr = groupbySub(conditions, orderbys, sb, groupby);
			qr.setFirstResult((curPage - 1) * pageSize);
			qr.setMaxResults(pageSize);
			return qr.getResultList();
		}
		return Collections.emptyList();
	}

	private Query groupbySub(Set<Condition> conditions,
			LinkedHashSet<OrderBy> orderbys, StringBuilder sb,
			String... groupby) {
		Set<Condition> fs = null;
		Set<Condition> ws = null;

		sb.append("  from  ").append(this.clazz.getSimpleName()).append("   ")
				.append(alias);

		if (MyUtils.isNotEmpty(conditions)) {
			ws = new HashSet<Condition>();
			fs = new HashSet<Condition>();
			for (Condition c : conditions) {
				if (c.getCdType().equals(CdType.FUN)) {
					fs.add(c);
				} else {
					ws.add(c);
				}
			}
			if (ws.size() > 0) {
				sb.append(getWhereSql(ws));
			}

		}

		groupbyappend(sb, groupby);

		havingappend(sb, fs);
		if (MyUtils.isNotEmpty(orderbys)) {
			Iterator<OrderBy> odite = orderbys.iterator();

			w: while (odite.hasNext()) {
				OrderBy ob = odite.next();
				for (String g : groupby) {
					if (MyUtils.isNotEmpty(ob.getFunName())
							|| g.equals(ob.getPropertyName())) {
						continue w;
					}
				}
				odite.remove();
			}

			sb.append(getOrderbyJpSql(orderbys));

		}

		Query qr = getEm().createQuery(sb.toString());
		addConditionValue(qr, ws);
		if (MyUtils.isNotEmpty(fs)) {
			Iterator<Condition> fsite = fs.iterator();
			while (fsite.hasNext()) {
				Condition cd = fsite.next();
				qr.setParameter(cd.getFunName() + cd.getProperty(),
						cd.getValue());
			}
		}
		return qr;
	}

	private void groupbyappend(StringBuilder sb, String... groupby) {
		sb.append("  group by  ");
		for (int j = 0; j < groupby.length; j++) {

			sb.append(alias).append(".").append(groupby[j]);
			if (j < groupby.length - 1) {
				sb.append(",");
			}
		}
	}

	private void havingappend(StringBuilder sb, Set<Condition> fs) {
		if (MyUtils.isNotEmpty(fs)) {
			sb.append(" having ");
			Iterator<Condition> fsite = fs.iterator();
			while (fsite.hasNext()) {
				Condition cd = fsite.next();
				sb.append(cd.getFunName()).append("(").append(alias)
						.append(".").append(cd.getProperty()).append(")");
				sb.append(cd.getOper().getValue()).append(":")
						.append(cd.getFunName()).append(cd.getProperty());

				if (fsite.hasNext()) {
					sb.append(" and  ");
				}
			}

		}
	}

	@Override
	public Long getTotalCountByGroupBy(Set<Condition> conditions,
			String... groupby) {
		StringBuilder sb = new StringBuilder("select count(1) from (select ");
		String tableName = this.clazz.getSimpleName();
		if (this.clazz.isAnnotationPresent(Table.class)) {
			Table tbl = this.clazz.getAnnotation(Table.class);
			if (tbl.name().length() > 0) {
				tableName = tbl.name();
			}
		}

		sb.append(alias).append(".").append(groupby[0]);

		Set<Condition> fs = null;
		Set<Condition> ws = null;

		sb.append("  from  ").append(tableName).append("   ").append(alias);

		if (MyUtils.isNotEmpty(conditions)) {
			ws = new HashSet<Condition>();
			fs = new HashSet<Condition>();
			for (Condition c : conditions) {
				if (c.getCdType().equals(CdType.FUN)) {
					fs.add(c);
				} else {
					ws.add(c);
				}
			}
			if (ws.size() > 0) {
				sb.append(getWhereSql(ws));
			}

		}

		groupbyappend(sb, groupby);

		havingappend(sb, fs);

		sb.append(")  ").append(tableName);

		Query qr = getEm().createNativeQuery(sb.toString());
		addConditionValue(qr, ws);
		if (MyUtils.isNotEmpty(fs)) {
			Iterator<Condition> fsite = fs.iterator();
			while (fsite.hasNext()) {
				Condition cd = fsite.next();
				qr.setParameter(cd.getFunName() + cd.getProperty(),
						cd.getValue());
			}
		}

		Number nb = (Number) qr.getSingleResult();
		return nb.longValue();
	}

	@Override
	public void save(POJO pojo) {
		getEm().persist(pojo);
	}

	@Override
	public POJO persist(POJO pojo) {
		getEm().persist(pojo);
		return pojo;
	}

	@Override
	public List<POJO> getListOrderby(LinkedHashMap<String, String> orderby,
			Set<Condition> conditions) {
		return getList(1, Integer.MAX_VALUE, conditions, orderby);
	}

	@Override
	public List<POJO> getListBySgCd(Set<Condition> conditions,
			String... propertys) {
		return this.getList(null, conditions, propertys);
	}

	@Override
	public Long getCount(Set<Condition> conditions) {
		StringBuilder sqlbuf = getselectCountSqlPre();
		sqlbuf.append(this.getWhereSql(conditions));
		Query qr = getEm().createQuery(sqlbuf.toString());
		addConditionValue(qr, conditions);
		return (Long) qr.getSingleResult();
	}

	/**
	 * 拼接count统计sql查询
	 * 
	 * @return
	 */
	protected StringBuilder getselectCountSqlPre() {
		StringBuilder sqlbuf = new StringBuilder("select count(");
		sqlbuf.append(alias).append(")").append("  from ")
				.append(this.clazz.getSimpleName()).append("   ").append(alias);
		return sqlbuf;
	}

	@Override
	public Long getCount() {
		return getCount(null);
	}

	@Override
	public void update(POJO pojo) {
		getEm().merge(pojo);
	}

	@Override
	public POJO updateAndRetrun(POJO pojo) {
		return getEm().merge(pojo);
	}

	@Override
	public List<POJO> getList(LinkedHashMap<String, String> orderby,
			Set<Condition> conditions, String... propertys) {
		return this.getList(1, Integer.MAX_VALUE, conditions, orderby,
				propertys);
	}

	@Override
	public POJO getReference(Serializable sid) {
		return getEm().getReference(this.clazz, sid);
	}

	@Override
	public PageInfo<POJO> getPageInfo(List<POJO> dataList, Long datacount,
			int curPage, int pageSize) {
		return new PageInfo<POJO>(curPage, pageSize, datacount, dataList);
	}

	@Override
	public <T> PageInfo<T> getOptionPageInfo(List<T> dataList, Long datacount,
			int curPage, int pageSize) {
		return new PageInfo<T>(curPage, pageSize, datacount, dataList);
	}

	@Override
	public POJO get(Set<Condition> conditions) {
		return get(conditions, null);
	}

	@Override
	public void deleteByCondition(Set<Condition> conditions) {
		StringBuffer buf = new StringBuffer("delete from  ");
		buf.append(this.clazz.getSimpleName()).append("  ").append(alias);
		buf.append(getWhereSql(conditions));
		Query qr = getEm().createQuery(buf.toString());
		addConditionValue(qr, conditions);
		qr.executeUpdate();
	}

	@Override
	public void updateByCondition(Set<Condition> conditions,
			Map<String, Object> newValues) {
		if (newValues != null && newValues.size() > 0) {
			StringBuilder buf = new StringBuilder("update ");
			buf.append(this.clazz.getSimpleName()).append("   ").append(alias)
					.append("  ").append("set  ");
			Iterator<Entry<String, Object>> ite = newValues.entrySet()
					.iterator();
			while (ite.hasNext()) {
				Entry<String, Object> en = ite.next();
				buf.append(alias).append(".").append(en.getKey()).append("=")
						.append(":").append(alias).append(en.getKey());
				if (ite.hasNext()) {
					buf.append(",");
				}
			}

			buf.append(getWhereSql(conditions));

			Query qr = getEm().createQuery(buf.toString());

			if (newValues != null && newValues.size() > 0) {
				Set<Entry<String, Object>> entrySet = newValues.entrySet();
				for (Entry<String, Object> en : entrySet) {
					qr.setParameter(alias + en.getKey(), en.getValue());
				}

			}

			addConditionValue(qr, conditions);
			qr.executeUpdate();
		}

	}

	@Override
	public int update(Set<Condition> conditions, Map<String, Object> newValues) {
		if (newValues != null && newValues.size() > 0) {
			StringBuilder buf = new StringBuilder("update ");
			buf.append(this.clazz.getSimpleName()).append("   ").append(alias)
					.append("  ").append("set  ");
			Iterator<Entry<String, Object>> ite = newValues.entrySet()
					.iterator();
			while (ite.hasNext()) {
				Entry<String, Object> en = ite.next();
				buf.append(alias).append(".").append(en.getKey()).append("=")
						.append(":").append(alias).append(en.getKey());
				if (ite.hasNext()) {
					buf.append(",");
				}
			}

			buf.append(getWhereSql(conditions));

			Query qr = getEm().createQuery(buf.toString());

			if (newValues != null && newValues.size() > 0) {
				Set<Entry<String, Object>> entrySet = newValues.entrySet();
				for (Entry<String, Object> en : entrySet) {
					qr.setParameter(alias + en.getKey(), en.getValue());
				}

			}

			addConditionValue(qr, conditions);
			return qr.executeUpdate();
		}
		return 0;
	}

	@Override
	public double getAvgPropertyValue(Set<Condition> condition, String property) {
		if (property != null && !property.trim().equals("")) {
			StringBuilder buf = new StringBuilder("select  avg(");
			buf.append(alias).append(".").append(property).append(")")
					.append("  from  ").append(this.clazz.getSimpleName())
					.append("  ").append(alias);
			buf.append(getWhereSql(condition));
			Query qr = getEm().createQuery(buf.toString());
			addConditionValue(qr, condition);
			Object o = qr.getSingleResult();
			if (o != null) {
				return Double.valueOf(o.toString());
			}
		}

		return 0;
	}

	@Override
	public double getSumPropertyValue(Set<Condition> conditions, String property) {
		if (property != null && !property.trim().equals("")) {
			StringBuilder buf = new StringBuilder("select sum(");
			buf.append(alias).append(".").append(property).append(")")
					.append(" from ").append(this.clazz.getSimpleName())
					.append("  ").append(alias);
			buf.append(getWhereSql(conditions));
			Query qr = getEm().createQuery(buf.toString());
			addConditionValue(qr, conditions);
			Object o = qr.getSingleResult();
			if (o != null) {
				return Double.valueOf(o.toString());
			}
		}

		return 0;
	}

	@Override
	public double getFunctionPropertyValue(Set<Condition> conditions,
			String property, String functionName) {
		if (property != null && !property.trim().equals("")
				&& MyUtils.isNotEmpty(functionName)) {
			StringBuilder buf = new StringBuilder("select  " + functionName
					+ "(");
			buf.append(alias).append(".").append(property).append(")")
					.append(" from ").append(this.clazz.getSimpleName())
					.append("  ").append(alias);
			buf.append(getWhereSql(conditions));
			Query qr = getEm().createQuery(buf.toString());
			addConditionValue(qr, conditions);
			Object o = qr.getSingleResult();
			if (o != null) {
				return Double.valueOf(o.toString());
			}
		}

		return 0;
	}

	@Override
	public POJO get(String propertyName, Serializable value, String... c) {
		if (MyUtils.isNotEmpty(propertyName)) {
			StringBuilder bd = new StringBuilder(getSelectJPQL(c));
			bd.append(" where ").append(alias).append(".").append(propertyName);
			if (value != null && !value.toString().trim().equals("")) {
				bd.append("=:").append(propertyName);
			} else {
				bd.append(" is null ");
			}
			Query qr = getEm().createQuery(bd.toString());
			if (value != null && !value.toString().trim().equals("")) {
				qr.setParameter(propertyName, value);
			}

			if (c != null && c.length > 0 && !c[0].trim().equals("")) {
				List<POJO> ps = packageObject(qr, c);
				if (ps.size() == 1) {
					return ps.get(0);
				}
			} else {
				List<POJO> ps = qr.getResultList();
				if (ps.size() == 1) {
					return ps.get(0);
				}
			}
		}
		return null;
	}

	@Override
	public POJO get(Set<Condition> conditions, String... propertys) {
		String jpql = getSelectJPQL(propertys) + getWhereSql(conditions);
		Query qr = getEm().createQuery(jpql);
		addConditionValue(qr, conditions);
		if (propertys != null && propertys.length > 0
				&& !propertys[0].trim().equals("")) {
			List<POJO> list = packageObject(qr, propertys);
			if (list.size() == 1) {
				return list.get(0);
			}
		} else {
			List<POJO> list = qr.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
		}
		return null;
	}

	@Override
	public void delete(POJO pojo) {
		getEm().remove(pojo);
	}

	@Override
	public void deleteById(Serializable... sid) {
		if (sid != null) {
			for (Serializable s : sid) {
				getEm().remove(getEm().getReference(this.clazz, s));
			}
		}
	}

	@Override
	public POJO getById(Serializable sid) {
		return getEm().find(this.clazz, sid);
	}

	@Override
	public List<POJO> getList(int curPage, int pageSize) {
		return getList(curPage, pageSize, null, null, null);
	}

	@Override
	public List<POJO> getList() {
		return getList(1, Integer.MAX_VALUE, null, null, null);
	}

	@Override
	public List<POJO> getList(int pageSize) {
		return getList(1, pageSize, null, null, null);
	}

	@Override
	public List<POJO> getList(int curPage, int pageSize,
			LinkedHashMap<String, String> orderby) {
		return getList(curPage, pageSize, null, orderby, null);
	}

	@Override
	public List<POJO> getListOrderBy(LinkedHashMap<String, String> orderby) {
		return getList(1, Integer.MAX_VALUE, orderby);
	}

	@Override
	public List<POJO> getListByCondition(Set<Condition> conditions) {
		return getList(1, Integer.MAX_VALUE, conditions, null, null);
	}

	@Override
	public List<POJO> getList(int curPage, int pageSize,
			Set<Condition> conditions, LinkedHashMap<String, String> orderby) {
		return getList(curPage, pageSize, conditions, orderby, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<POJO> getList(int curPage, int pageSize,
			Set<Condition> conditions, LinkedHashMap<String, String> orderby,
			String... propertys) {
		String jpql = getSelectJPQL(propertys) + getWhereSql(conditions)
				+ getOrderbyJpSql(orderby);
		Query qr = getEm().createQuery(jpql);
		addConditionValue(qr, conditions);
		qr.setFirstResult((curPage - 1) * pageSize);
		qr.setMaxResults(pageSize);
		if (propertys != null && propertys.length > 0
				&& !propertys[0].trim().equals("")) {
			return packageObject(qr, propertys);
		} else {
			return qr.getResultList();
		}
	}

	@Override
	public List<POJO> getList(Set<Condition> conditions, int curPage,
			int pageSize) {
		return getList(curPage, pageSize, conditions, null, null);
	}

	@Override
	public List<POJO> getList(LinkedHashMap<String, String> orderby,
			String... propertys) {
		return getList(1, Integer.MAX_VALUE, null, orderby);
	}

	@Override
	public PageInfo<POJO> getPageInfo(int curPage, int pageSize,
			Set<Condition> conditions, LinkedHashMap<String, String> orderby,
			String... propertys) {
		return new PageInfo<POJO>(curPage, pageSize, getCount(conditions),
				getList(curPage, pageSize, conditions, orderby, propertys));

	}

	@Override
	public PageInfo<POJO> getPageInfo(int curPage, int pageSize,
			Set<Condition> conditions, LinkedHashMap<String, String> orderby) {
		return new PageInfo<POJO>(curPage, pageSize, getCount(conditions),
				getList(curPage, pageSize, conditions, orderby, null));

	}

	@Override
	public PageInfo<POJO> getPageInfo(Set<Condition> conditions, int curPage,
			int pageSize) {
		return new PageInfo<POJO>(curPage, pageSize, getCount(conditions),
				getList(curPage, pageSize, conditions, null, null));

	}

	@Override
	public PageInfo<POJO> getPageInfo(int curPage, int pageSize) {
		return new PageInfo<POJO>(curPage, pageSize, getCount(null), getList(
				curPage, pageSize, null, null, null));

	}

	@Override
	public PageInfo<POJO> getPageInfo(int curPage, int pageSize,
			LinkedHashMap<String, String> orderby, String... propertys) {
		return new PageInfo<POJO>(curPage, pageSize, getCount(null), getList(
				curPage, pageSize, null, orderby, propertys));

	}

	@Override
	public void updateByList(List<POJO> poList) {
		if (MyUtils.isNotEmpty(poList)) {
			for (POJO p : poList) {
				update(p);
			}
		}
	}

	@Override
	public void saveBatch(List<POJO> pojos) {
		if (MyUtils.isNotEmpty(pojos)) {
			for (POJO p : pojos) {
				getEm().persist(p);
			}
		}
	}

	@Override
	public PageInfo<POJO> getPageInfo(int curPage, int pageSize,
			LinkedHashMap<String, String> orderby) {
		return new PageInfo<POJO>(curPage, pageSize, getCount(null), getList(
				curPage, pageSize, null, orderby, null));

	}

	@Override
	public List<POJO> getListBySql(int curPage, int pageSize, SqlWhere[] sws,
			LinkedHashMap<String, String> orderby, String... props) {
		StringBuilder bd = new StringBuilder(getSelectJPQL(props));
		bd.append(getWhereSql(sws));
		bd.append(getOrderbyJpSql(orderby));
		Query qr = sqlAddcondtion(sws, bd);

		qr.setFirstResult((curPage - 1) * pageSize);
		qr.setMaxResults(pageSize);
		if (MyUtils.isNotEmpty(props) && !props[0].trim().equals("")) {
			return packageObject(qr, props);
		}
		return qr.getResultList();
	}

	@Override
	public Long getCountBySql(SqlWhere[] sws) {
		StringBuilder sqlbuf = getselectCountSqlPre();
		sqlbuf.append(getWhereSql(sws));
		Query qr = sqlAddcondtion(sws, sqlbuf);
		Number o = (Number) qr.getSingleResult();
		return o.longValue();
	}

	@Override
	public PageInfo<POJO> getPageinfoBySql(int curPage, int pageSize,
			SqlWhere[] sws, LinkedHashMap<String, String> orderby,
			String... props) {
		return new PageInfo<POJO>(curPage, pageSize, getCountBySql(sws),
				getListBySql(curPage, pageSize, sws, orderby, props));
	}

	/***********
	 * -------------------------------华丽分割线------------------------------------
	 * ------------
	 *******/
	/***
	 * 查询指定属性的值返回的是object数组需要调用此方法把数据转换成POJO对象返回，此方法封装实现细节
	 */
	@SuppressWarnings("unchecked")
	protected List<POJO> packageObject(Query qr, String... propertys) {
		try {
			List<POJO> list = new ArrayList<POJO>();
			if (propertys.length == 1) {
				List<Object> vlist = qr.getResultList();
				for (Object ov : vlist) {
					Object obj = this.clazz.newInstance();
					Field fd = this.clazz.getDeclaredField(propertys[0]);
					fd.setAccessible(true);
					if (ov != null) {

						if (ov instanceof BigDecimal) {
							ov = ((BigDecimal) ov).doubleValue();
						} else if (ov instanceof BigInteger) {
							ov = ((BigInteger) ov).longValue();
						}

						if (fd.getType().isEnum()) {
							Class<Enum> cls = (Class<Enum>) fd.getType();
							if (ov instanceof Number) {
								Enum[] ccs = (Enum[]) fd.getType()
										.getEnumConstants();
								fd.set(obj, Enum.valueOf(cls, ccs[Number.class
										.cast(ov).intValue()].name()));
							} else {

								fd.set(obj, Enum.valueOf(cls, ov.toString()));
							}

						} else {
							fd.set(obj, ov);
						}
					}

					list.add((POJO) obj);

				}

			} else {
				List<Object[]> vlist = qr.getResultList();
				for (Object[] ov : vlist) {
					Object obj = this.clazz.newInstance();
					for (int i = 0; i < propertys.length; i++) {
						Field fd = this.clazz.getDeclaredField(propertys[i]);
						fd.setAccessible(true);

						if (ov[i] != null) {
							if (ov[i] instanceof BigDecimal) {
								ov[i] = ((BigDecimal) ov[i]).doubleValue();
							} else if (ov[i] instanceof BigInteger) {
								ov[i] = ((BigInteger) ov[i]).longValue();
							}

							if (fd.getType().isEnum()) {
								Class<Enum> cls = (Class<Enum>) fd.getType();
								if (ov[i] instanceof Number) {
									Enum[] ccs = (Enum[]) fd.getType()
											.getEnumConstants();
									fd.set(obj, Enum.valueOf(cls,
											ccs[Number.class.cast(ov[i])
													.intValue()].name()));
								} else {
									fd.set(obj,
											Enum.valueOf(cls, ov[i].toString()));
								}

							} else {
								fd.set(obj, ov[i]);
							}
						}

					}
					list.add((POJO) obj);
				}
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Query sqlAddcondtion(SqlWhere[] sws, StringBuilder bd) {
		Query qr = getEm().createQuery(bd.toString());
		int ct = 0;
		if (MyUtils.isNotEmpty(sws)) {
			for (int i = 0; i < sws.length; i++) {
				if (MyUtils.isNotEmpty(sws[i].getConditions())) {
					for (int j = 0; j < sws[i].getConditions().length; j++) {
						Condition c = sws[i].getConditions()[j];
						if (!((c.getOper().equals(Operators.EQ) || c.getOper()
								.equals(Operators.NOT_EQ)) && !MyUtils
								.isNotEmpty(c.getValue()))) {

							if (c.getOper().equals(Operators.BETWEEN)) {
								qr.setParameter(ct + 1, c.getFirstValue());
								qr.setParameter(++ct + 1, c.getValue());
							} else {
								if (c.getOper().equals(Operators.LIKE)) {
									qr.setParameter(ct + 1, c.getValue());
								} else {
									qr.setParameter(ct + 1, c.getValue());
								}
							}
						}
						ct++;
					}
				}

			}

		}
		return qr;
	}

	/**
	 * 得到JPQL语句的select部分语句不包括WHERE部分
	 */
	protected String getSelectJPQL(String... propertys) {
		return appendselectsql(alias, propertys);
	}

	/**
	 * 得到JPQL语句的select部分语句不包括WHERE部分
	 */
	protected String getSelectJPQL(String alias, String... propertys) {
		return appendselectsql(alias, propertys);
	}

	private String appendselectsql(String alias, String... propertys) {
		StringBuilder buf = new StringBuilder(" select  ");
		if (propertys != null && propertys.length > 0
				&& !propertys[0].trim().equals("")) {
			for (int i = 0; i < propertys.length; i++) {
				buf.append(alias).append(".").append(propertys[i]);
				if (i < propertys.length - 1) {
					buf.append(",");
				}
			}
		} else {
			buf.append(alias);
		}
		buf.append("  from  ").append(this.clazz.getSimpleName()).append("   ")
				.append(alias);
		return buf.append("  ").toString();
	}

	/**
	 * 得到排序JPQL语句
	 * 
	 * @param orderbys
	 * @return
	 */
	protected static String getOrderbyJpSql(
			LinkedHashMap<String, String> orderbys) {
		if (orderbys == null || orderbys.size() < 1) {
			return "";
		}
		StringBuilder buf = new StringBuilder(" order by  ");

		Set<Entry<String, String>> entrySet = orderbys.entrySet();
		Iterator<Entry<String, String>> ite = entrySet.iterator();

		while (ite.hasNext()) {
			Entry<String, String> en = ite.next();
			buf.append(alias).append(".").append(en.getKey()).append("  ")
					.append(en.getValue());
			if (ite.hasNext()) {
				buf.append(",");
			}
		}
		return buf.toString();
	}

	/**
	 * 得到排序JPQL语句
	 * 
	 * @param orderbys
	 * @return
	 */
	protected static String getOrderbyJpSql(
			LinkedHashMap<String, String> orderbys, String alias) {
		if (MyUtils.isNotEmpty(orderbys) || !MyUtils.isNotEmpty(alias)) {
			return "";
		}
		StringBuilder buf = new StringBuilder(" order by  ");

		Set<Entry<String, String>> entrySet = orderbys.entrySet();
		Iterator<Entry<String, String>> ite = entrySet.iterator();

		while (ite.hasNext()) {
			Entry<String, String> en = ite.next();
			buf.append(alias).append(".").append(en.getKey()).append("  ")
					.append(en.getValue());
			if (ite.hasNext()) {
				buf.append(",");
			}
		}
		return buf.toString();
	}

	/**
	 * 得到排序JPQL语句
	 * 
	 * @param orderbys
	 * @return
	 */
	protected static String getOrderbyJpSql(LinkedHashSet<OrderBy> orderbys) {
		if (orderbys == null || orderbys.size() < 1) {
			return "";
		}
		StringBuilder buf = new StringBuilder(" order by  ");

		Iterator<OrderBy> ite = orderbys.iterator();

		while (ite.hasNext()) {
			OrderBy od = ite.next();

			if (MyUtils.isNotEmpty(od.getFunName())) {
				buf.append(od.getFunName()).append("(");
			}
			buf.append(alias).append(".").append(od.getPropertyName());

			if (MyUtils.isNotEmpty(od.getFunName())) {

				buf.append(")");
			}

			if (od.getIsDesc()) {
				buf.append("   desc ");
			}

			if (ite.hasNext()) {
				buf.append(",");
			}
		}
		return buf.toString();
	}

	/**
	 * 动态获取此DAO操作的实体类型
	 */
	@SuppressWarnings("unchecked")
	private Class<POJO> getThisClass(Class clazz) {
		Type type = clazz.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			Type[] ts = ((ParameterizedType) type).getActualTypeArguments();
			return (Class<POJO>) ts[0];
		}
		throw new RuntimeException("DAO 继承出现错误！DAO的父类需要使用泛型却没有使用泛型。。");
	}

	/**
	 * 拼接 JPQL Where 语句
	 * 
	 * @param conditions
	 * @return
	 */
	protected String getWhereSql(String alias, Set<Condition> conditions) {

		return setconditions(alias, conditions);
	}

	/**
	 * 拼接 JPQL Where 语句
	 * 
	 * @param conditions
	 * @return
	 */
	protected String getWhereSql(Set<Condition> conditions) {

		return setconditions(alias, conditions);
	}

	private String setconditions(String alias, Set<Condition> conditions) {
		if (conditions != null && conditions.size() > 0) {
			StringBuilder bufWhere = new StringBuilder(" where  ");
			Iterator<Condition> ite = conditions.iterator();

			while (ite.hasNext()) {
				// 得到条件数据对象
				Condition cd = ite.next();
				// 拼接属性
				if (MyUtils.isNotEmpty(cd.getValue())) {
					bufWhere.append(alias).append(".").append(cd.getProperty());
					bufWhere.append(cd.getOper().getValue());
					if (cd.getCdType().equals(CdType.OG)) {
						if (cd.getOper().equals(Operators.BETWEEN)) {
							bufWhere.append(alias).append(".")
									.append(cd.getFirstValue());
							bufWhere.append(Sentences.AND.getValue());
						}
						if (cd.getOper().equals(Operators.IN)
								|| cd.getOper().equals(Operators.NOT_IN)) {
							bufWhere.append("(");
						}
						bufWhere.append(alias).append(".")
								.append(cd.getValue());
						if (cd.getOper().equals(Operators.IN)
								|| cd.getOper().equals(Operators.NOT_IN)) {
							bufWhere.append(")");
						}
					} else {
						if (cd.getOper().equals(Operators.BETWEEN)) {
							bufWhere.append(":").append(alias)
									.append(cd.getProperty())
									.append(Sentences.AND.getValue());
						}
						if (cd.getOper().equals(Operators.IN)
								|| cd.getOper().equals(Operators.NOT_IN)) {
							bufWhere.append("(");
						}
						bufWhere.append(":").append(cd.getProperty());
						if (cd.getOper().equals(Operators.IN)
								|| cd.getOper().equals(Operators.NOT_IN)) {
							bufWhere.append(")");
						}
					}
				} else {
					if (cd.getCdType().equals(CdType.VL)) {
						if (cd.getOper().equals(Operators.EQ)) {
							if (isStr(cd.getProperty())) {
								bufWhere.append("(");
								bufWhere.append(alias).append(".")
										.append(cd.getProperty());
								bufWhere.append(cd.getOper().getValue());
								bufWhere.append("''").append(
										Sentences.OR.getValue());
							}
							bufWhere.append(alias).append(".")
									.append(cd.getProperty());
							bufWhere.append(Sentences.IS_NULL.getValue());
							if (isStr(cd.getProperty())) {
								bufWhere.append(")");
							}
						} else if (cd.getOper().equals(Operators.NOT_EQ)) {
							if (isStr(cd.getProperty())) {
								bufWhere.append(alias).append(".")
										.append(cd.getProperty());
								bufWhere.append(cd.getOper().getValue());
								bufWhere.append("''").append(
										Sentences.AND.getValue());
							}
							bufWhere.append(alias).append(".")
									.append(cd.getProperty());
							bufWhere.append(Sentences.IS_NOT_NULL.getValue());

						} else {
							bufWhere.append(alias).append(".")
									.append(cd.getProperty());
							bufWhere.append(cd.getOper().getValue());
							if (cd.getOper().equals(Operators.BETWEEN)) {
								bufWhere.append(":").append(alias)
										.append(cd.getProperty())
										.append(Sentences.AND.getValue());
							}
							if (cd.getOper().equals(Operators.IN)
									|| cd.getOper().equals(Operators.NOT_IN)) {
								bufWhere.append("(");
							}
							bufWhere.append(":").append(cd.getProperty());
							if (cd.getOper().equals(Operators.IN)
									|| cd.getOper().equals(Operators.NOT_IN)) {
								bufWhere.append(")");
							}

						}
					}
				}

				if (ite.hasNext()) {
					bufWhere.append(Sentences.AND.getValue());
				}

			}
			return bufWhere.toString();
		} else {
			return "";
		}
	}

	/**
	 * 给查询条件赋值
	 * 
	 * @param qr
	 * @param conditions
	 */
	protected static void addConditionValue(Query qr, Set<Condition> conditions) {
		setConditionvalue(alias, qr, conditions);
	}

	/**
	 * 给查询条件赋值
	 * 
	 * @param qr
	 * @param conditions
	 */
	protected static void addConditionValue(String alias, Query qr,
			Set<Condition> conditions) {
		setConditionvalue(alias, qr, conditions);
	}

	private static void setConditionvalue(String alias, Query qr,
			Set<Condition> conditions) {
		if (conditions != null && conditions.size() > 0) {
			for (Condition cd : conditions) {
				if (cd.getCdType().equals(CdType.VL)) {
					if (cd.getOper().equals(Operators.LIKE)) {
						qr.setParameter(cd.getProperty(), cd.getValue());
					} else if (cd.getOper().equals(Operators.BETWEEN)) {
						qr.setParameter(alias + cd.getProperty(),
								cd.getFirstValue());
						qr.setParameter(cd.getProperty(), cd.getValue());
					} else {
						if (cd.getValue() != null && !cd.getValue().equals("")) {
							qr.setParameter(cd.getProperty(), cd.getValue());
						}
					}
				}
			}
		}
	}

	/**
	 * 属性是否String 类型
	 * 
	 * @param property
	 * @return
	 */
	private boolean isStr(String property) {
		try {
			Field fd = clazz.getDeclaredField(property.trim());
			return fd.getType() == String.class;
		} catch (Exception e) {
			throw new IllegalArgumentException(property + "属性不合法！！！");
		}
	}

	/**
	 * 当前操作的实体对象类型
	 */
	private Class<POJO> clazz = getThisClass(getClass());
}
