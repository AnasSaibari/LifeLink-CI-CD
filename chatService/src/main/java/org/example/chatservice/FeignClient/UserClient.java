package org.example.chatservice.FeignClient;

import org.example.chatservice.DTO.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:9091/api/users")
public interface UserClient {
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}

