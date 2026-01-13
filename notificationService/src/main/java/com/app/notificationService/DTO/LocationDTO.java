package com.app.notificationService.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@lombok.NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationDTO{
    Long id;
    String ville;
}

