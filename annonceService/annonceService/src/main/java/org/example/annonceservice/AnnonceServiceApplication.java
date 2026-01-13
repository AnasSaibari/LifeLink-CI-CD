package org.example.annonceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.example.annonceservice.FeignClient")
public class AnnonceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnonceServiceApplication.class, args);
    }

}
