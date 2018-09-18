package com.itmayiedu.mq;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.itmayiedu.adapter.MessageAdapter;
import com.itmayiedu.constants.Constants;
import com.itmayiedu.email.service.EmailService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConsumerDistribute {
	
	private MessageAdapter messageAdapter;

	@Autowired
	private EmailService emailService;
	// 监听消息
	@JmsListener(destination = "messages_queue")
	public void distribute(String json) {
		log.info("####消息服务平台调用接收短信消息内容:{}#####", json);
		if (StringUtils.isEmpty(json)) {
			return;
		}
		JSONObject rootJSON = new JSONObject().parseObject(json);
		JSONObject header = rootJSON.getJSONObject("header");
		String interfaceType = header.getString("interfaceType");

		if (StringUtils.isEmpty(interfaceType)) {
			return;
		}
		// 判断接口类型是否为发邮件
		if (interfaceType.equals(Constants.MSG_EMAIL)) {
			messageAdapter = emailService;
		}
		if (messageAdapter == null) {
			return;
		}
		JSONObject contentJson = rootJSON.getJSONObject("content");
		messageAdapter.sendMsg(contentJson);

	}

}
