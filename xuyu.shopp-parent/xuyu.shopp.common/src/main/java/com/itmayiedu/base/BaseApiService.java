package com.itmayiedu.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itmayiedu.constants.Constants;

@Component
public class BaseApiService {
	//redis单例，设置全局，放在父类中让子类引用
	@Autowired
	 protected BaseRedisService baseRedisService;
	//返回失败，可传msg
	public  ResponseBase setResultError(Integer code,String msg) {
		return setResult(code,msg,null);
		
	}
	//返回失败，可传msg
	public  ResponseBase setResultError(String msg) {
		return setResult(Constants.HTTP_RES_CODE_500,msg,null);
		
	}
	//返回成功，但可以传data值
	public  ResponseBase setResultSuccess(Object data) {
		return setResult(Constants.HTTP_RES_CODE_200,Constants.HTTP_RES_CODE_200_VALUE,data);
		
	}
	//返回成功，但没有data值
	public  ResponseBase setResultSuccess() {
		return setResult(Constants.HTTP_RES_CODE_200,Constants.HTTP_RES_CODE_200_VALUE,null);
		
	}
	//返回成功，可以传msg
	public  ResponseBase setResultSuccess(String msg) {
		return setResult(Constants.HTTP_RES_CODE_200,msg,null);
		
	}
	//通用封装
	public ResponseBase setResult(Integer code,String msg,Object data) {
		return new ResponseBase(code, msg, data);
		
	}
}
