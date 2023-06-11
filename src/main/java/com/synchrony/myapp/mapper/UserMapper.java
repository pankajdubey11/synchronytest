package com.synchrony.myapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.synchrony.myapp.dao.model.User;
import com.synchrony.myapp.model.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    UserDTO entityToDto(User user);

    User dtoToEntity(UserDTO userDTO);
	
}
