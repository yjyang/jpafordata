package com.fd.dao.base;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fd.dao.base.bean.Cuery;
import com.fd.dao.base.bean.JoinQuery;
import com.fd.dao.base.bean.Midcm;
import com.fd.dao.base.common.Condition;
import com.fd.dao.base.common.OrderBy;
import com.fd.dao.base.common.PageInfo;

/***
 * 多表查询
 * 
 * @author 符冬
 * 
 */
public interface IMQuery extends ICommonDao {
	/***
	 * 自连接列表页数据查询
	 * <p>
	 * // 查询哪几个表的哪几个字段 LinkedHashMap<String, String[]> qrcms = new
	 * LinkedHashMap<>(); qrcms.put("employee", new String[] {
	 * "sid employeesid", "ename" }); qrcms.put("departMent", new String[] {
	 * "sid", "dname" }); qrcms.put("UserRole", new String[] { "sid roleid",
	 * "roleName" }); // 条件 LinkedHashMap<String, Set<Condition>> mconditions =
	 * new LinkedHashMap<String, Set<Condition>>(); //
	 * mconditions.put("employee", Condition.getConditions( new Condition("sid",
	 * MyUtils.getListByStrs("3"), Operators.NOT_IN), new Condition("sid",
	 * Operators.IN, MyUtils.getListByStrs("1", "2", "3")))); // //
	 * mconditions.put("departMent", Condition.getConditions(new Condition(
	 * "sid", Operators.NOT_EQ, null), new Condition("dname", Operators.LIKE,
	 * "部"), new Condition("sid", Operators.BETWEEN, 1l, 10l)));
	 * mconditions.put("UserRole", Condition.getConditions(new Condition(
	 * "roleName", Operators.LIKE, "管理员"), new Condition("dpId", Operators.IN,
	 * Arrays.asList(3l, 2l)))); // 获取结果集 List list = IMQuery.getMlist(1, 3,
	 * qrcms, Midcm.getMidCm("employee", "dptId", "departMent", "sid",
	 * "departMent", "sid", "UserRole", "dpId"), mconditions, OrderBy.getSet(new
	 * OrderBy("sid", true, "employee"), new OrderBy("sid", false,
	 * "departMent"))); log.info("size:" + list.size()); List<ObjVo> ovs =
	 * MyUtils.packageObject(ObjVo.class, list, "eid", "ename", "did", "dname",
	 * "rid", "rname"); for (ObjVo vo : ovs) { log.info(vo.getEid());
	 * log.info(vo.getDid()); log.info(vo.getRid());
	 * 
	 * }
	 * </p>
	 * 
	 * @param curPage
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @param qrcms
	 *            SELECT语句查询字段集
	 * @param wrmidcms
	 *            自连接字段集
	 * @param mconditions
	 *            多表查询条件
	 * @param orderbys
	 *            排序字段
	 * @return List<object,object[]>
	 */
	List getMlist(int curPage, int pageSize,
			LinkedHashMap<String, String[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys);

	/**
	 * 多表自联结总记录数查询
	 * 
	 * @param wrmidcms
	 *            自连接字段集
	 * @param mconditions
	 *            多表查询条件
	 * @return 总记录
	 */
	Long getMCount(Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions);

	/**
	 * 自连接分页查询
	 * 
	 * @param curPage
	 *            当前页
	 * @param pageSize
	 *            每页查询多少条
	 * @param qrcms
	 *            查询字段
	 * @param wrmidcms
	 *            自连接关联字段集
	 * @param mconditions
	 *            查询条件
	 * @param orderbys
	 *            排序字段
	 * @return PageInfo<object,object[]>
	 */
	PageInfo getMPageInfo(int curPage, int pageSize,
			LinkedHashMap<String, String[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys);

	/**
	 * 统计去掉重复值的总数量
	 * 
	 * @param wrmidcms
	 *            自联结关联字段
	 * @param mconditions
	 *            条件
	 * @param qrcms
	 *            <表名，字段集> 重复值判断字段集合
	 * @return
	 */
	Long getMDistinctCount(Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashMap<String, String[]> qrcms);

	/**
	 * 获取去掉重复值后的数据列表
	 * 
	 * @param curPage
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @param qrcms
	 *            <表名，字段集> 重复值判断字段集合
	 * @param wrmidcms
	 *            关联字段
	 * @param mconditions
	 *            条件
	 * @param orderbys
	 *            排序
	 * @return
	 */
	List getMDistinctList(int curPage, int pageSize,
			LinkedHashMap<String, String[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys);

	/**
	 * 获取去掉重复值后的分页数据
	 * 
	 * @param curPage
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @param qrcms
	 *            <表名，字段集> 重复值判断字段集合
	 * @param wrmidcms
	 *            自联结关联字段
	 * @param mconditions
	 *            条件
	 * @param orderbys
	 *            排序
	 * @return
	 */
	PageInfo getMDistinctPageInfo(int curPage, int pageSize,
			LinkedHashMap<String, String[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys);

	/**
	 * 多表分组数据列表查询
	 * 
	 * @param curPage
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @param qrcms
	 *            查询数据和分组字段
	 * @param wrmidcms
	 *            关联字段
	 * @param mconditions
	 *            条件
	 * @param orderbys
	 *            排序
	 * @return
	 */
	List getMGroupbylist(int curPage, int pageSize,
			LinkedHashMap<String, Cuery[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions,
			LinkedHashSet<OrderBy> orderbys);

	/**
	 * 多表自联结分组总记录数查询
	 * 
	 * @param qrcms
	 * @param wrmidcms
	 * @param mconditions
	 * @return
	 */
	Long getMGroupbyCount(LinkedHashMap<String, Cuery[]> qrcms, Midcm wrmidcms,
			LinkedHashMap<String, Set<Condition>> mconditions);

	/**
	 * 得到多表联结查询数据
	 * 
	 * @param jqpt
	 * @param curPage
	 * @param pageSize
	 * @return
	 */
	List<?> getJoinList(JoinQuery jqpt, int curPage, int pageSize);

	/**
	 * 得到多表联结查询记录数
	 * 
	 * @param jqpt
	 * @return
	 */
	Long getJoinCount(JoinQuery jqpt);

	/**
	 * 得到多表联结查询分页列表
	 * 
	 * @param jqpt
	 * @param curPage
	 * @param pageSize
	 * @return
	 */
	PageInfo getJoinPageInfo(JoinQuery jqpt, int curPage, int pageSize);
}
