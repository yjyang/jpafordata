package com.fd.dao.base.impl;

import java.io.Reader;
import java.lang.reflect.Field;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.fd.dao.base.IBaseDao;
import com.fd.dao.base.ICommonDao;
import com.fd.dao.base.common.Condition;
import com.fd.dao.base.common.MultiPartInfo;
import com.fd.dao.base.common.PageInfo;
import com.fd.dao.base.em.MergeType;
import com.fd.dao.base.em.Operators;
import com.fd.dao.base.em.Sentences;
import com.fd.util.MyUtils;

public abstract class CommonDao implements ICommonDao {
	private static final long serialVersionUID = -5469780272067822450L;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PageInfo getMultipartPageinfo(List<MultiPartInfo> list, int curPage,
			int pageSize) {
		try {
			if (list != null && list.size() > 0) {
				StringBuffer buf = new StringBuffer(Sentences.SELECT.getValue());
				addqueryProperty(list, buf);
				addOrderby(list, buf);
				Query qr = getEm().createNativeQuery(buf.toString());
				setConditionValue(list, qr);
				qr.setFirstResult((curPage - 1) * pageSize);
				qr.setMaxResults(pageSize);
				PageInfo data = new PageInfo(curPage, pageSize,
						getMultipartqueryCount(list), qr.getResultList());
				if (data.getDataList().size() > 0) {
					if (!(data.getDataList().get(0) instanceof Object[])) {
						for (int i = 0; i < data.getDataList().size(); i++) {
							Object o = data.getDataList().get(i);
							if (o != null) {
								if (o.getClass().getSimpleName()
										.startsWith("$Proxy")) {
									Clob c = null;
									c = (Clob) o
											.getClass()
											.getDeclaredMethod(
													"getWrappedClob",
													new Class[] {})
											.invoke(o, null);
									Reader rd = null;
									rd = c.getCharacterStream();
									char[] cbuf = null;
									cbuf = new char[(int) c.length()];
									rd.read(cbuf);
									data.getDataList().set(i, new String(cbuf));
									rd.close();
								}
							}
						}

					} else {
						for (Object os : data.getDataList()) {
							Object[] oss = (Object[]) os;
							for (int i = 0; i < oss.length; i++) {
								if (oss[i] != null) {
									if (oss[i].getClass().getSimpleName()
											.startsWith("$Proxy")) {

										Clob c = null;
										c = (Clob) oss[i]
												.getClass()
												.getDeclaredMethod(
														"getWrappedClob",
														new Class[] {})
												.invoke(oss[i], null);
										Reader rd = null;
										rd = c.getCharacterStream();
										char[] cbuf = null;
										cbuf = new char[(int) c.length()];
										rd.read(cbuf);
										oss[i] = new String(cbuf);
										rd.close();
									}
								}
							}
						}
					}
					return data;
				}

			}
			return new PageInfo(curPage, pageSize, 0, new ArrayList());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Long getMultipartqueryCount(List<MultiPartInfo> list) {
		Iterator<MultiPartInfo> ite = list.iterator();
		StringBuffer ctbfu = new StringBuffer(" SELECT  COUNT(*)  ");
		StringBuilder wherebuf = new StringBuilder();
		StringBuilder innerbuf = new StringBuilder();

		int c = 96;
		char fg = (char) c;
		String oldmidcol = "";
		while (ite.hasNext()) {
			fg = (char) ++c;
			MultiPartInfo mt = ite.next();
			Condition[] conditions = mt.getConditions();
			oldmidcol = addInnerJoin(innerbuf, c, fg, oldmidcol, mt);
			addWhereSql(wherebuf, fg, conditions);
		}
		if (wherebuf.length() > 5) {
			wherebuf.delete(wherebuf.length() - 5, wherebuf.length());
		}
		ctbfu.append(innerbuf).append(wherebuf);
		Query qr = getEm().createNativeQuery(ctbfu.toString());
		setConditionValue(list, qr);

		Object o = qr.getSingleResult();
		Number nb = (Number) o;
		return nb.longValue();
	}

	/*
	 * 多表链接查询添加条件的值
	 */
	private static void setConditionValue(List<MultiPartInfo> list, Query qr) {
		Iterator<MultiPartInfo> ite = list.iterator();
		int c = 96;
		char fg = (char) c;
		while (ite.hasNext()) {
			fg = (char) ++c;
			MultiPartInfo mt = ite.next();
			if (mt.getConditions() != null && mt.getConditions().length > 0) {
				for (Condition cd : mt.getConditions()) {
					if (cd.getOper().equals(Operators.BETWEEN)) {
						qr.setParameter(fg + alias + cd.getProperty(),
								cd.getFirstValue());
						qr.setParameter(fg + cd.getProperty(), cd.getValue());
					} else if (cd.getOper().equals(Operators.LIKE)) {
						qr.setParameter(fg + cd.getProperty(), cd.getValue());
					} else {
						qr.setParameter(fg + cd.getProperty(), cd.getValue());
					}
				}
			}
		}
	}

	/*
	 * 多表链接查询添加排序语句
	 */
	public static void addOrderby(List<MultiPartInfo> list, StringBuffer buf) {
		Iterator<MultiPartInfo> ite = list.iterator();
		int c = 96;
		char fg = (char) c;
		StringBuffer od = new StringBuffer("  order  by");
		while (ite.hasNext()) {
			fg = (char) ++c;
			MultiPartInfo mt = ite.next();
			Map<String, Object> orders = mt.getOrderbys();
			if (orders.size() > 0) {
				Set<Entry<String, Object>> entryset = orders.entrySet();
				od.append("   ");
				for (Entry<String, Object> en : entryset) {

					od.append(fg).append(".").append(en.getKey()).append("  ")
							.append(en.getValue()).append(",");
				}

			}
		}
		if (!od.toString().endsWith("by")) {
			od.deleteCharAt(od.length() - 1);
			buf.append(od);
		}

	}

	/*
	 * 多表链接查询拼接sql语句
	 */
	public static void addqueryProperty(List<MultiPartInfo> list,
			StringBuffer buf) {
		Iterator<MultiPartInfo> ite = list.iterator();
		StringBuilder cdbuf = new StringBuilder();
		StringBuilder innerbuf = new StringBuilder();
		int c = 96;
		char fg = (char) c;
		String oldmidcol = "";
		while (ite.hasNext()) {
			fg = (char) ++c;
			MultiPartInfo mt = ite.next();
			String[] columns = mt.getColumns();
			Condition[] conditions = mt.getConditions();
			addWhereSql(cdbuf, fg, conditions);
			if (columns != null) {
				for (int i = 0; i < columns.length; i++) {
					buf.append(fg).append(".");
					if (buf.indexOf("." + columns[i]) != -1) {
						buf.append(columns[i]).append("   ")
								.append(fg + columns[i]).append(",");
					} else {
						buf.append(columns[i]).append(",");
					}
				}

			}
			oldmidcol = addInnerJoin(innerbuf, c, fg, oldmidcol, mt);
		}
		buf.deleteCharAt(buf.length() - 1);
		buf.append("  ").append(innerbuf);
		if (cdbuf.indexOf("and") != -1) {
			buf.append(cdbuf.delete(cdbuf.length() - 4, cdbuf.length()));
		}
	}

	/*
	 * 得到InnerJoinSql
	 */
	private static String addInnerJoin(StringBuilder innerbuf, int c, char fg,
			String oldmidcol, MultiPartInfo mt) {
		if (innerbuf.length() == 0) {
			innerbuf.append(" from  ").append(mt.getTables()).append("  ")
					.append(fg);
		} else {
			int cc = c;
			innerbuf.append(" inner join  ").append(mt.getTables())
					.append("  ").append(fg).append("  ").append(" on ")
					.append((char) --cc).append(".").append(oldmidcol)
					.append("=").append(fg).append(".")
					.append(mt.getMidcolumn());
		}
		oldmidcol = mt.getMidcolumn();
		return oldmidcol;
	}

	/*
	 * 多表链接查询，添加where语句
	 */
	private static void addWhereSql(StringBuilder cdbuf, char fg,
			Condition[] conditions) {
		if (conditions != null && conditions.length > 0) {
			for (int i = 0; i < conditions.length; i++) {
				Condition cdt = conditions[i];
				if (cdbuf.length() == 0) {
					cdbuf.append(" where ");
				}
				cdbuf.append(fg).append(".").append(cdt.getProperty());
				if (cdt.getOper().equals(Operators.EQ)) {
					cdbuf.append("=");
				} else if (cdt.getOper().equals(Operators.NOT_EQ)) {
					cdbuf.append("<>");
				} else if (cdt.getOper().equals(Operators.BETWEEN)) {
					cdbuf.append("  between  ").append(":").append(fg)
							.append(alias).append(cdt.getProperty())
							.append("  and  ");
				} else if (cdt.getOper().equals(Operators.IN)
						|| cdt.getOper().equals(Operators.NOT_IN)) {
					cdbuf.append(cdt.getOper().getValue()).append("(");
				} else if (cdt.getOper().equals(Operators.LIKE)) {
					cdbuf.append(" like  ");
				} else if (cdt.getOper().equals(Operators.LT)) {
					cdbuf.append(" < ");
				} else if (cdt.getOper().equals(Operators.GT)) {
					cdbuf.append(" > ");
				} else if (cdt.getOper().equals(Operators.GE)) {
					cdbuf.append(" >= ");
				} else if (cdt.getOper().equals(Operators.LE)) {
					cdbuf.append(" <= ");
				}

				cdbuf.append(":").append(fg).append(cdt.getProperty());
				if (cdt.getOper().equals(Operators.IN)
						|| cdt.getOper().equals(Operators.NOT_IN)) {
					cdbuf.append(")");
				}
				cdbuf.append(" and ");
			}
		}
	}

	@Override
	public <POJO> void mergeBatch(IBaseDao<POJO>[] daos, POJO[] pojos,
			MergeType... types) {
		if (daos != null && pojos != null && daos.length == pojos.length) {
			if (types != null && types.length == daos.length) {
				for (int i = 0; i < daos.length; i++) {
					if (types[i].equals(MergeType.DELTE)) {
						daos[i].delete(pojos[i]);
					} else if (types[i].equals(MergeType.UPDATE)) {
						daos[i].update(pojos[i]);
					} else if (types[i].equals(MergeType.SAVE)) {
						daos[i].save(pojos[i]);
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void mergeBatch(Map<MergeType, List> updaos) {
		if (updaos != null && updaos.size() > 0) {
			Set<Entry<MergeType, List>> entryset = updaos.entrySet();
			for (Entry<MergeType, List> en : entryset) {
				for (Object p : en.getValue()) {
					if (en.getKey().equals(MergeType.SAVE)) {
						getEm().persist(p);
					} else if (en.getKey().equals(MergeType.UPDATE)) {
						getEm().merge(p);
					} else if (en.getKey().equals(MergeType.DELTE)) {
						getEm().remove(p);
					}
				}
			}
		}
	}

	/***
	 * 动态封装成域模型对象
	 * 
	 * @param data
	 * @param clazz
	 * @param targetProperty
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> PageInfo<T> convert2VO(PageInfo data, Class<T> clazz,
			String... targetProperty) {
		try {
			if (data != null && targetProperty.length > 0
					&& data.getDataList().size() > 0) {
				List<T> dataList = new ArrayList<T>();
				if (!(data.getDataList().get(0) instanceof Object[])) {
					String p = targetProperty[0];
					for (Object o : data.getDataList()) {
						T obj = (T) clazz.newInstance();
						Field fd = MyUtils.getfd(clazz, p);
						fd.setAccessible(true);
						fd.set(obj, o);
						dataList.add(obj);
					}

				} else {
					for (Object os : data.getDataList()) {
						Object[] oss = (Object[]) os;
						T obj = (T) clazz.newInstance();
						for (int i = 0; i < targetProperty.length; i++) {
							Field fd = MyUtils.getfd(clazz, targetProperty[i]);
							fd.setAccessible(true);
							if (oss[i] instanceof Number) {
								Number nb = (Number) oss[i];
								if (fd.getType().equals(Double.class)
										|| fd.getType().equals(double.class)) {
									fd.set(obj, nb.doubleValue());
								} else if (fd.getType().equals(byte.class)
										|| fd.getType().equals(Byte.class)) {

									fd.set(obj, nb.byteValue());

								} else if (fd.getType().equals(long.class)
										|| fd.getType().equals(Long.class)) {

									fd.set(obj, nb.longValue());

								} else if (fd.getType().equals(float.class)) {

									fd.set(obj, nb.floatValue());

								} else if (fd.getType().equals(Float.class)) {
									fd.set(obj, nb.floatValue());
								} else if (fd.getType().equals(Integer.class)) {
									fd.set(obj, nb.intValue());
								} else if (fd.getType().equals(int.class)) {
									fd.set(obj, nb.intValue());
								} else if (fd.getType().equals(Boolean.class)
										|| fd.getType().equals(boolean.class)) {
									if (nb.intValue() == 1) {
										fd.set(obj, true);
									} else {
										fd.set(obj, false);
									}
								} else {
									fd.set(obj, nb);
								}
							} else {
								if (fd.getType().getSuperclass()
										.getSimpleName()
										.equalsIgnoreCase("enum")) {
									fd.set(obj,
											fd.getType()
													.getDeclaredMethod(
															"valueOf",
															String.class)
													.invoke(null, oss[i]));

								} else {
									fd.set(obj, oss[i]);
								}

							}
						}
						dataList.add(obj);

					}
				}

				data.setDataList(dataList);
				return data;
			}
			return new PageInfo<T>(1, 20, 0, new ArrayList());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void flush() {
		getEm().flush();
	}

	@Override
	public void clear() {
		getEm().clear();
	}

	/**
	 * 得到实体管理器
	 * 
	 * @return
	 */
	protected abstract EntityManager getEm();

	/**
	 * 别名
	 */
	protected static String alias = "o";
}
