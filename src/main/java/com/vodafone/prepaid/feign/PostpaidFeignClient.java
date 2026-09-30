package com.vodafone.prepaid.feign;

import com.vodafone.prepaid.dto.ApiResponse;
import com.vodafone.prepaid.dto.PostpaidAccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "postpaid-service", url = "${postpaid.service.url:http://localhost:8082}")
public interface PostpaidFeignClient {

    @GetMapping("/api/postpaid/account/{msisdn}")
    ApiResponse<PostpaidAccountDto> getPostpaidAccountByMsisdn(@PathVariable("msisdn") String msisdn);
}
