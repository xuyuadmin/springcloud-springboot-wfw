package com.itmayiedu.api.service;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itmayiedu.base.ResponseBase;

@RequestMapping("/callBack")
public interface CallBackService {

	//同步回调通知
	public ResponseBase synCallBack(@RequestParam Map<String,String> params);
	
	//异步通知
	public String asynCallBack(@RequestParam Map<String,String> params);
}
