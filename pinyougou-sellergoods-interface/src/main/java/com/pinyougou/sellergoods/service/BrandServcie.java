package com.pinyougou.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbBrand;

/**
 * @author user
 * 	品牌接口
 */
public interface BrandServcie {

	
	/**查询所有的品牌信息
	 * @return
	 */
	public List<TbBrand> findAll();
}
