package com.pyg.manager.service;

import java.util.List;

import com.pyg.pojo.PageResult;
import com.pyg.pojo.TbGoods;
import com.pyg.utils.PygResult;
import com.pyg.vo.Goods;

/**
 * 服务层接口
 * 
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	public List<TbGoods> findAll();

	/**
	 * 返回分页列表
	 * 
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);

	/**
	 * 增加
	 */
	public void add(TbGoods goods);

	/**
	 * 修改
	 */
	public void update(TbGoods goods);

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	public TbGoods findOne(Long id);

	/**
	 * 批量删除
	 * 
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * 
	 * @param pageNum
	 *            当前页 码
	 * @param pageSize
	 *            每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	// 插入商品信息(三个表)
	public PygResult insert(Goods goods);

	// 逻辑删除商品
	public PygResult deleNotTrue(Long[] ids);

	// 修改商品是够审核通过
	public PygResult updateStatus(Long[] ids, String status);

}
