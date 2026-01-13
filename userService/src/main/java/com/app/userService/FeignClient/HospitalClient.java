package com.app.userService.FeignClient;

import com.app.userService.DTO.HospitalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hospital-service", url = "http://localhost:9093/hospital")
public interface HospitalClient {
    @GetMapping("/{id}")
    public HospitalDTO getHospitalById(@PathVariable("id") long id);
}
