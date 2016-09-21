package com.fd.dao.base;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fd.dao.base.common.Condition;
import com.fd.dao.base.common.OrderBy;
import com.fd.dao.base.common.PageInfo;
import com.fd.dao.base.common.SqlWhere;
import com.fd.dao.base.em.SqlOp;

/***
 * 通用DAO,封装所有底层数据库访问细节-采用JPA实现
 * 
 * @author 符冬
 * 
 */
public interface IBaseDao<POJO> extends Serializable, ICommonDao {
	/**
	 * 保存对象
	 * 
	 * @param pojo
	 */
	void save(POJO pojo);

	/**
	 * 根据条件得到对象数量
	 * 
	 * @param conditions
	 * @return
	 */
	Long getCount(Set<Condition> conditions);

	/**
	 * 得到所有对象数量
	 * 
	 * @param conditions
	 * @return
	 */
	Long getCount();

	/***
	 * 得到分页对象
	 * 
	 * @param dataList
	 *            数据
	 * @param datacount
	 *            总记录数
	 * @return
	 */
	PageInfo<POJO> getPageInfo(List<POJO> dataList, Long datacount,
			int curPage, int pageSize);

	/**
	 * 得到自定义类型分页数据对象
	 * 
	 * @param <T>
	 * @param dataList
	 * @param datacount
	 * @param curPage
	 * @param pageSize
	 * @return
	 */
	<T> PageInfo<T> getOptionPageInfo(List<T> dataList, Long datacount,
			int curPage, int pageSize);

	/**
	 * 更新对象
	 * 
	 * @param pojo
	 */
	void update(POJO pojo);

	/**
	 * 删除对象
	 * 
	 * @param pojo
	 */
	void delete(POJO pojo);

	/**
	 * 根据对象标识符删除对象
	 * 
	 * @param sid
	 */
	void deleteById(Serializable... sid);

	/**
	 * 根据对象标识符得到一个对象
	 * 
	 * @param sid
	 * @return
	 */
	POJO getById(Serializable sid);

	/***
	 * 得到托管对象,只包含对象标识符，不包含其他数据 <br/>
	 * 如果没有会报异常
	 * 
	 * @param sid
	 * @return
	 */
	POJO getReference(Serializable sid);

	/**
	 * 无条件分页数据列表
	 * 
	 * @param curPage
	 * @param pageSize
	 * @return
	 */
	List<POJO> getList(int curPage, int pageSize);

	/***
	 * 得到表中所有记录
	 * 
	 * @return
	 */
	List<POJO> getList();

	/**
	 * 得到指定数量的记录
	 * 
	 * @param pageSize
	 * @return
	 */
	List<POJO> getList(int pageSize);

	/***
	 * 无条件分页排序数据列表
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param orderby
	 * @return
	 */
	List<POJO> getList(int curPage, int pageSize,
			LinkedHashMap<String, String> orderby);

	/***
	 * 无条件所有排序记录集合
	 * 
	 * @param orderby
	 * @return
	 */
	List<POJO> getListOrderBy(LinkedHashMap<String, String> orderby);

	/***
	 * 得到符合条件的分页数据列表
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param conditions
	 * @return
	 */
	List<POJO> getList(Set<Condition> conditions, int curPage, int pageSize);

	/***
	 * 得到符合条件的分页排序数据列表
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param conditions
	 * @param orderby
	 * @return
	 */
	List<POJO> getList(int curPage, int pageSize, Set<Condition> conditions,
			LinkedHashMap<String, String> orderby);

	/***
	 * 根据条件得到指定属性值的排序分页列表
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param conditions
	 * @param orderby
	 * @param propertys
	 * @return
	 */
	List<POJO> getList(int curPage, int pageSize, Set<Condition> conditions,
			LinkedHashMap<String, String> orderby, String... propertys);

	/***
	 * 得到所有指定字段值列表
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param orderby
	 * @param propertys
	 * @return
	 */
	List<POJO> getList(LinkedHashMap<String, String> orderby,
			String... propertys);

	/***
	 * 得到排序列表
	 * 
	 * @param orderby
	 * @param conditions
	 * @return
	 */
	List<POJO> getListOrderby(LinkedHashMap<String, String> orderby,
			Set<Condition> conditions);

	/***
	 * 得到所有指定字段值排序列表
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param orderby
	 * @param propertys
	 * @return
	 */
	List<POJO> getList(LinkedHashMap<String, String> orderby,
			Set<Condition> conditions, String... propertys);

	/***
	 * 更据条件查询指定字段值的列表
	 * 
	 * @param conditions
	 * @param propertys
	 * @return
	 */
	List<POJO> getListBySgCd(Set<Condition> conditions, String... propertys);

	/**
	 * 分页查询数据
	 * 
	 * @param curPage
	 *            当前页
	 * @param pageSize
	 *            每页显示多少条记录
	 * @param conditions
	 *            查询条件
	 * @param orderby
	 *            排序
	 * @param propertys
	 *            查询哪些属性的值（属性的名称）
	 * @return
	 */
	PageInfo<POJO> getPageInfo(int curPage, int pageSize,
			Set<Condition> conditions, LinkedHashMap<String, String> orderby,
			String... propertys);

	/**
	 * 所有记录分页
	 * 
	 * @param curPage
	 * @param pageSize
	 * @return
	 */
	PageInfo<POJO> getPageInfo(int curPage, int pageSize);

	/**
	 * 所有记录排序分页
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param orderby
	 * @return
	 */
	PageInfo<POJO> getPageInfo(int curPage, int pageSize,
			LinkedHashMap<String, String> orderby);

	/****
	 * 指定属性字段的所有记录排序分页
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param orderby
	 * @param propertys
	 * @return
	 */
	PageInfo<POJO> getPageInfo(int curPage, int pageSize,
			LinkedHashMap<String, String> orderby, String... propertys);

	/***
	 * 根据条件排序分页
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param conditions
	 * @param orderby
	 * @return
	 */
	PageInfo<POJO> getPageInfo(int curPage, int pageSize,
			Set<Condition> conditions, LinkedHashMap<String, String> orderby);

	/***
	 * 根据条件分页
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param conditions
	 * @return
	 */
	PageInfo<POJO> getPageInfo(Set<Condition> conditions, int curPage,
			int pageSize);

	/***
	 * 根据条件查询对象列表
	 * 
	 * @param conditions
	 * @return
	 */
	List<POJO> getListByCondition(Set<Condition> conditions);

	/***
	 * 根据条件查询一个对象
	 * 
	 * @param conditions
	 * @return
	 */
	POJO get(Set<Condition> conditions);

	/***
	 * 根据条件得到对象，可以只查询到部分属性的值
	 * 
	 * @param conditions
	 * @param propertys
	 * @return
	 */
	POJO get(Set<Condition> conditions, String... propertys);

	/**
	 * 根据条件删除数据
	 * 
	 * @param conditions
	 */
	void deleteByCondition(Set<Condition> conditions);

	/***
	 * 根据属性名称统计该属性符合条件的数据总和，只能是Number类型，不能其他类型，请知悉。
	 * 
	 * @param conditions
	 * @param property
	 * @return
	 */
	double getSumPropertyValue(Set<Condition> conditions, String property);

	/***
	 * 根据属性名称统计该属性符合条件的平均值，该属性只能是number类型，不能是其他类型，请知悉
	 * 
	 * @param condition
	 * @param property
	 * @return
	 */
	double getAvgPropertyValue(Set<Condition> condition, String property);

	/***
	 * 根据条件批量修改数据
	 * 
	 * @param conditions
	 *            条件
	 * @param newValues
	 *            修改的属性<>值
	 */
	void updateByCondition(Set<Condition> conditions,
			Map<String, Object> newValues);

	/**
	 * 批量修改集合当中的对象
	 * 
	 * @param poList
	 */
	void updateByList(List<POJO> poList);

	/***
	 * 批量保存对象
	 * 
	 * @param pojos
	 */
	void saveBatch(List<POJO> pojos);

	/***
	 * 持久化对象之后返回该托管对象
	 * 
	 * @param pojo
	 * @return 托管对象
	 */
	POJO persist(POJO pojo);

	/***
	 * 根据条件批量修改
	 * 
	 * @param conditions
	 * @param newValues
	 * @return
	 */
	int update(Set<Condition> conditions, Map<String, Object> newValues);

	/***
	 * 修改一个对象，然后把修改后的对象返回
	 * 
	 * @param pojo
	 * @return
	 */
	POJO updateAndRetrun(POJO pojo);

	/**
	 * 根据函数的名称，计算统计当前属性的值
	 * 
	 * @param conditions
	 * @param property
	 * @param functionName
	 * @return
	 */
	double getFunctionPropertyValue(Set<Condition> conditions, String property,
			String functionName);

	/**
	 * Sql语句直接查询
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param sws
	 *            条件
	 * @param orderby
	 * @param props
	 * @return
	 */
	List<POJO> getListBySql(int curPage, int pageSize, SqlWhere[] sws,
			LinkedHashMap<String, String> orderby, String... props);

	/**
	 * 根据sql语句分页查询数据
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param sws
	 *            条件
	 * @param orderby
	 * @param props
	 * @return
	 */
	PageInfo<POJO> getPageinfoBySql(int curPage, int pageSize, SqlWhere[] sws,
			LinkedHashMap<String, String> orderby, String... props);

	/**
	 * 按照SQL语句查询数量
	 * 
	 * @param sws
	 * @return
	 */
	Long getCountBySql(SqlWhere[] sws);

	/**
	 * 分组查询
	 * <p>
	 * 返回记录包含分组字段和统计函数的值,统计函数的值在前分组字段的值在后
	 * <p>
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param conditions
	 *            条件
	 * @param orderby
	 *            排序字段
	 * @param funtions
	 *            <统计函数名称,统计字段的名称> 可以是任意字段
	 * @param groupby
	 *            需要分组的字段
	 * @return List<Object[]>
	 */
	<T> List<T> getGroupbyList(int curPage, int pageSize,
			Set<Condition> conditions, LinkedHashSet<OrderBy> orderbys,
			LinkedHashMap<String, String> funtions, String... groupby);

	/**
	 * 分组总数量 By 原生SQL,需要输入数据库字段名称
	 * 
	 * @param conditions
	 *            条件
	 * @param groupby
	 *            分组的属性
	 * @return
	 */
	Long getTotalCountByGroupBy(Set<Condition> conditions, String... groupby);

	/**
	 * 根据属性和值得到对象
	 * 
	 * @param propertyName
	 *            属性
	 * @param value
	 *            对应的值
	 * @return
	 */
	POJO get(String propertyName, Serializable value, String... c);

	/**
	 * 支持各种复杂逻辑
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param sws
	 * @param so
	 * @param orderby
	 * @param props
	 * @return
	 */
	List<POJO> getListBySql(int curPage, int pageSize, SqlWhere[] sws,
			SqlOp so, LinkedHashMap<String, String> orderby, String... props);

	/**
	 * 支持各种复杂逻辑
	 * 
	 * @param so
	 * @param sws
	 * @return
	 */
	Long getCountBySql(SqlOp so, SqlWhere[] sws);

	/**
	 * 支持各种复杂逻辑
	 * 
	 * @param curPage
	 * @param pageSize
	 * @param sws
	 * @param so
	 * @param orderby
	 * @param props
	 * @return
	 */
	PageInfo<POJO> getPageinfoBySql(int curPage, int pageSize, SqlWhere[] sws,
			SqlOp so, LinkedHashMap<String, String> orderby, String... props);

}
