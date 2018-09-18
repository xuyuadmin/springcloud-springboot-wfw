package com.itmayiedu.controller;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itmayiedu.base.ResponseBase;
import com.itmayiedu.constants.Constants;
import com.itmayiedu.entity.UserEntity;
import com.itmayiedu.feign.MemberServiceFegin;
import com.itmayiedu.utils.CookieUtil;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.oauth.Oauth;
@SuppressWarnings("all")
@Controller
public class LoginController {
	@Autowired
	private MemberServiceFegin memberServiceFegin;

	private static final String LOGIN="login";
	private static final String ERROR="error";
	private static final String INDEX="redirect:/";
	private static final String QQRELATION="qqrelation";
	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String loginGet() {
		return LOGIN;
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String loginPost(UserEntity userEntity,HttpServletRequest request,HttpServletResponse response) {
		//1.验证参数
		//2.调用登录接口，获取token信息
		ResponseBase loginBase = memberServiceFegin.login(userEntity);
		if(loginBase.getRtnCode().equals(Constants.HTTP_RES_CODE_500)) {
			request.setAttribute("error", "账号或者密码错误！");
			return LOGIN;
		}
		//3.将token信息存放在cookie里面
		LinkedHashMap loginData=(LinkedHashMap) loginBase.getData();
		String memberToken = (String) loginData.get("memberToken");
		if(StringUtils.isEmpty(memberToken)) {
			request.setAttribute("error", "会话已经失效！");
			return LOGIN;
		}
		setCookie(memberToken, response);
		return INDEX;
	}
	//生成qq授权链接
	@RequestMapping("/locaQQLogin")
	public String locaQQLogin(HttpServletRequest request) throws Exception {
		String authorizeURL = new Oauth().getAuthorizeURL(request);
		//重定向到授权链接
		return "redirect:"+authorizeURL;
	}
	@RequestMapping("/qqLoginCallback")
	public String qqLoginCallback(HttpServletRequest request,HttpSession httpSession,HttpServletResponse response) throws Exception {
		//1.获取授权码Code
		//2.使用授权码code获取accessToken
		AccessToken accessTokenOj = new Oauth().getAccessTokenByRequest(request);
		if(accessTokenOj==null) {
			request.setAttribute("error", "qq授权失败");
			return ERROR;
		}
		//获取accessToken
		String accessToken = accessTokenOj.getAccessToken();
		if(accessToken==null) {
			request.setAttribute("error", "accessToken为空！");
			return ERROR;
		}
		//3.使用accessToken获取openid
		OpenID openidOj = new OpenID(accessToken);
		if(openidOj==null) {
			request.setAttribute("error", "openidOj为空！");
			return ERROR;
		}
		String userOpenId = openidOj.getUserOpenID();
		//4.调用会员服务接口 使用userOpenId 查找是否已经关联账号
		ResponseBase openUserBase = memberServiceFegin.findByOpenIdUser(userOpenId);
		if(openUserBase.getRtnCode().equals(Constants.HTTP_RES_CODE_201)) {
			//把openid带到服务器
			httpSession.setAttribute("qqOpenid", userOpenId);
			//5.如果没有关联账号，跳转到QQ关联账号页面
			return QQRELATION;
		}
		//6.已经绑定了账号,自动登录，将用户token信息存在cookie中
		LinkedHashMap  dataTokenMap = (LinkedHashMap) openUserBase.getData();
		String memberToken = (String) dataTokenMap.get("memberToken");
		setCookie(memberToken, response);
		return INDEX;
	}
	//qq授权关联页面 已有账号
	@RequestMapping(value="/qqRelation",method=RequestMethod.POST)
	public String qqRelation(UserEntity userEntity,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession) {
		//1.获取openid
		String qqOpenid = (String) httpSession.getAttribute("qqOpenid");
		if(StringUtils.isEmpty(qqOpenid)) {
			request.setAttribute("error", "没有获取到openid!");
			return ERROR;
		}
		//2.调用登录接口，获取token信息
		userEntity.setOpenid(qqOpenid);
		ResponseBase loginBase = memberServiceFegin.qqLogin(userEntity);
		if(loginBase.getRtnCode().equals(Constants.HTTP_RES_CODE_500)) {
			request.setAttribute("error", "账号或者密码错误！");
			return LOGIN;
		}
		//3.将token信息存放在cookie里面
		LinkedHashMap loginData=(LinkedHashMap) loginBase.getData();
		String memberToken = (String) loginData.get("memberToken");
		if(StringUtils.isEmpty(memberToken)) {
			request.setAttribute("error", "会话已经失效！");
			return LOGIN;
		}
		setCookie(memberToken, response);
		return INDEX;
	}
	public void setCookie(String memberToken,HttpServletResponse response) {
		CookieUtil.addCookie(response, Constants.COOKIE_MEMBER_TOKEN, memberToken, Constants.COOKIE_TOKEN_MEMBER_TIME);
	}
	
}
