package com.synchrony.myapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.synchrony.myapp.dao.model.UserImage;
import com.synchrony.myapp.model.UserImageDTO;

@Mapper(componentModel = "spring")
public interface UserImageMapper {

	UserImageMapper userImageMapper = Mappers.getMapper(UserImageMapper.class);

    UserImageDTO entityToDto(UserImage userImage);

    UserImage dtoToEntity(UserImageDTO userImageDTO);
	
}
