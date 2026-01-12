package org.example.chatservice.FeignClient;

import org.example.chatservice.DTO.HospitalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hospital-service", url = "http://localhost:9093/hospital")
public interface HospitalClient {
    @GetMapping("/{id}")
    HospitalDTO getHospitalById(@PathVariable("id") Long id);
}

