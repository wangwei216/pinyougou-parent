package com.pinyougou.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

/**认证类
 * @author user
 * 这个是专门用来动态从数据库中取数据进行和用户输入的账号和密码进行比对的
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	//添加一个set方法，这里需要使用配置的方式是通过dubbo把另外的一个微服务的service工程给注入进来
	private SellerService sellerService;
	
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//这个集合中装的是你的不同角色的权限分组，也就是构建一个角色列表
		List<GrantedAuthority> grantAuths = new ArrayList<GrantedAuthority>();
		//因为GrantedAuthority这个是一个接口，不能直接添加，所以需要new一个SimpleGrantedAuthority的构造器
		grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		//然后开始从数据库中去拿到数据
		TbSeller tbSeller = sellerService.findOne(username);
		//先判断是不是查询到了数据
		if (tbSeller!=null) {
			//然后还需要判断若果店铺的审核状态是不是通过的状态为“1”的才可以登录
			if (tbSeller.getStatus().equals("1")) {
				//表示可以访问的grantAuths
				return new User(username, tbSeller.getPassword(), grantAuths);
			}else {
				return null;
			}
		}else{
			return null;
		}
	}

}
