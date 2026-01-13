package org.example.annonceservice.FeignClient;

import org.example.annonceservice.DTO.LocationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "location-service", url = "http://localhost:9092")
public interface LocationClient {

    @GetMapping("/location/{id}")
    LocationDTO getLocationById(@PathVariable("id") Long id);
}

