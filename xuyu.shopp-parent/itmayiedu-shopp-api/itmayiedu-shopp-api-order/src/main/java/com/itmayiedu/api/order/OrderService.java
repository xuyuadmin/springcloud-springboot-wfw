package com.itmayiedu.api.order;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itmayiedu.base.ResponseBase;

@RequestMapping("/order")
public interface OrderService {

	@RequestMapping("/updateOrderIdInfo")
	public ResponseBase updateOrder
	(@RequestParam("payState") Long payState,
			@RequestParam("payId")String payId,@RequestParam("orderNumber")String orderNumber);

}
