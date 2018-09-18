package com.itmayiedu.controller;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itmayiedu.base.ResponseBase;
import com.itmayiedu.constants.Constants;
import com.itmayiedu.feign.MemberServiceFegin;
import com.itmayiedu.utils.CookieUtil;

@Controller
public class IndexController {

	private static final String INDEX="index";
	@Autowired
	private MemberServiceFegin memberServiceFegin;
	//主页
	@RequestMapping(value="/",method=RequestMethod.GET)
	public String index(HttpServletRequest request) {
		//1.从Cookie中获取token信息
		String token = CookieUtil.getUid(request, Constants.COOKIE_MEMBER_TOKEN);
		//2.如果cookie存在token 调用会员服务接口，使用token查询用户信息
		if(!StringUtils.isEmpty(token)) {
			ResponseBase responseBase = memberServiceFegin.findByToken(token);
			if(responseBase.getRtnCode().equals(Constants.HTTP_RES_CODE_200)) {
				LinkedHashMap userData=(LinkedHashMap) responseBase.getData();
				String username = (String) userData.get("username");
				//3.把username转发到页面进行展示
				request.setAttribute("username", username);
			}
		}
		return INDEX;
		
	}
}
