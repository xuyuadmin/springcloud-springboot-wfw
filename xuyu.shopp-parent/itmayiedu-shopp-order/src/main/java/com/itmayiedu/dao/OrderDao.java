package com.itmayiedu.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestParam;

import com.itmayiedu.base.ResponseBase;

@Mapper
public interface OrderDao {

	@Update("update order_info set isPay=#{isPay} ,payId=#{aliPayId} where orderNumber=#{orderNumber};")
	public int updateOrder
	(@RequestParam("payState") Long payState,
			@RequestParam("payId")String payId,@RequestParam("orderNumber")String orderNumber);
}
