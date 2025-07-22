package org.example.authenticationservice.mapper;

import org.example.authenticationservice.dto.UserDto;
import org.example.authenticationservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = RoleMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class UserMapper {
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    public abstract UserDto mapTo(User user);
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapStringsToRoles")
    public abstract User mapTo(UserDto userDto);
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapStringsToRoles")
    public abstract void updateUserFromDto(@MappingTarget User user, UserDto userDto);
}
