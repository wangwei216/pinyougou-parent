package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		
		goods.getGoods().setAuditStatus("0");//状态：未审核
		goodsMapper.insert(goods.getGoods());//插入商品基本信息
		
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//将商品基本表的ID给商品扩展表
		goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展表数据
		
		if("1".equals(goods.getGoods().getIsEnableSpec())){
			for(TbItem item:   goods.getItemList()){
				//构建标题  SPU名称+ 规格选项值
				String title=goods.getGoods().getGoodsName();//SPU名称
				Map<String,Object> map=  JSON.parseObject(item.getSpec());
				for(String key:map.keySet()) {
					title+=" "+map.get(key);
				}
				item.setTitle(title);
				
				setItemValues(item,goods);
				
				itemMapper.insert(item);
			}
		}else{//没有启用规格			
			
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//标题
			item.setPrice(goods.getGoods().getPrice());//价格
			item.setNum(99999);//库存数量
			item.setStatus("1");//状态
			item.setIsDefault("1");//默认
			item.setSpec("{}");//规格
			
			setItemValues(item,goods);
			
			itemMapper.insert(item);
		}
		
		
		
	}
	
	private void setItemValues(TbItem item,Goods goods){
		//商品分类 
		item.setCategoryid(goods.getGoods().getCategory3Id());//三级分类ID
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//更新日期 
		
		item.setGoodsId(goods.getGoods().getId());//商品ID
		item.setSellerId(goods.getGoods().getSellerId());//商家ID
		
		//分类名称			
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		//商家名称(店铺名称)			
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		
		//图片
		List<Map> imageList = JSON.parseArray( goods.getGoodsDesc().getItemImages(), Map.class) ;
		if(imageList.size()>0){
			item.setImage( (String)imageList.get(0).get("url"));
		}
		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//这个是当修改完商品信息的组合表的时候，需要更新这个组合实体表，先从基本表开始
		goodsMapper.updateByPrimaryKey(goods.getGoods());  //更新商品基本表
		//更新商品的扩展表，其实也就是商品的描述表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		
	
		//然后是先删除原来的SKU的列表，然后再插入新的SKU列表
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(goods.getGoods().getId());
		//然后直接删除
		itemMapper.deleteByExample(example);
		//这里是插入新的SKU列表 
		goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展表数据
 		if("1".equals(goods.getGoods().getIsEnableSpec())){
			for(TbItem item:   goods.getItemList()){
				//构建标题  SPU名称+ 规格选项值
				String title=goods.getGoods().getGoodsName();//SPU名称
				Map<String,Object> map=  JSON.parseObject(item.getSpec());
				for(String key:map.keySet()) {
					title+=" "+map.get(key);
				}
				item.setTitle(title);
				
				setItemValues(item,goods);
				
				itemMapper.insert(item);
			}
		}else{//没有启用规格			
			
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//标题
			item.setPrice(goods.getGoods().getPrice());//价格
			item.setNum(99999);//库存数量
			item.setStatus("1");//状态
			item.setIsDefault("1");//默认
			item.setSpec("{}");//规格
			
			setItemValues(item,goods);
			
			itemMapper.insert(item);
		}
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		//先定义一个返回的组合实体类
		Goods goods = new Goods();
		//这个是通过主键去查询商品的基本信息表
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		//这个是查询商品信息的扩展表
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);
		
		//这里是构建一个查询对象
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		//构建查询条件，等于goods表的id的
		createCriteria.andGoodsIdEqualTo(id);
		//这里是读取SKU的列表信息
		List<TbItem> itemsList= itemMapper.selectByExample(example);
		goods.setItemList(itemsList);
		
		//这里是返回组合实体类对象
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
//				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				//因为有可能会出现两个卖家的ID是包含的关系
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
