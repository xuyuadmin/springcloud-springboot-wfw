package com.itmayiedu.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.itmayiedu.base.TextMessage;
import com.itmayiedu.utils.CheckUtil;
import com.itmayiedu.utils.HttpClientUtil;
import com.itmayiedu.utils.XmlUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class DispatCherController {
	/**
	 * 参数 signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
	 * timestamp 时间戳 nonce 随机数 echostr 随机字符串
	 * 
	 * 描述:服务器验证接口的地址： 将token、timestamp、nonce三个参数进行字典序排序 2）将三个参数字符串拼接成一个字符串进行sha1加密
	 * 3） 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
	 * 
	 * @return
	 */
	private static final String REQUESTURL = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=";

	@RequestMapping(value = "/dispatCher", method = RequestMethod.GET)
	public String dispatCherGet(String signature, String timestamp, String nonce, String echostr) {
		// 1.验证参数
		boolean checkSignature = CheckUtil.checkSignature(signature, timestamp, nonce);
		// 2.参数验证成功后，返回随机数
		if (!checkSignature) {
			return null;
		}
		return echostr;

	}

	// 微信动作推送
	@RequestMapping(value = "/dispatCher", method = RequestMethod.POST)
	public void dispatCherPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		// 1.将xml转换为map格式
		Map<String, String> resultMap = XmlUtils.parseXml(request);
		log.info("##收到微信消息##resultMap:" + resultMap.toString());
		// 2.判断消息类型
		String msgType = resultMap.get("MsgType");
		// 3.如果是文本类型返回结果给微信服务器端
		PrintWriter writer = response.getWriter();
		switch (msgType) {
		case "text":
			// 开发者微信公众号
			String toUserName = resultMap.get("ToUserName");
			// 发送方帐号（一个OpenID）
			String fromUserName = resultMap.get("FromUserName");
			// 文本消息内容
			String content = resultMap.get("Content");
			String resultJson = HttpClientUtil.doGet(REQUESTURL + content);
			JSONObject jsonObject = JSONObject.parseObject(resultJson);
			Integer resultCode = jsonObject.getInteger("result");
			String msg = null;
			if(resultCode==0) {
				String resultContent=jsonObject.getString("content");
			}else {
				msg=setText("我现在有点忙，稍后回复您！", toUserName,fromUserName);
			}
			
			/*
			 * if(content.equals("蚂蚁课堂")) { //返回蚂蚁课堂相关信息
			 * msg=setText("蚂蚁课堂官方网站：www.itmayiedu.com", toUserName,fromUserName); }else
			 * if(content.equals("灰灰")) { msg=setText("不错哦！", toUserName,fromUserName);
			 * }else if(content.equals("小宝")) { msg=setText("不错哦！",
			 * toUserName,fromUserName); }else if(content.equals("婷婷")) {
			 * msg=setText("马上结婚了！", toUserName,fromUserName); } else {
			 * msg=setText("很抱歉，未匹配上关键字！", toUserName,fromUserName); }
			 * log.info("##给微信发送消息###msg:"+msg);
			 */
			// 发送xml格式消息
			writer.println(msg);
			break;

		default:
			break;
		}
		writer.close();

	}

	public String setText(String content, String fromUserName, String ToUserName) {
		TextMessage textMessage = new TextMessage();
		textMessage.setContent(content);
		textMessage.setMsgType("text");
		textMessage.setCreateTime(new Date().getTime());
		textMessage.setFromUserName(fromUserName);
		textMessage.setToUserName(ToUserName);
		// 将实体类转化为xml格式
		String msg = XmlUtils.messageToXml(textMessage);
		return msg;
	}
}
