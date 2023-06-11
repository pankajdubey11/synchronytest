package com.synchrony.myapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.synchrony.myapp.dao.model.User;
import com.synchrony.myapp.dao.model.UserImage;
import com.synchrony.myapp.dao.repository.UserImageRepository;
import com.synchrony.myapp.dao.repository.UserRepository;
import com.synchrony.myapp.exception.ImageProcessingException;
import com.synchrony.myapp.mapper.UserImageMapper;
import com.synchrony.myapp.mapper.UserMapper;
import com.synchrony.myapp.model.UserDTO;
import com.synchrony.myapp.model.UserImageDTO;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserImageRepository userImageRepository;

    @Mock
    private ImgurService imgurService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserImageMapper userImageMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUser() {
        UserDTO reqUserDTO = new UserDTO();
        User user = new User();
        UserDTO resUserDTO = new UserDTO();

        when(userMapper.dtoToEntity(reqUserDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.entityToDto(user)).thenReturn(resUserDTO);

        UserDTO savedUserDTO = userService.saveUser(reqUserDTO);

        assertNotNull(savedUserDTO);
        assertEquals(resUserDTO, savedUserDTO);

        verify(userMapper).dtoToEntity(reqUserDTO);
        verify(userRepository).save(user);
        verify(userMapper).entityToDto(user);
    }

    @Test
    public void testGetUserByUserName_ExistingUser() {
        String userName = "john_doe";
        User user = new User();
        UserDTO expectedUserDTO = new UserDTO();

        when(userRepository.findByUserName(userName)).thenReturn(user);
        when(userMapper.entityToDto(user)).thenReturn(expectedUserDTO);

        UserDTO actualUserDTO = userService.getUserByUserName(userName);

        assertNotNull(actualUserDTO);
        assertEquals(expectedUserDTO, actualUserDTO);

        verify(userRepository).findByUserName(userName);
        verify(userMapper).entityToDto(user);
    }

    @Test
    public void testGetUserByUserName_NonExistingUser() {
        String userName = "john_doe";

        when(userRepository.findByUserName(userName)).thenReturn(null);

        UserDTO userDTO = userService.getUserByUserName(userName);

        assertNull(userDTO);

        verify(userRepository).findByUserName(userName);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void testUploadImage_SuccessfulUpload() throws ImageProcessingException {
        String data = "image_data";
        String fileName = "image.jpg";
        Integer userId = 1;
        String responseBody = "response_body";
        UserImage userImage = new UserImage();
        UserImageDTO userImageDTO = new UserImageDTO();

        when(imgurService.upload(data)).thenReturn(responseBody);
        when(userImageRepository.save(userImage)).thenReturn(userImage);
        when(userImageMapper.entityToDto(userImage)).thenReturn(userImageDTO);

        UserImageDTO uploadedImageDTO = userService.uploadImage(data, fileName, userId);

        assertNotNull(uploadedImageDTO);
        assertEquals(userImageDTO, uploadedImageDTO);

        verify(imgurService).upload(data);
        verify(userImageRepository).save(userImage);
        verify(userImageMapper).entityToDto(userImage);
    }

    @Test
    public void testUploadImage_UploadFailure() {
        String data = "image_data";
        String fileName = "image.jpg";
        Integer userId = 1;
        Exception exception = new RuntimeException("Upload failed");

        when(imgurService.upload(data)).thenThrow(exception);

        assertThrows(ImageProcessingException.class, () -> {
            userService.uploadImage(data, fileName, userId);
        });

        verify(imgurService).upload(data);
        verifyNoInteractions(userImageRepository, userImageMapper);
    }

   


    @Test
    public void testDeleteImage_DeletionFailure() {
        String deleteHash = "delete_hash";
        Integer userId = 1;
        Exception exception = new RuntimeException("Deletion failed");

        when(imgurService.delete(deleteHash)).thenThrow(exception);

        assertThrows(ImageProcessingException.class, () -> {
            userService.deleteImage(deleteHash, userId);
        });

        verify(imgurService).delete(deleteHash);
        verifyNoInteractions(userImageRepository);
    }

    // Add more test cases for other methods in UserService

}
