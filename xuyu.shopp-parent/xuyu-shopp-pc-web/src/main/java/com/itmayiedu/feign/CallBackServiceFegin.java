package com.itmayiedu.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

import com.itmayiedu.api.service.CallBackService;

@FeignClient("pay")
@Component
public interface CallBackServiceFegin extends CallBackService {

}
