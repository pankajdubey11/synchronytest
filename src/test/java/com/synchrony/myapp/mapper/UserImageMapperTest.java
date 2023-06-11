package com.synchrony.myapp.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.synchrony.myapp.dao.model.User;
import com.synchrony.myapp.dao.model.UserImage;
import com.synchrony.myapp.mapper.UserImageMapper;
import com.synchrony.myapp.model.UserDTO;
import com.synchrony.myapp.model.UserImageDTO;

@RunWith(MockitoJUnitRunner.class)
public class UserImageMapperTest {

	private static final Integer USER_IMAGE_ID = 1;
    private static final Integer USER_ID = 100;

    @Test
    public void entityToDto() {
        //Given
        final UserImage userimage = new UserImage();
        userimage.setUserId(USER_ID);
        userimage.setUserImageId(USER_IMAGE_ID);

        //when
        final UserImageDTO userImageDTO = UserImageMapper.userImageMapper.entityToDto(userimage);

        //then
        assertNotNull("User DTO is null", userImageDTO);
        assertEquals("User Id not matching", USER_ID, userImageDTO.getUserId());

    }
    
    @Test
    public void entityToDto_NullTest() {
        //when
    	final UserImageDTO userImageDTO = UserImageMapper.userImageMapper.entityToDto(null);

        //then
        assertNull("UserImage DTO is not null", userImageDTO);

    }
    
    @Test
    public void dtoToEntity() {
        //Given
        final UserImageDTO userImageDTO = new UserImageDTO();
        userImageDTO.setUserId(USER_ID);

        //when
        final UserImage userImage = UserImageMapper.userImageMapper.dtoToEntity(userImageDTO);

        //then
        assertNotNull("UserImage Entity is null", userImage);
        assertEquals("User Id not matching", USER_ID, userImage.getUserId());
    }

    @Test
    public void dtoToEntity_NullTest() {
        //when
    	final UserImage userImage = UserImageMapper.userImageMapper.dtoToEntity(null);

        //then
        assertNull("UserImage Entity is not null", userImage);
    }

	
}
