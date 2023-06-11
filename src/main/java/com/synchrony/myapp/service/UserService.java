package com.synchrony.myapp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synchrony.myapp.dao.model.User;
import com.synchrony.myapp.dao.model.UserImage;
import com.synchrony.myapp.dao.repository.UserImageRepository;
import com.synchrony.myapp.dao.repository.UserRepository;
import com.synchrony.myapp.exception.ImageProcessingException;
import com.synchrony.myapp.mapper.UserImageMapper;
import com.synchrony.myapp.mapper.UserMapper;
import com.synchrony.myapp.model.UserDTO;
import com.synchrony.myapp.model.UserImageDTO;
import com.synchrony.myapp.model.UserResponseDTO;

@Service
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserImageRepository userImageRepository;

	@Autowired
	private ImgurService imgurService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserImageMapper userImageMapper;

	/**
	 * This method is used to save user registration details.
	 *
	 * @param reqUserDTO The UserDTO containing the user registration details.
	 * @return The saved UserDTO.
	 */
	@Transactional
	public UserDTO saveUser(final UserDTO reqUserDTO) {
		LOGGER.info("Entered saveUser method with UserDTO: {}", reqUserDTO);

		User user = userMapper.dtoToEntity(reqUserDTO);
		user = userRepository.save(user);

		final UserDTO resUserDTO = userMapper.entityToDto(user);

		LOGGER.info("Exiting saveUser method after registering user with details: {}", resUserDTO);
		return resUserDTO;
	}

	/**
	 * This method is used to get user details based on userName.
	 *
	 * @param userName The username.
	 * @return The UserDTO containing the user details, or null if not found.
	 */
	public UserDTO getUserByUserName(String userName) {
		UserDTO userDTO = null;
		User user = userRepository.findByUserName(userName);
		if (user != null) {
			userDTO = userMapper.entityToDto(user);
		}
		return userDTO;
	}

	/**
	 * This method is used to upload an image using the IMGUR API wrapper service and make an entry in the user_images table.
	 *
	 * @param data     The image data.
	 * @param fileName The name of the image file.
	 * @param userId   The ID of the user.
	 * @return The uploaded UserImageDTO.
	 * @throws ImageProcessingException if an error occurs during image upload.
	 */
	public UserImageDTO uploadImage(final String data, final String fileName, final Integer userId) {
		LOGGER.info("Entered uploadImage method");

		try {
			// Calling imgur service to upload image
			final String responseBody = imgurService.upload(data);

			final Map<String, String> responseMap = deSerializeJson(responseBody);

			UserImage userImage = new UserImage();
			userImage.setUserId(userId);
			userImage.setSrcImageName(fileName);

			// Copy response attributes to UserImage entity
			copyJsonResponseToUserImage(responseMap, userImage);

			final UserImage savedUserImage = userImageRepository.save(userImage);
			final UserImageDTO userImageDTO = userImageMapper.entityToDto(savedUserImage);

			LOGGER.info("Exiting uploadImage method with userImage details: {}", userImageDTO);

			return userImageDTO;
		} catch (Exception e) {
			LOGGER.error("Error occurred during image upload: {}", e.getMessage());
			// Handle the exception or rethrow it
			throw new ImageProcessingException("Failed to upload image", e);
		}
	}

	/**
	 * This method is used to delete an image from IMGUR API and the user_images table.
	 *
	 * @param deleteHash The delete hash of the image.
	 * @param userId     The ID of the user.
	 * @return true if the image was successfully deleted, false otherwise.
	 * @throws ImageProcessingException if an error occurs during image deletion.
	 */
	@Transactional
	public Boolean deleteImage(final String deleteHash, final Integer userId) {
		LOGGER.info("Entered deleteImage method");

		try {
			final String responseBody = imgurService.delete(deleteHash);
			final JSONObject responseObj = new JSONObject(responseBody);

			if (responseObj.getInt("status") == 200) {
				userImageRepository.deleteByUserIdAndImgurImageDeleteHash(userId, deleteHash);
				LOGGER.info("Image deleted successfully");
				return true;
			} else {
				LOGGER.warn("Failed to delete image: {}", responseObj.getString("message"));
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred during image deletion: {}", e.getMessage());
			// Handle the exception or rethrow it
			throw new ImageProcessingException("Failed to delete image", e);
		}
	}

	// Rest of the code...

	/**
	 * This method is used to get user details and associated images based on userName.
	 *
	 * @param userName The username.
	 * @return The UserResponseDTO containing the user details and associated images.
	 */
	public UserResponseDTO getUserDetailsByUserName(final String userName) {
	    LOGGER.info("Entered getUserDetailsByUserName method with userName: {}", userName);

	    // Get user details
	    final User user = userRepository.findByUserName(userName);

	    // Get images associated with user
	    final List<UserImage> userImageList = userImageRepository.findByUserId(user.getUserId());

	    // Prepare response DTO
	    UserResponseDTO userResponseDTO = new UserResponseDTO();
	    userResponseDTO.setUserId(user.getUserId());
	    userResponseDTO.setUserName(user.getUserName());
	    userResponseDTO.setEmail(user.getEmail());
	    List<UserImageDTO> userImageDTOList = new ArrayList<>();
	    if (userImageList != null && !userImageList.isEmpty()) {
	        userImageList.forEach(i -> {
	            userImageDTOList.add(userImageMapper.entityToDto(i));
	        });
	    }
	    userResponseDTO.setUserImages(userImageDTOList);

	    LOGGER.info("Exiting getUserDetailsByUserName method with userDetails: {} for userName: {}", userResponseDTO,
	            userName);

	    return userResponseDTO;
	}

	/**
	 * This method retrieves the image by its image ID from the Imgur API.
	 *
	 * @param imageId The ID of the image to retrieve.
	 * @return The image data as a string.
	 * @throws ImageProcessingException If there is an error retrieving the image.
	 */
	public String getImageByImageId(String imageId) throws ImageProcessingException {
	    LOGGER.info("Entered into getImageByImageId method with imageId: {}", imageId);

	    try {
	        // Call the Imgur service to get the image by its ID
	        String imageData = imgurService.getImageByImageId(imageId);

	        LOGGER.info("Exiting from getImageByImageId method with image data for imageId: {}", imageId);
	        return imageData;
	    } catch (Exception e) {
	        LOGGER.error("Exception occurred while retrieving image by ID: {}", imageId, e);
	        throw new ImageProcessingException("Failed to retrieve image", e);
	    }
	}

	/**
	 * This method is used for deserializing the JSON response and storing the required attribute values into a map from the JSON response.
	 *
	 * @param responseBody The JSON response body.
	 * @return The map containing the deserialized attributes.
	 * @throws JSONException if an error occurs during JSON deserialization.
	 */
	private Map<String, String> deSerializeJson(String responseBody) throws JSONException {
	    Map<String, String> responseMap = new HashMap<>();

	    JSONObject responseObj = new JSONObject(responseBody);

	    if (responseObj.getInt("status") == 200) {
	        JSONObject dataObj = responseObj.getJSONObject("data");

	        responseMap.put("id", dataObj.getString("id"));
	        responseMap.put("title", dataObj.optString("title", null));
	        responseMap.put("description", dataObj.optString("description", null));
	        responseMap.put("type", dataObj.getString("type"));
	        responseMap.put("deletehash", dataObj.getString("deletehash"));
	        responseMap.put("link", dataObj.getString("link"));
	    }

	    return responseMap;
	}

	/**
	 * This method is used to copy the attributes from the JSON response to the UserImage entity.
	 *
	 * @param responseMap The map containing the attributes from the JSON response.
	 * @param userImage   The UserImage entity to copy the attributes to.
	 */
	private void copyJsonResponseToUserImage(Map<String, String> responseMap, UserImage userImage) {
	    userImage.setImgurImageId(responseMap.get("id"));
	    userImage.setImgurImageTitle(responseMap.get("title"));
	    userImage.setImgurImageDesc(responseMap.get("description"));
	    userImage.setImgurImageType(responseMap.get("type"));
	    userImage.setImgurImageLink(responseMap.get("link"));
	    userImage.setImgurImageDeleteHash(responseMap.get("deletehash"));
	    userImage.setUploadedDate(new Date());
	}

	// End of helper methods


}
