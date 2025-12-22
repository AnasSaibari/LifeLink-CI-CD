package com.app.userService.FeignClient;

import com.app.userService.DTO.LocationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "location-service", url = "http://localhost:9092")
public interface LocationClient {

    @GetMapping("/location/{id}")
    LocationDTO getLocationById(@PathVariable("id") Long id);
}