package com.synchrony.myapp.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import com.synchrony.myapp.exception.BadRequestException;
import com.synchrony.myapp.exception.DataNotFoundException;
import com.synchrony.myapp.exception.UnAuthorizedException;
import com.synchrony.myapp.model.UserDTO;
import com.synchrony.myapp.service.UserService;

public class UserValidator implements Validator{

	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return UserDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		UserDTO userDTO = (UserDTO) obj;
		
		if (userDTO.getUserName() == null || userDTO.getUserName().isEmpty()) {
			errors.reject("userName", "UserName Can't be Null/Empty");
		}
		
		if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
			errors.reject("password", "Password Can't be Null/Empty");
		}
		
		if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
			errors.reject("email", "Email Can't be Null/Empty");
		}
		
		if(!errors.hasErrors()) {
			if(userService.getUserByUserName(userDTO.getUserName()) != null) {
				errors.reject("userName", "UserName already exists. Please try with different one.");
			}
		}
	}
	
	/**
	 * validating user credentials valid or not
	 * 
	 * @param userName
	 * @param password
	 */
	public UserDTO validateUserAndPassword(final String userName, final String password) {
		
		if (userName == null || userName.isEmpty()) {
			throw new BadRequestException(new FieldError("UserDTO", "userName", "UserName Can't be Null/Empty"));
		}
		
		if (password == null || password.isEmpty()) {
			throw new BadRequestException(new FieldError("UserDTO", "password", "Password Can't be Null/Empty"));
		}
		
		final UserDTO userDTO = userService.getUserByUserName(userName);
		
		if(userDTO == null) {
			throw new DataNotFoundException("Provided userName not exists.");
		}else {
			if(!passwordEncoder.matches(password, userDTO.getPassword())) {
				throw new UnAuthorizedException("incorrect userName or Password");
			}
		}
		
		return userDTO;
	}
	
	/**
	 * This method used to validate user and return userDTo for valid user
	 * 
	 * @param userName
	 * @return
	 */
	public void validateUser(final String userName) {
		
		if (userName == null || userName.isEmpty()) {
			throw new BadRequestException(new FieldError("UserDTO", "userName", "UserName Can't be Null/Empty"));
		}
		
		final UserDTO userDTO = userService.getUserByUserName(userName);
		
		if(userDTO == null) {
			throw new DataNotFoundException("Provided userName not exists.");
		}
		
	}
}