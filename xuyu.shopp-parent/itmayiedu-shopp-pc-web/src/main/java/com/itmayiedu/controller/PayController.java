package com.itmayiedu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itmayiedu.base.ResponseBase;
import com.itmayiedu.constants.Constants;
import com.itmayiedu.feign.PayServiceFegin;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
public class PayController {

	@Autowired
	private PayServiceFegin payServiceFeign;
	//使用token进行支付
	@RequestMapping("/aliPay")
	public void aliPay(String payToken,HttpServletResponse response) throws IOException {
		//设置页面展示格式为html格式，编码为utf-8
		response.setContentType("text/html;charset=utf-8");
		PrintWriter writer = response.getWriter();
		//1.参数验证
		if(StringUtils.isEmpty(payToken)) {
			return;
		}
		//2.调用支付服务接口 获取支付宝html元素
		ResponseBase payTokenResult = payServiceFeign.findPayToken(payToken);
		if(! payTokenResult.getRtnCode().equals(Constants.HTTP_RES_CODE_200)) {
			String msg=payTokenResult.getMsg();
			writer.print(msg);
			return;
		}
		//3.返回可以执行的html元素给客户端
		LinkedHashMap data = (LinkedHashMap) payTokenResult.getData();
		String payHtml = (String) data.get("payHtml");
		log.info("###PayController##payHtml:{}",payHtml);
		//4.页面上进行渲染
		writer.println(payHtml);
		writer.close();
	}
}
