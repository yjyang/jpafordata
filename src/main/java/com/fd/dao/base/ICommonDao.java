package com.fd.dao.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fd.dao.base.common.MultiPartInfo;
import com.fd.dao.base.common.PageInfo;
import com.fd.dao.base.em.MergeType;

/***
 * 多表通用查询
 * 
 * @author 符冬
 * 
 */
public interface ICommonDao extends Serializable {
	/***
	 * 多表链接分页查询
	 * <P>
	 * MultiPartInfo product1 = new MultiPartInfo(); product1.setColumns(
	 * "productDescription"); product1.setConditions(new Condition("sid",
	 * Operators.EQ, sid)); product1.setMidcolumn("brandId");
	 * product1.setTables("ProductInfo"); MultiPartInfo brand2 = new
	 * MultiPartInfo(); brand2.setColumns("brandName");
	 * brand2.setMidcolumn("sid"); brand2.setOrderbys(MyUtils.getMap("sid",
	 * "desc")); brand2.setConditions(new Condition("sid", Operators.EQ, 30l));
	 * brand2.setTables("brand"); List<MultiPartInfo> mllist = new
	 * ArrayList<MultiPartInfo>(); mllist.add(product1); mllist.add(brand2);
	 * PageInfo odata = productService.getMultiQuery().getMultipartPageinfo(
	 * mllist, 1, pageSize);
	 * 
	 * PageInfo<ProductDetailVo> data = MyUtils.convert2VO(odata,
	 * ProductDetailVo.class, "productDescription", "brandName");
	 * ProductDetailVo vo = data.getDataList().get(0);
	 * req.setAttribute("product", vo);
	 * System.out.println(vo.getProductDescription());
	 * System.out.println(vo.getBrandName());
	 * </p>
	 * 
	 * @param list
	 * @param curPage
	 * @param pageSize
	 * @return
	 */
	PageInfo getMultipartPageinfo(List<MultiPartInfo> list, int curPage,
			int pageSize);

	/***
	 * 多表链接查询数量
	 * 
	 * @param list
	 * @return
	 */
	Long getMultipartqueryCount(List<MultiPartInfo> list);

	/***
	 * 批量更新
	 * 
	 * <p>
	 * 调用例子： <br/>
	 * Book bk = new Book(); bk.setBookName("草莓了"); IBaseDao[] daos = { bookDao,
	 * consigneeDao, bookDao }; Consignee cg = consigneeDao.getById(1104l);
	 * cg.setProvince("草泥22马"); Object[] os = { bk, cg,
	 * bookDao.getReference(482l) }; clientcommonDao.mergeall(daos, os,
	 * MergeType.SAVE, MergeType.UPDATE, MergeType.DELTE);
	 * </p>
	 * 
	 * @param <POJO>
	 * @param daos
	 * @param pojos
	 */
	<POJO> void mergeBatch(IBaseDao<POJO>[] daos, POJO[] pojos,
			MergeType... types);

	/**
	 * 批量更新
	 * 
	 * @param updaos
	 *            键：更新类型，值：持久化对象集合
	 */
	void mergeBatch(Map<MergeType, List> updaos);

	/**
	 * 立即同步持久化上下文的托管对象到数据
	 */
	void flush();

	/**
	 * 清空持久化上下文所有受托管对象
	 */
	void clear();

}
