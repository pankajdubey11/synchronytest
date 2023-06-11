package com.synchrony.myapp.controller;

import com.synchrony.myapp.model.ImageRequestDTO;
import com.synchrony.myapp.model.UserDTO;
import com.synchrony.myapp.model.UserImageDTO;
import com.synchrony.myapp.model.UserResponseDTO;
import com.synchrony.myapp.service.UserService;
import com.synchrony.myapp.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistration() throws BindException {
        // Arrange
        UserDTO userDTO = new UserDTO();
        BindingResult errors = mock(BindingResult.class);

        when(errors.hasErrors()).thenReturn(false);
        when(userService.saveUser(userDTO)).thenReturn(userDTO);

        // Act
        UserDTO result = userController.registration(userDTO, errors);

        // Assert
        verify(userValidator).validate(userDTO, errors);
        verify(userService).saveUser(userDTO);
        assertEquals(userDTO, result);
    }

    @Test
    void testRegistrationWithValidationErrors() throws BindException {
        // Arrange
        UserDTO userDTO = new UserDTO();
        BindingResult errors = mock(BindingResult.class);

        when(errors.hasErrors()).thenReturn(true);

        // Act & Assert
        assertThrows(BindException.class, () -> userController.registration(userDTO, errors));

        verify(userValidator).validate(userDTO, errors);
        verifyNoInteractions(userService);
    }

    @Test
    void testGetUserDetailsByUserName() {
        // Arrange
        String userName = "testUser";
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        when(userService.getUserDetailsByUserName(userName)).thenReturn(userResponseDTO);

        // Act
        UserResponseDTO result = userController.getUserDetailsByUserName(userName);

        // Assert
        verify(userValidator).validateUser(userName);
        verify(userService).getUserDetailsByUserName(userName);
        assertEquals(userResponseDTO, result);
    }

    @Test
    void testUploadImage() throws IOException {
        // Arrange
        String userName = "testUser";
        String password = "testPassword";
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        UserDTO userDTO = new UserDTO();
        byte[] bytes = file.getBytes();
        String data = Base64.getEncoder().encodeToString(bytes);
        UserImageDTO userImageDTO = new UserImageDTO();

        when(userValidator.validateUserAndPassword(userName, password)).thenReturn(userDTO);
        when(userService.uploadImage(data, file.getOriginalFilename(), userDTO.getUserId())).thenReturn(userImageDTO);

        // Act
        UserImageDTO result = userController.uploadImage(file, userName, password);

        // Assert
        verify(userValidator).validateUserAndPassword(userName, password);
        verify(userService).uploadImage(data, file.getOriginalFilename(), userDTO.getUserId());
        assertEquals(userImageDTO, result);
    }

    @Test
    void testDelete() throws IOException {
        // Arrange
        ImageRequestDTO requestDTO = new ImageRequestDTO();
        UserDTO userDTO = new UserDTO();
        boolean isDeleted = true;

        when(userValidator.validateUserAndPassword(requestDTO.getUserName(), requestDTO.getPassword())).thenReturn(userDTO);
        when(userService.deleteImage(requestDTO.getHash(), userDTO.getUserId())).thenReturn(isDeleted);

        // Act
        Boolean result = userController.delete(requestDTO);

        // Assert
        verify(userValidator).validateUserAndPassword(requestDTO.getUserName(), requestDTO.getPassword());
        verify(userService).deleteImage(requestDTO.getHash(), userDTO.getUserId());
        assertEquals(isDeleted, result);
    }

    @Test
    void testGetImageByImageId() {
        // Arrange
        ImageRequestDTO requestDTO = new ImageRequestDTO();
        String imageResponse = "test image response";

        when(userValidator.validateUserAndPassword(requestDTO.getUserName(), requestDTO.getPassword())).thenReturn(null);
        when(userService.getImageByImageId(requestDTO.getHash())).thenReturn(imageResponse);

        // Act
        ResponseEntity<String> result = userController.getImageByImageId(requestDTO);

        // Assert
        verify(userValidator).validateUserAndPassword(requestDTO.getUserName(), requestDTO.getPassword());
        verify(userService).getImageByImageId(requestDTO.getHash());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(imageResponse, result.getBody());
    }

    @Test
    void testGetImageByImageIdNotFound() {
        // Arrange
        ImageRequestDTO requestDTO = new ImageRequestDTO();

        when(userValidator.validateUserAndPassword(requestDTO.getUserName(), requestDTO.getPassword())).thenReturn(null);
        when(userService.getImageByImageId(requestDTO.getHash())).thenThrow(new IllegalArgumentException("Image not found"));

        // Act
        ResponseEntity<String> result = userController.getImageByImageId(requestDTO);

        // Assert
        verify(userValidator).validateUserAndPassword(requestDTO.getUserName(), requestDTO.getPassword());
        verify(userService).getImageByImageId(requestDTO.getHash());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Image not found", result.getBody());
    }
}
