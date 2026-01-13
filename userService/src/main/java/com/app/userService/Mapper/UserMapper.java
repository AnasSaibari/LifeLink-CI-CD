package com.app.userService.Mapper;

import com.app.userService.Entity.User;
import com.app.userService.DTO.UserDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // ENTITY → DTO

    @Mapping(source = "villeId", target = "villeId")
    UserDTO toDto(User user);

    // DTO → ENTITY

    @Mapping(target = "id", ignore = true)
    User toEntity(UserDTO dto);

    // DTO -> ENTITY (UPDATE FUNCTION)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFromDto(UserDTO dto, @MappingTarget User user);


}


