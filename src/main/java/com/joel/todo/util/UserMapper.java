package com.joel.todo.util;

import com.joel.todo.dto.CreateUserEntityDto;
import com.joel.todo.dto.UpdateUserEntityDto;
import com.joel.todo.dto.UserEntityResponseDto;
import com.joel.todo.dto.UserEntityResponseWithTokenDto;
import com.joel.todo.model.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {


    @Mapping(target = "authorities", ignore = true)
    void mapCreateUserEntityDtoToUserEntity(CreateUserEntityDto createUserEntityDto, @MappingTarget UserEntity userEntity);

    void mapUserEntityToUserEntityResponseDto(UserEntity userEntity, @MappingTarget UserEntityResponseDto userEntityResponseDto);

    @Mapping(target = "authorities", ignore = true)
    void mapUpdateUserEntityDtoToUserEntity(UpdateUserEntityDto updateUserEntityDto, @MappingTarget UserEntity userEntity);

    @Mapping(target = "authorities", ignore = true)
    void mapUserEntityPartialToUserEntity(UserEntity userEntityPartial, @MappingTarget UserEntity userEntity);

    void mapUserEntityToUserEntityResponseWithToken(UserEntity userEntity, @MappingTarget UserEntityResponseWithTokenDto userEntityResponseWithTokenDto);

}
