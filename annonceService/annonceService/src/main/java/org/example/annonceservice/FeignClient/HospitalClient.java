package org.example.annonceservice.FeignClient;

import org.example.annonceservice.DTO.HospitalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "hospital-service", url = "http://localhost:9093/hospital")
public interface HospitalClient {
    @GetMapping("/{id}")
    public HospitalDTO getHospitalById(@PathVariable("id") long id);
}