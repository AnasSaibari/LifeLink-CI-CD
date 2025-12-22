package com.app.donationService.FeignClient;

import com.app.donationService.DTO.DonationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:9091/api/users")
public interface UserClient {

    @GetMapping("{id}")
    DonationDTO.userInfo getUserById(@PathVariable Long id);
}
