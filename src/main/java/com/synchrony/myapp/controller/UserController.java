package com.synchrony.myapp.controller;

import java.io.IOException;
import java.util.Base64;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.synchrony.myapp.model.ImageRequestDTO;
import com.synchrony.myapp.model.UserDTO;
import com.synchrony.myapp.model.UserImageDTO;
import com.synchrony.myapp.model.UserResponseDTO;
import com.synchrony.myapp.service.UserService;
import com.synchrony.myapp.validator.UserValidator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/user")
@Api(value = "user", description = "REST API for User Management", tags = {"user"})
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/registration", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "User Registration")
    public UserDTO registration(@RequestBody UserDTO userDTO, BindingResult errors) throws BindException {
		LOGGER.info("Entered into registration method");
		
		//validating request
		userValidator.validate(userDTO, errors);
		
		if(errors.hasErrors()) {
			throw new BindException(errors);
		}
		
		
		//encrypted password
		userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		
		LOGGER.debug("user registration request:{}", userDTO);
		
		//call registration service
		UserDTO registeredUserDTO = userService.saveUser(userDTO);
		
		LOGGER.info("Exiting from registration method with details :{}", registeredUserDTO);
		
		return registeredUserDTO;
	}

    @GetMapping(value = "/{userName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a user by username")
    public UserResponseDTO getUserDetailsByUserName(@PathVariable String userName) {
        LOGGER.info("Entered into viewUser method with userName: {}", userName);

        userValidator.validateUser(userName);

        UserResponseDTO userResponseDTO = userService.getUserDetailsByUserName(userName);

        LOGGER.info("Exiting from viewUser method with userDetails: {} for userName: {}", userResponseDTO, userName);

        return userResponseDTO;
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Upload image")
    public UserImageDTO uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("userName") String userName,
                                    @RequestParam("password") String password) throws IOException {
        LOGGER.info("Entered into upload image method with userName: {}", userName);

        UserDTO userDTO = userValidator.validateUserAndPassword(userName, password);

        byte[] bytes = file.getBytes();
        String data = Base64.getEncoder().encodeToString(bytes);
        LOGGER.debug("Converted image into encoded string.");

        UserImageDTO userImageDTO = userService.uploadImage(data, file.getOriginalFilename(), userDTO.getUserId());

        LOGGER.info("Exiting from upload image method for userName: {}", userName);

        return userImageDTO;
    }

    @DeleteMapping(value = "/image", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete image")
    public Boolean delete(@RequestBody ImageRequestDTO requestDTO) throws IOException {
        LOGGER.info("Entered into delete image method with userName: {}", requestDTO.getUserName());

        UserDTO userDTO = userValidator.validateUserAndPassword(requestDTO.getUserName(), requestDTO.getPassword());

        boolean isDeleted = userService.deleteImage(requestDTO.getHash(), userDTO.getUserId());

        LOGGER.info("Exiting from delete image {} method for userName: {}", requestDTO.getHash(), requestDTO.getUserName());

        return isDeleted;
    }

    @PostMapping(value = "/image/getImage", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get image by imageId")
    public ResponseEntity<String> getImageByImageId(@RequestBody ImageRequestDTO requestDTO) {
        LOGGER.info("Entered into view image method with userName:{}", requestDTO.getUserName());

        // Validating user
        userValidator.validateUserAndPassword(requestDTO.getUserName(), requestDTO.getPassword());

        try {
            String response = userService.getImageByImageId(requestDTO.getHash());

            LOGGER.info("Exiting from view image {} method for userName:{}", response, requestDTO.getUserName());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error occurred while retrieving the image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
        }
    }
}
