package com.codigo.msregister.feignClient;

import com.codigo.msregister.aggregates.response.ResponseReniec;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "reniec-client", url = "https://api.apis.net.pe/v2/reniec/")
public interface ReniecClient {
    @GetMapping("/dni")
    ResponseReniec getReniecInfo(@RequestParam("numero") String numero,
                                 @RequestHeader("Authorization") String authorizationHeader);
}
