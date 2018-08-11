package com.pinyougou.manager.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandServcie;

@Controller
@ResponseBody
@RequestMapping("/brand")
public class BrandController {

	
	@Reference
	private BrandServcie brandServcie;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll() {
		List<TbBrand> findAll = brandServcie.findAll();
		return findAll;
	}
	
}
