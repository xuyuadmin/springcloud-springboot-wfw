package com.itmayiedu.api.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.config.AlipayConfig;
import com.itmayiedu.base.BaseApiService;
import com.itmayiedu.base.ResponseBase;
import com.itmayiedu.constants.Constants;
import com.itmayiedu.dao.PaymentInfoDao;
import com.itmayiedu.entity.PaymentInfo;
import com.itmayiedu.fegin.OrderServiceFeign;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CallBackServiceImpl extends BaseApiService implements CallBackService {
	@Autowired
	private PaymentInfoDao paymentInfoDao;
	@Autowired
	private OrderServiceFeign orderServiceFeign;
	@Override
	public ResponseBase synCallBack(@RequestParam Map<String, String> params) {
		// 1.日志记录
		log.info("#####支付宝同步通知 synCallBack####开始,params:{}", params);
		try {
			// 2.验签
			boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key,
					AlipayConfig.charset, AlipayConfig.sign_type); //
			log.info("#####支付宝同步通知signVerified:{}######", signVerified);
			if (!signVerified) {
				return setResultError("验签失败！");
			}
			String outTradeNo = params.get("out_trade_no");
			String tradeNo = params.get("trade_no");
			String totalAmount = params.get("total_amount");
			JSONObject data = new JSONObject();
			data.put("outTradeNo", outTradeNo);
			data.put("tradeNo", tradeNo);
			data.put("totalAmount", totalAmount);
			return setResultSuccess(data);
		} catch (Exception e) {
			log.error("支付宝同步通知出现异常,ERROR:", e);
			return setResultError("系统错误!");
		} finally {
			log.info("#####支付宝同步通知 synCallBack####结束,params:{}", params);
		}

	}

	@Override
	public synchronized String asynCallBack(Map<String, String> params) {
		// 1.日志记录
		log.info("#####支付宝异步通知 synCallBack####开始,params:{}", params);
		try {
			// 2.验签
			boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key,
					AlipayConfig.charset, AlipayConfig.sign_type); //
			log.info("#####支付宝异步通知signVerified:{}######", signVerified);
			if (!signVerified) {
				return Constants.PAY_FAIL;
			}
			//商户订单号
			String outTradeNo = params.get("out_trade_no");
			//集群分布式锁，不是就synchronized锁
			//修改支付表状态
			PaymentInfo paymentInfo = paymentInfoDao.getByOrderIdPayInfo(outTradeNo);
			//判断交易信息在数据库是否存在
			if(paymentInfo==null) {
				return Constants.PAY_FAIL;
			}
			Integer state = paymentInfo.getState();
			//放在重试机制，并行执行还是会执行两遍，支付宝间隔执行，则不会有并行执行，除非代码延迟等待
			if(state==1) {
				return Constants.PAY_SUCCESS;
			}
			//支付宝交易号
			String tradeNo = params.get("trade_no");
			//金额
			String totalAmount = params.get("total_amount");
			//判断实际付款金额与商品金额是否一致，不一致修改为异常订单
			//订单状态标识为已经支付
			paymentInfo.setState(1);
			paymentInfo.setPayMessage(params.toString());
			//支付宝交易号
			paymentInfo.setPlatformorderId(tradeNo);
			
			//########1.手动开启事务begin########
			//更新数据库
			Integer updateResult = paymentInfoDao.updatePayInfo(paymentInfo);
			if(updateResult<=0) {
				return Constants.PAY_FAIL;
			}
			//加积分，调用积分接口
			//调用订单接口通知支付状态
			ResponseBase orderResult = orderServiceFeign.updateOrder(1l, tradeNo, outTradeNo);
			if(!orderResult.getRtnCode().equals(Constants.HTTP_RES_CODE_200)) {
				//#########2.手动回滚事务 rollback###########
				return Constants.PAY_FAIL;
			}
			//###########3.手动提交事务commit#############
			return Constants.PAY_SUCCESS;
		} catch (Exception e) {
			log.error("支付宝异步通知出现异常,ERROR:", e);
			//#########4.手动回滚事务 rollback###########
			return Constants.PAY_FAIL;
		} finally {
			log.info("#####支付宝异步通知 synCallBack####结束,params:{}", params);
		}
	}

}
