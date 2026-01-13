package com.app.notificationService.FeignClient;

import com.app.notificationService.DTO.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:9091/api/users")
public interface UserClient {

    @GetMapping("/roleU")
    List<UserDTO> getAllUsersWithRole();

}