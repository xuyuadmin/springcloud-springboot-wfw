package com.itmayiedu.constants;

public interface Constants {


	//响应请求成功
	String HTTP_RES_CODE_200_VALUE = "success";
	//响应请求失败
	String HTTP_RES_CODE_500_VALUE = "fial";
	//响应成功code
	Integer HTTP_RES_CODE_200 = 200;
	//响应失败code
	Integer HTTP_RES_CODE_500 = 500;
	//发送邮件
	String MSG_EMAIL="email";
	//会员token
	String TOKEN_MEMBER="TOKEN_MEMBER";
	//支付token
	String TOKEN_PAY="TOKEN_PAY";
	//支付成功
	String PAY_SUCCESS="pay_success";
	//支付失败
	String PAY_FAIL="pay_fail";
	//token有效期:90天
	Long TOKEN_MEMBER_TIME=(long) (60*60*24*90);
	//cookie有效期
	int COOKIE_TOKEN_MEMBER_TIME=60*60*24*90;
	//支付token有效期
	Long PAY_TOKEN_MEMBER_TIME=(long)(60*15);
	//cookie会员token名称
	String COOKIE_MEMBER_TOKEN="cookie_member_token";
	
	
	//未关联qq账号
	Integer HTTP_RES_CODE_201=201;
}
