package com.pyg.content.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.content.service.ContentService;
import com.pyg.mapper.TbContentMapper;
import com.pyg.pojo.PageResult;
import com.pyg.pojo.TbContent;
import com.pyg.pojo.TbContentExample;
import com.pyg.pojo.TbContentExample.Criteria;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	// 注入redistemplate
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		// 清除缓存
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content) {
		// 清空缓存
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		redisTemplate.boundHashOps("content").delete(categoryId);
		contentMapper.updateByPrimaryKey(content);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id) {
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();

		if (content != null) {
			if (content.getTitle() != null && content.getTitle().length() > 0) {
				criteria.andTitleLike("%" + content.getTitle() + "%");
			}
			if (content.getUrl() != null && content.getUrl().length() > 0) {
				criteria.andUrlLike("%" + content.getUrl() + "%");
			}
			if (content.getPic() != null && content.getPic().length() > 0) {
				criteria.andPicLike("%" + content.getPic() + "%");
			}
			if (content.getStatus() != null && content.getStatus().length() > 0) {
				criteria.andStatusLike("%" + content.getStatus() + "%");
			}

		}

		Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	// 查询指定广告分类下的广告
	public List<TbContent> findAdByCategoryId(Long categoryId) {
		// TODO Auto-generated method stub

		// 先查询redis
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		if (contentList == null) {
			// 从数据库读取后放入缓存里
			// 查询外键关联并且状态是1 的广告
			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			criteria.andStatusEqualTo("1");
			example.setOrderByClause("sort_order");// 排序
			contentList = contentMapper.selectByExample(example);
			// 将查询的值赋值给redis
			redisTemplate.boundHashOps("content").put(categoryId, contentList);

		} else {
			System.out.println("从缓存redis读取数据");
		}
		return contentList;
	}

}
