package com.pinyougou.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author user
 * 登录控制器，用来更新登录人的信息展示
 */
@Controller
@ResponseBody
@RequestMapping("/login")
public class LoginController {
	
	@RequestMapping("/name")
	public Map name(){
		
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map map = new HashMap<>();
		map.put("loginName", name);
		return map;
		
	}
	

}
