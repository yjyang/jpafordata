package com.fd.dao.base.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Query;

import com.fd.dao.base.IMQuery;
import com.fd.dao.base.bean.Cuery;
import com.fd.dao.base.bean.GroupBy;
import com.fd.dao.base.bean.JoinQuery;
import com.fd.dao.base.bean.Midcm;
import com.fd.dao.base.common.Condition;
import com.fd.dao.base.common.OrderBy;
import com.fd.dao.base.common.PageInfo;
import com.fd.dao.base.em.CdType;
import com.fd.dao.base.em.Operators;
import com.fd.dao.base.em.SType;
import com.fd.dao.base.em.Sentences;
import com.fd.dao.base.em.SqlOp;
import com.fd.util.MyUtils;

public abstract class MQuery extends CommonDao implements IMQuery {
	private static final long serialVersionUID = 6286710605364501516L;

	// 赋值
	private static void setQrCondition(Query qr, Condition cd) {
		addOrCondition(qr, cd);
		if (cd.getSw() != null
				&& MyUtils.isNotEmpty(cd.getSw().getConditions())) {
			for (Condition cdd : cd.getSw().getConditions()) {
				addOrCondition(qr, cdd);
			}
		}

	}

	private static void addOrCondition(Query qr, Condition cd) {
		if (cd.getValue() != null) {
			if (cd.getOper().equals(Operators.BETWEEN)) {
				String sk = cd.getTable() + cd.getTable();
				if (cd.getCdType().equals(CdType.FUN)) {
					sk += cd.getFunName();
				}
				sk += cd.getCplx();
				sk += cd.getProperty();
				qr.setParameter(sk, cd.getFirstValue());
			}
			String prm = cd.getTable();

			if (cd.getCdType().equals(CdType.FUN)) {
				prm += cd.getFunName();
			}
			prm += cd.getCplx();
			prm += cd.getProperty();
			qr.setParameter(prm, cd.getValue());
		}
	}

	private static void cdsub(Condition cd, StringBuilder sb) {
		if (cd.getValue() == null) {
			if (cd.getOper().equals(Operators.EQ)
					|| cd.getOper().equals(Operators.NOT_EQ)) {
				sb.append("(");
				if (cd.getIsPre()) {
					sb.append(cd.getTable());
					sb.append(".");
				}
				sb.append(cd.getProperty());
				if (cd.getOper().equals(Operators.EQ)) {
					sb.append(Sentences.IS_NULL);
					sb.append(Sentences.OR);
				} else {
					sb.append(Sentences.IS_NOT_NULL);
					sb.append(Sentences.AND);

				}
				if (cd.getIsPre()) {
					sb.append(cd.getTable());
					sb.append(".");
				}
				sb.append(cd.getProperty());
				sb.append(cd.getOper().getValue());
				sb.append("''");
				sb.append(")");
			}
		} else {
			if (cd.getCdType().equals(CdType.FUN)) {
				sb.append(cd.getFunName()).append("(");
			}
			if (cd.getIsPre()) {
				sb.append(cd.getTable());
				sb.append(".");
			}

			sb.append(cd.getProperty());
			if (cd.getCdType().equals(CdType.FUN)) {
				sb.append(")");
			}
			sb.append(cd.getOper().getValue());

			if (cd.getOper().equals(Operators.BETWEEN)) {
				sb.append(":").append(cd.getTable()).append(cd.getTable());
				if (cd.getCdType().equals(CdType.FUN)) {
					sb.append(cd.getFunName());
				}
				sb.append(cd.getCplx());
				sb.append(cd.getProperty()).append(Sentences.AND.getValue());

			}
			if (cd.getOper().equals(Operators.IN)
					|| cd.getOper().equals(Operators.NOT_IN)) {
				sb.append("(");
			}

			sb.append(":").append(cd.getTable());
			if (cd.getCdType().equals(CdType.FUN)) {
				sb.append(cd.getFunName());
			}
			sb.append(cd.getCplx());
			sb.append(cd.getProperty());

			if (cd.getOper().equals(Operators.IN)
					|| cd.getOper().equals(Operators.NOT_IN)) {
				sb.append(")");
			}
		}
	}

	// 得到所以普通字段查询条件
	private static Set<Condition> getConditions(LinkedHashSet<JoinQuery> jqs) {
		Set<Condition> cds = new LinkedHashSet<Condition>();
		for (JoinQuery jq : jqs) {
			if (MyUtils.isNotEmpty(jq.getConditions())) {
				for (Condition cd : jq.getConditions()) {
					if (!MyUtils.isNotEmpty(cd.getFunName())) {
						if (!MyUtils.isNotEmpty(cd.getTable())) {
							cd.setTable(jq.getTable());
						}
						cds.add(cd);
					}
				}
			}
		}
		return cds;
	}

	// 得到聚集查询条件
	private static Set<Condition> getClusterConditions(
			LinkedHashSet<JoinQuery> jqs) {
		Set<Condition> cds = new LinkedHashSet<Condition>();
		for (JoinQuery jq : jqs) {
			if (MyUtils.isNotEmpty(jq.getConditions())) {
				for (Condition cd : jq.getConditions()) {
					if (MyUtils.isNotEmpty(cd.getFunName())
							&& cd.getCdType().equals(CdType.FUN)) {
						if (!MyUtils.isNotEmpty(cd.getTable())) {
							cd.setTable(jq.getTable());
						}
						cds.add(cd);
					}
				}
			}
		}
		return cds;

	}

	// 得到所以条件
	private static Set<Condition> getAllConditions(LinkedHashSet<JoinQuery> jqs) {
		Set<Condition> cds = new LinkedHashSet<Condition>();
		for (JoinQuery jq : jqs) {
			if (MyUtils.isNotEmpty(jq.getConditions())) {
				for (Condition cd : jq.getConditions()) {
					if (!MyUtils.isNotEmpty(cd.getTable())) {
						cd.setTable(jq.getTable());
					}
					cds.add(cd);
				}
			}
		}
		return cds;
	}

	// /多表条件分解where ,having 适用
	private static String getSqlStr(Condition cd) {
		StringBuilder sb = new StringBuilder();
		if (cd.getSw() != null
				&& MyUtils.isNotEmpty(cd.getSw().getConditions())) {
			sb.append("(");
		}
		cdsub(cd, sb);
		if (cd.getSw() != null
				&& MyUtils.isNotEmpty(cd.getSw().getConditions())) {
			sb.append(Sentences.OR);
			sb.append("(");
			Iterator<Condition> subswitem = cd.getSw().getConditionList()
					.iterator();
			while (subswitem.hasNext()) {
				Condition cds = subswitem.next();
				cdsub(cds, sb);
				if (subswitem.hasNext()) {
					if (cd.getSw().getSqlOp().equals(SqlOp.AND)) {
						sb.append(Sentences.AND);
					} else {
						sb.append(Sentences.OR);
					}
				}
			}
			sb.append(")");
		}
		if (cd.getSw() != null
				&& MyUtils.isNotEmpty(cd.getSw().getConditions())) {
			sb.append(")");
		}
		return sb.toString();
	}

	// /是否分组
	private boolean isGroupBy(LinkedHashSet<JoinQuery> jqs) {
		for (JoinQuery jq : jqs) {
			if (MyUtils.isNotEmpty(jq.getGroupbys())) {
				return true;
			}
		}
		return false;
	}

	// 查询哪些数据信息
	private static LinkedHashSet<Cuery> getCuerySet(LinkedHashSet<JoinQuery> jqs) {
		LinkedHashSet<Cuery> crs = new LinkedHashSet<Cuery>();
		for (JoinQuery jq : jqs) {
			if (MyUtils.isNotEmpty(jq.getCuerys())) {
				for (Cuery c : jq.getCuerys()) {
					if (!MyUtils.isNotEmpty(c.getTable())) {
						c.setTable(jq.getTable());
					}
					crs.add(c);
				}
			}
		}
		return crs;
	}

	@Override
	public Long getJoinCount(JoinQuery jqpt) {
		/**
		 * 开始封装查询语句
		 */
		StringBuilder sb = new StringBuilder(Sentences.SELECT.getValue());
		/**
		 * 所有的表信息
		 */
		LinkedHashSet<JoinQuery> jqs = getAllJq(jqpt);
		sb.append("COUNT(1)");
		if (isGroupBy(jqs)) {
			sb.append("  FROM  (SELECT  1  ");
		}
		joinQuery(jqpt, sb, jqs);
		if (isGroupBy(jqs)) {
			sb.append(")  c");
		}
		Query qr = getEm().createNativeQuery(sb.toString());
		for (Condition cd : getAllConditions(jqs)) {
			setQrCondition(qr, cd);
		}
		Number nm = (Number) qr.getSingleResult();
		return nm.longValue();
	}

	@Override
	public PageInfo getJoinPageInfo(JoinQuery jqpt, int curPage, int pageSize) {
		return new PageInfo<>(curPage, pageSize, getJoinCount(jqpt),
				getJoinList(jqpt, curPage, pageSize));
	}

	@Override
	public List<?> getJoinList(JoinQuery jqpt, int curPage, int pageSize) {
		/**
		 * 开始封装查询语句
		 */
		StringBuilder sb = new StringBuilder(Sentences.SELECT.getValue());
		/**
		 * 所有的表信息
		 */
		LinkedHashSet<JoinQuery> jqs = getAllJq(jqpt);
		/**
		 * select
		 */
		LinkedHashSet<Cuery> crs = getCuerySet(jqs);
		if (MyUtils.isNotEmpty(crs)) {
			Iterator<Cuery> crite = crs.iterator();
			while (crite.hasNext()) {
				Cuery c = crite.next();
				if (c.getsType().equals(SType.FIELD)) {
					if (c.getIsPre()) {
						sb.append(c.getTable()).append(".");
					}
					sb.append(c.getCname());
				} else if (c.getsType().equals(SType.FUNTION)) {

					sb.append(c.getFname()).append("(");
					if (c.getIsPre()) {
						sb.append(c.getTable()).append(".");
					}
					sb.append(c.getCname()).append(")");
				}
				if (crite.hasNext()) {
					sb.append(",");
				}
			}
		} else {
			sb.append("*");
		}

		joinQuery(jqpt, sb, jqs);

		/**
		 * order by
		 */
		LinkedHashSet<OrderBy> obs = getOrderBy(jqs);
		Iterator<OrderBy> obitem = obs.iterator();
		if (obitem.hasNext()) {
			sb.append(Sentences.ORDERBY.getValue());
			while (obitem.hasNext()) {
				OrderBy ob = obitem.next();
				if (MyUtils.isNotEmpty(ob.getFunName())) {
					sb.append(ob.getFunName()).append("(");
				}
				sb.append(ob.getTable()).append(".")
						.append(ob.getPropertyName());

				if (MyUtils.isNotEmpty(ob.getFunName())) {
					sb.append(")");

				}
				if (ob.getIsDesc()) {
					sb.append(Sentences.DESC.getValue());
				}
				if (obitem.hasNext()) {
					sb.append(",");
				}

			}
		}

		Query qr = getEm().createNativeQuery(sb.toString());
		for (Condition cd : getAllConditions(jqs)) {
			setQrCondition(qr, cd);
		}

		qr.setFirstResult((curPage - 1) * pageSize);
		qr.setMaxResults(pageSize);
		return qr.getResultList();

	}

	// 得到所有排序
	private static LinkedHashSet<OrderBy> getOrderBy(
			LinkedHashSet<JoinQuery> jqs) {
		LinkedHashSet<OrderBy> obs = new LinkedHashSet<OrderBy>();
		for (JoinQuery jq : jqs) {
			if (MyUtils.isNotEmpty(jq.getOrderbys())) {
				for (OrderBy ob : jq.getOrderbys()) {
					if (!MyUtils.isNotEmpty(ob.getTable())) {
						ob.setTable(jq.getTable());
					}
					obs.add(ob);
				}
			}
		}
		return obs;
	}

	private void joinQuery(JoinQuery jqpt, StringBuilder sb,
			LinkedHashSet<JoinQuery> jqs) {
		/**
		 * from
		 */
		sb.append(Sentences.FROM.getValue());
		sb.append(jqpt.getTable());
		sb.append(jqpt.getMultiJoinTp().getValue());
		sb.append(jqpt.getJoinQuery().getTable());
		sb.append(Sentences.ON);
		sb.append(jqpt.getTable()).append(".")
				.append(getAdpValue(jqpt.getMidcm())).append("=");
		sb.append(jqpt.getJoinQuery().getTable()).append(".")
				.append(jqpt.getJoinQuery().getMidcm()[0]);
		JoinQuery jqeury = jqpt.getJoinQuery();
		while (jqeury.getJoinQuery() != null) {
			JoinQuery tqr = jqeury;
			jqeury = jqeury.getJoinQuery();
			sb.append(tqr.getMultiJoinTp().getValue());
			sb.append(jqeury.getTable());
			sb.append(Sentences.ON);

			sb.append(tqr.getTable()).append(".")
					.append(getAdpValue(tqr.getMidcm())).append("=");
			sb.append(jqeury.getTable()).append(".")
					.append(jqeury.getMidcm()[0]);
		}
		/**
		 * pre where
		 */
		if (isGroupBy(jqs)) {
			addCondition(sb, getConditions(jqs));
		} else {
			addCondition(sb, getAllConditions(jqs));
		}

		/**
		 * group by
		 * 
		 */
		LinkedHashSet<GroupBy> gbs = getGroupBys(jqs);
		if (MyUtils.isNotEmpty(gbs)) {
			sb.append(Sentences.GROUPBY);
			Iterator<GroupBy> gbitem = gbs.iterator();
			while (gbitem.hasNext()) {
				GroupBy gb = gbitem.next();
				sb.append(gb.getTable()).append(".").append(gb.getProp());
				if (gbitem.hasNext()) {
					sb.append(",");
				}
			}
		}
		/**
		 * having
		 */
		Set<Condition> hvcds = getClusterConditions(jqs);
		if (MyUtils.isNotEmpty(hvcds)) {
			sb.append(Sentences.HAVING.getValue());
			Iterator<Condition> hvcditem = hvcds.iterator();
			while (hvcditem.hasNext()) {
				sb.append(getSqlStr(hvcditem.next()));
				if (hvcditem.hasNext()) {
					sb.append(Sentences.AND);
				}
			}
		}
	}

	/**
	 * 分组
	 * 
	 * @param jqs
	 * @return
	 */
	private static LinkedHashSet<GroupBy> getGroupBys(
			LinkedHashSet<JoinQuery> jqs) {
		LinkedHashSet<GroupBy> gbs = new LinkedHashSet<GroupBy>();
		for (JoinQuery jq : jqs) {
			if (MyUtils.isNotEmpty(jq.getGroupbys())) {
				for (String prop : jq.getGroupbys()) {
					gbs.add(new GroupBy(jq.getTable(), prop));
				}
			}
		}
		return gbs;
	}

	private void addCondition(StringBuilder sb, Set<Condition> jqscds) {
		if (MyUtils.isNotEmpty(jqscds)) {
			sb.append(Sentences.WHERE);
			Iterator<Condition> cdite = jqscds.iterator();
			while (cdite.hasNext()) {
				Condition cd = cdite.next();
				sb.append(getSqlStr(cd));
				if (cdite.hasNext()) {
					sb.append(Sentences.AND);
				}
			}
		}
	}

	private static LinkedHashSet<JoinQuery> getAllJq(JoinQuery jq) {
		JoinQuery jqn = jq;
		LinkedHashSet<JoinQuery> jqs = new LinkedHashSet<JoinQuery>();
		if (jqn != null) {
			while (jqn != null) {
				jqs.add(jqn);
				jqn = jqn.getJoinQuery();
			}
		}
		return jqs;
	}

	private static String getAdpValue(String[] arr) {
		if (arr.length == 2) {
			return arr[1];
		}
		return arr[0];
	}

	@Override
	public List<?> getMlist(int curPage, int pageSize,
			LinkedHashMap<String, String[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys) {
		/**
		 * 开始封装查询语句
		 */
		StringBuilder sb = new StringBuilder(Sentences.SELECT.getValue());
		return getList(curPage, pageSize, qrcms, wrmidcms, mconditions,
				orderbys, sb);

	}

	@Override
	public Long getMGroupbyCount(LinkedHashMap<String, Cuery[]> qrcms,
			Midcm wrmidcms, LinkedHashMap<String, Set<Condition>> mconditions) {
		StringBuilder sb = new StringBuilder(Sentences.SELECT.getValue());
		sb.append("COUNT(1) FROM  (SELECT  1  ");

		/**
		 * 中间字段关联
		 */
		addMidColumn(wrmidcms, sb);
		/**
		 * 查询条件
		 */
		Set<Condition> condis = new LinkedHashSet<Condition>();
		Set<Entry<String, Set<Condition>>> es = mconditions.entrySet();
		for (Entry<String, Set<Condition>> ens : es) {
			for (Condition cd : ens.getValue()) {
				if (!cd.getCdType().equals(CdType.FUN)) {
					cd.setTable(ens.getKey());
					condis.add(cd);
				}
			}
		}
		addCondition(condis, sb);

		/**
		 * 分组
		 */
		List<Cuery> groupbys = new ArrayList<Cuery>();
		/**
		 * 封装查询哪些数据
		 */
		Iterator<Entry<String, Cuery[]>> encs = qrcms.entrySet().iterator();
		while (encs.hasNext()) {
			Entry<String, Cuery[]> en = encs.next();
			for (int i = 0; i < en.getValue().length; i++) {
				Cuery c = en.getValue()[i];
				if (c.getsType().equals(SType.FIELD)) {
					c.setTable(en.getKey());
					groupbys.add(c);
				}
			}

		}
		Iterator<Cuery> cite = groupbys.iterator();
		sb.append(Sentences.GROUPBY.getValue());
		while (cite.hasNext()) {
			Cuery c = cite.next();
			sb.append(c.getTable()).append(".").append(c.getCname());
			if (cite.hasNext()) {
				sb.append(",");
			}
		}
		/**
		 * having
		 */
		List<Condition> hvs = new ArrayList<Condition>();
		for (Entry<String, Set<Condition>> c : mconditions.entrySet()) {
			for (Condition cdt : c.getValue()) {
				if (cdt.getCdType().equals(CdType.FUN)
						&& cdt.getFunName() != null
						&& cdt.getFunName().length() != 0) {
					cdt.setTable(c.getKey());
					hvs.add(cdt);
				}
			}
		}
		if (hvs.size() > 0) {
			sb.append(Sentences.HAVING);
			Iterator<Condition> itec = hvs.iterator();
			while (itec.hasNext()) {
				Condition cd = itec.next();
				sb.append(cd.getFunName()).append("(").append(cd.getTable())
						.append(".").append(cd.getProperty()).append(")");

				sb.append(cd.getOper().getValue());
				if (cd.getOper().equals(Operators.BETWEEN)) {
					sb.append(" :").append(cd.getTable()).append(cd.getTable())
							.append(cd.getFunName()).append(cd.getCplx())
							.append(cd.getProperty())
							.append(Sentences.AND.getValue());
				} else if (cd.getOper().equals(Operators.IN)
						|| cd.getOper().equals(Operators.NOT_IN)) {
					sb.append("(");
				}
				sb.append(" :").append(cd.getTable()).append(cd.getFunName())
						.append(cd.getCplx()).append(cd.getProperty());
				if (cd.getOper().equals(Operators.IN)
						|| cd.getOper().equals(Operators.NOT_IN)) {
					sb.append(")");
				}

				if (itec.hasNext()) {
					sb.append(Sentences.AND);
				}
			}
		}
		sb.append(")  c");
		Query qr = getEm().createNativeQuery(sb.toString());
		/**
		 * 添加条件
		 */
		setCondition(mconditions, qr);
		Number nm = (Number) qr.getSingleResult();
		return nm.longValue();
	}

	@Override
	public List<?> getMGroupbylist(int curPage, int pageSize,
			LinkedHashMap<String, Cuery[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys) {
		if (qrcms != null && qrcms.size() > 0) {
			/**
			 * 开始封装查询语句
			 */
			StringBuilder sb = new StringBuilder(Sentences.SELECT.getValue());
			List<Condition> hvs = new ArrayList<Condition>();
			for (Entry<String, Set<Condition>> c : mconditions.entrySet()) {
				for (Condition cdt : c.getValue()) {
					if (cdt.getCdType().equals(CdType.FUN)
							&& cdt.getFunName() != null
							&& cdt.getFunName().length() != 0) {
						cdt.setTable(c.getKey());
						hvs.add(cdt);
					}
				}
			}
			List<Cuery> groupbys = new ArrayList<Cuery>();
			/**
			 * 封装查询哪些数据
			 */
			Iterator<Entry<String, Cuery[]>> encs = qrcms.entrySet().iterator();
			while (encs.hasNext()) {
				Entry<String, Cuery[]> en = encs.next();
				for (int i = 0; i < en.getValue().length; i++) {
					Cuery c = en.getValue()[i];
					if (c.getsType().equals(SType.FIELD)) {
						sb.append(en.getKey()).append(".").append(c.getCname());
						c.setTable(en.getKey());
						groupbys.add(c);
					} else if (c.getsType().equals(SType.FUNTION)) {
						sb.append(c.getFname()).append("(");
						if (c.getIsPre()) {
							sb.append(en.getKey()).append(".");
						}
						sb.append(c.getCname()).append(")");
					} else if (c.getsType().equals(SType.SUBQUERY)) {
						sb.append(c.getCname());
					}
					if (i < en.getValue().length - 1) {
						sb.append(",");
					}
				}
				if (encs.hasNext()) {
					sb.append(",");
				}
			}
			/**
			 * 中间字段关联
			 */
			addMidColumn(wrmidcms, sb);
			/**
			 * 查询条件
			 */
			Set<Condition> condis = new LinkedHashSet<Condition>();
			Set<Entry<String, Set<Condition>>> es = mconditions.entrySet();
			for (Entry<String, Set<Condition>> ens : es) {
				for (Condition cd : ens.getValue()) {
					if (!cd.getCdType().equals(CdType.FUN)) {
						cd.setTable(ens.getKey());
						condis.add(cd);
					}
				}
			}
			addCondition(condis, sb);

			/**
			 * 分组
			 */
			Iterator<Cuery> cite = groupbys.iterator();
			sb.append(Sentences.GROUPBY.getValue());
			while (cite.hasNext()) {
				Cuery c = cite.next();
				sb.append(c.getTable()).append(".").append(c.getCname());
				if (cite.hasNext()) {
					sb.append(",");
				}
			}
			/**
			 * having
			 */
			if (hvs.size() > 0) {
				sb.append(Sentences.HAVING);
				Iterator<Condition> itec = hvs.iterator();
				while (itec.hasNext()) {
					Condition cd = itec.next();
					sb.append(cd.getFunName()).append("(")
							.append(cd.getTable()).append(".")
							.append(cd.getProperty()).append(")");

					sb.append(cd.getOper().getValue());
					if (cd.getOper().equals(Operators.BETWEEN)) {
						sb.append(" :").append(cd.getTable())
								.append(cd.getTable()).append(cd.getFunName())
								.append(cd.getCplx()).append(cd.getProperty())
								.append(Sentences.AND.getValue());
					} else if (cd.getOper().equals(Operators.IN)
							|| cd.getOper().equals(Operators.NOT_IN)) {
						sb.append("(");
					}
					sb.append(" :").append(cd.getTable())
							.append(cd.getFunName()).append(cd.getCplx())
							.append(cd.getProperty());
					if (cd.getOper().equals(Operators.IN)
							|| cd.getOper().equals(Operators.NOT_IN)) {
						sb.append(")");
					}

					if (itec.hasNext()) {
						sb.append(Sentences.AND);
					}
				}
			}

			/**
			 * 排序
			 */
			addOrderby(orderbys, sb);

			Query qr = getEm().createNativeQuery(sb.toString());
			qr.setFirstResult((curPage - 1) * pageSize);
			qr.setMaxResults(pageSize);
			/**
			 * 添加条件
			 */
			setCondition(mconditions, qr);

			return qr.getResultList();
		}
		return Collections.EMPTY_LIST;

	}

	@Override
	public List<?> getMDistinctList(int curPage, int pageSize,
			LinkedHashMap<String, String[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys) {
		/**
		 * 开始封装查询语句
		 */
		StringBuilder sb = new StringBuilder(Sentences.SELECT.getValue())
				.append(Sentences.DISTINCT.getValue());
		return getList(curPage, pageSize, qrcms, wrmidcms, mconditions,
				orderbys, sb);

	}

	private List<?> getList(int curPage, int pageSize,
			LinkedHashMap<String, String[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys, StringBuilder sb) {
		/**
		 * 添加查询语句
		 */
		sb.append(addSelect(qrcms));
		/**
		 * 中间字段关联
		 */
		addMidColumn(wrmidcms, sb);
		/**
		 * 查询条件
		 */
		addCondition(mconditions, sb);
		/**
		 * 排序
		 */
		addOrderby(orderbys, sb);

		Query qr = getEm().createNativeQuery(sb.toString());
		qr.setFirstResult((curPage - 1) * pageSize);
		qr.setMaxResults(pageSize);
		/**
		 * 添加条件
		 */
		setCondition(mconditions, qr);

		return qr.getResultList();
	}

	@Override
	public Long getMCount(Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions) {
		/**
		 * 开始封装查询语句
		 */
		StringBuilder sb = new StringBuilder("select count(*) ");
		return getCount(wrmidcms, mconditions, sb);

	}

	private Long getCount(Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions, StringBuilder sb) {
		/**
		 * 中间字段关联
		 */
		addMidColumn(wrmidcms, sb);
		/**
		 * 查询条件
		 */
		addCondition(mconditions, sb);
		Query qr = getEm().createNativeQuery(sb.toString());
		/**
		 * 添加条件
		 */
		setCondition(mconditions, qr);
		Number nm = (Number) qr.getSingleResult();
		return nm.longValue();
	}

	@Override
	public Long getMDistinctCount(Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashMap<String, String[]> qrcms) {
		/**
		 * 开始封装查询语句
		 */
		StringBuilder sb = new StringBuilder("select  count(")
				.append(Sentences.DISTINCT.getValue()).append(addSelect(qrcms))
				.append(")");

		return getCount(wrmidcms, mconditions, sb);
	}

	@Override
	public PageInfo<?> getMPageInfo(int curPage, int pageSize,
			LinkedHashMap<String, String[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys) {
		return new PageInfo<>(curPage, pageSize, getMCount(wrmidcms,
				mconditions), getMlist(curPage, pageSize, qrcms, wrmidcms,
				mconditions, orderbys));
	}

	@Override
	public PageInfo<?> getMDistinctPageInfo(int curPage, int pageSize,
			LinkedHashMap<String, String[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys) {
		return new PageInfo<>(curPage, pageSize, getMDistinctCount(wrmidcms,
				mconditions, qrcms), getMDistinctList(curPage, pageSize, qrcms,
				wrmidcms, mconditions, orderbys));
	}

	private String addSelect(LinkedHashMap<String, String[]> qrcms) {
		StringBuilder sb = new StringBuilder();
		if (qrcms == null || qrcms.size() < 1) {
			sb.append("  *   ");
		} else {
			Iterator<Entry<String, String[]>> ite = qrcms.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, String[]> en = ite.next();
				for (int i = 0; i < en.getValue().length; i++) {
					sb.append(en.getKey()).append(".").append(en.getValue()[i]);
					if (i < en.getValue().length - 1) {
						sb.append(",");
					}
				}
				if (ite.hasNext()) {
					sb.append(",");
				}
			}
		}
		return sb.toString();
	}

	private void setCondition(
			LinkedHashMap<String, Set<Condition>> mconditions, Query qr) {
		if (mconditions != null && mconditions.size() > 0) {
			Iterator<Entry<String, Set<Condition>>> ite = mconditions
					.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, Set<Condition>> entry = ite.next();
				Iterator<Condition> itecon = entry.getValue().iterator();
				while (itecon.hasNext()) {
					Condition cd = itecon.next();

					setOrCondition(qr,
							MyUtils.isNotEmpty(cd.getTable()) ? cd.getTable()
									: entry.getKey(), cd);
					if (cd.getSw() != null
							&& MyUtils.isNotEmpty(cd.getSw().getConditions())) {
						Iterator<Condition> cdite = cd.getSw()
								.getConditionList().iterator();
						while (cdite.hasNext()) {
							Condition orcd = cdite.next();
							setOrCondition(
									qr,
									MyUtils.isNotEmpty(orcd.getTable()) ? orcd
											.getTable() : entry.getKey(), orcd);
						}
					}
				}
			}
		}
	}

	private void setOrCondition(Query qr, String entryKey, Condition cd) {
		if (cd.getCdType().equals(CdType.FUN)) {
			if (cd.getOper().equals(Operators.BETWEEN)) {

				qr.setParameter(
						entryKey + entryKey + cd.getFunName() + cd.getCplx()
								+ cd.getProperty(), cd.getFirstValue());
				qr.setParameter(
						entryKey + cd.getFunName() + cd.getCplx()
								+ cd.getProperty(), cd.getValue());

			} else {
				if (cd.getValue() != null) {
					qr.setParameter(entryKey + cd.getFunName() + cd.getCplx()
							+ cd.getProperty(), cd.getValue());
				}
			}
		} else {
			if (cd.getOper().equals(Operators.BETWEEN)) {
				qr.setParameter(
						entryKey + entryKey + cd.getCplx() + cd.getProperty(),
						cd.getFirstValue());
				qr.setParameter(entryKey + cd.getCplx() + cd.getProperty(),
						cd.getValue());
			} else {
				if (cd.getValue() != null) {
					qr.setParameter(entryKey + cd.getCplx() + cd.getProperty(),
							cd.getValue());
				}
			}
		}
	}

	private void addOrderby(LinkedHashSet<OrderBy> orderbys, StringBuilder sb) {
		if (orderbys != null && orderbys.size() > 0) {

			sb.append(Sentences.ORDERBY.getValue());
			Iterator<OrderBy> odite = orderbys.iterator();
			while (odite.hasNext()) {
				OrderBy entry = odite.next();
				if (entry.getFunName() != null
						&& entry.getFunName().trim().length() != 0) {
					sb.append(entry.getFunName()).append("(");
				}
				sb.append(entry.getTable()).append(".")
						.append(entry.getPropertyName());
				if (entry.getFunName() != null
						&& entry.getFunName().trim().length() != 0) {
					sb.append(")");
				}
				if (entry.getIsDesc()) {
					sb.append(Sentences.DESC.getValue());
				}
				if (odite.hasNext()) {
					sb.append(",");
				}
			}
		}
	}

	private void addMidColumn(Midcm wrmidcms, StringBuilder sb) {
		sb.append(Sentences.FROM.getValue());

		if (wrmidcms != null && wrmidcms.getTables() != null
				&& wrmidcms.getTables().length > 0
				&& wrmidcms.getCnames() != null
				&& wrmidcms.getCnames().length == wrmidcms.getTables().length) {
			LinkedHashSet<String> tbls = new LinkedHashSet<String>();
			for (int i = 0; i < wrmidcms.getTables().length; i++) {
				tbls.add(wrmidcms.getTables()[i]);
			}
			Iterator<String> tbite = tbls.iterator();
			while (tbite.hasNext()) {
				sb.append(tbite.next());
				if (tbite.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(Sentences.WHERE.getValue());

			for (int i = 0; i < wrmidcms.getTables().length; i++) {
				sb.append(wrmidcms.getTables()[i]).append(".")
						.append(wrmidcms.getCnames()[i]).append("=");

				sb.append(wrmidcms.getTables()[++i]).append(".")
						.append(wrmidcms.getCnames()[i]);
				if (i < wrmidcms.getTables().length - 1) {
					sb.append(Sentences.AND.getValue());
				}
			}
		} else {
			throw new RuntimeException("无效的查询语句，没有设置多表关联字段");
		}
	}

	private void addCondition(
			LinkedHashMap<String, Set<Condition>> mconditions, StringBuilder sb) {
		if (mconditions != null && mconditions.size() > 0) {
			sb.append(Sentences.AND.getValue());
			Iterator<Entry<String, Set<Condition>>> ite = mconditions
					.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, Set<Condition>> entry = ite.next();
				Iterator<Condition> itecon = entry.getValue().iterator();
				while (itecon.hasNext()) {
					Condition cd = itecon.next();
					if (cd.getSw() != null
							&& MyUtils.isNotEmpty(cd.getSw().getConditions())) {
						sb.append("(");
					}
					apCondition(sb,
							MyUtils.isNotEmpty(cd.getTable()) ? cd.getTable()
									: entry.getKey(), cd);
					if (cd.getSw() != null
							&& MyUtils.isNotEmpty(cd.getSw().getConditions())) {
						Iterator<Condition> cdite = cd.getSw()
								.getConditionList().iterator();
						sb.append(Sentences.OR.getValue());
						sb.append("(");
						while (cdite.hasNext()) {

							Condition orcd = cdite.next();
							apCondition(
									sb,
									MyUtils.isNotEmpty(orcd.getTable()) ? orcd
											.getTable() : entry.getKey(), orcd);
							if (cdite.hasNext()) {
								if (cd.getSw().getSqlOp().equals(SqlOp.AND)) {
									sb.append(Sentences.AND.getValue());
								} else {
									sb.append(Sentences.OR.getValue());
								}
							}
						}
						sb.append(")");
					}
					if (cd.getSw() != null
							&& MyUtils.isNotEmpty(cd.getSw().getConditions())) {
						sb.append(")");
					}
					if (itecon.hasNext()) {
						sb.append(Sentences.AND.getValue());
					}
				}
				if (ite.hasNext()) {
					sb.append(Sentences.AND.getValue());
				}
			}

		}
	}

	private void apCondition(StringBuilder sb, String tablenm, Condition cd) {
		if (cd.getCdType().equals(CdType.VL)) {
			if (cd.getValue() == null) {
				sb.append("(");
			}
			sb.append(tablenm).append(".").append(cd.getProperty());
			if (cd.getValue() == null) {
				if (cd.getOper().equals(Operators.EQ)) {
					sb.append(Sentences.IS_NULL.getValue());
					sb.append(Sentences.OR.getValue());
				} else if (cd.getOper().equals(Operators.NOT_EQ)) {
					sb.append(Sentences.IS_NOT_NULL.getValue());
					sb.append(Sentences.AND.getValue());
				} else {
					throw new NullPointerException("请给查询条件赋值");
				}

				sb.append(tablenm).append(".").append(cd.getProperty());
				sb.append(cd.getOper().getValue()).append("''");
				sb.append(")");
			} else {
				sb.append(cd.getOper().getValue());
				if (cd.getOper().equals(Operators.BETWEEN)) {
					sb.append(" :").append(tablenm).append(tablenm)
							.append(cd.getCplx()).append(cd.getProperty())
							.append(Sentences.AND.getValue());
				} else if (cd.getOper().equals(Operators.IN)
						|| cd.getOper().equals(Operators.NOT_IN)) {
					sb.append("(");
				}
				sb.append(" :").append(tablenm).append(cd.getCplx())
						.append(cd.getProperty());
				if (cd.getOper().equals(Operators.IN)
						|| cd.getOper().equals(Operators.NOT_IN)) {
					sb.append(")");
				}

			}
		} else {
			sb.append(cd.getTable()).append(".").append(cd.getProperty());
			sb.append(cd.getOper().getValue());
			sb.append(cd.getTable()).append(".").append(cd.getValue());
		}
	}

	private void addCondition(Set<Condition> mconditions, StringBuilder sb) {
		if (mconditions != null && mconditions.size() > 0) {
			sb.append(Sentences.AND.getValue());
			Iterator<Condition> ite = mconditions.iterator();
			while (ite.hasNext()) {
				Condition cd = ite.next();
				if (cd.getCdType().equals(CdType.VL)) {
					if (cd.getValue() == null) {
						sb.append("(");
					}
					sb.append(cd.getTable()).append(".")
							.append(cd.getProperty());
					if (cd.getValue() == null) {
						if (cd.getOper().equals(Operators.EQ)) {
							sb.append(Sentences.IS_NULL.getValue());
							sb.append(Sentences.OR.getValue());
						} else if (cd.getOper().equals(Operators.NOT_EQ)) {
							sb.append(Sentences.IS_NOT_NULL.getValue());
							sb.append(Sentences.AND.getValue());
						} else {
							throw new NullPointerException("请给查询条件赋值");
						}

						sb.append(cd.getTable()).append(".")
								.append(cd.getProperty());
						sb.append(cd.getOper().getValue()).append("''");
						sb.append(")");
					} else {
						sb.append(cd.getOper().getValue());
						if (cd.getOper().equals(Operators.BETWEEN)) {
							sb.append(" :").append(cd.getTable())
									.append(cd.getTable()).append(cd.getCplx())
									.append(cd.getProperty())
									.append(Sentences.AND.getValue());
						} else if (cd.getOper().equals(Operators.IN)
								|| cd.getOper().equals(Operators.NOT_IN)) {
							sb.append("(");
						}
						sb.append(" :").append(cd.getTable())
								.append(cd.getCplx()).append(cd.getProperty());
						if (cd.getOper().equals(Operators.IN)
								|| cd.getOper().equals(Operators.NOT_IN)) {
							sb.append(")");
						}

					}
				} else {
					sb.append(cd.getTable()).append(".")
							.append(cd.getProperty());
					sb.append(cd.getOper().getValue());
					sb.append(cd.getTable()).append(".").append(cd.getValue());
				}
				if (ite.hasNext()) {
					sb.append(Sentences.AND.getValue());
				}
			}

		}
	}
}
