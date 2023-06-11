package com.synchrony.myapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.synchrony.myapp.exception.MethodFailureException;

import java.net.URI;

@Service
public class ImgurService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImgurService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${imgur.service.url}")
    private String imgurUrl;

    @Value("${imgur.clientId}")
    private String clientId;

    /**
     * This method is used to upload an image using the Imgur API.
     *
     * @param data holds the encoded image byte data format
     */
    public String upload(final String data) {
        LOGGER.info("Entered into upload method");

        try {
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization", "Client-ID " + clientId);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("image", data);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            URI uploadUrl = new URI(imgurUrl + "/image");
            ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, entity, String.class);

            String responseBody = response.getBody();
            LOGGER.info("Received response from service: {}", responseBody);
            return responseBody;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            handleException(e, "uploading image to Imgur");
        } catch (Exception e) {
            handleException(e, "uploading image to Imgur");
        }

        return null;
    }

    /**
     * This method is used to delete an image using the Imgur API.
     *
     * @param deleteHash holds the deletehash value
     */
    public String delete(final String deleteHash) {
        LOGGER.info("Entered into delete method");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Client-ID " + clientId);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            URI deleteUrl = new URI(imgurUrl + "/image/" + deleteHash);
            ResponseEntity<String> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, String.class);

            String responseBody = response.getBody();
            LOGGER.info("Received response from service: {}", responseBody);
            return responseBody;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            handleException(e, "deleting image from Imgur");
        } catch (Exception e) {
            handleException(e, "deleting image from Imgur");
        }

        return null;
    }

    /**
     * This method is used to view an image using the Imgur API.
     *
     * @param imageId holds the image ID value
     */
    public String getImageByImageId(final String imageId) {
        LOGGER.info("Entered into view method");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Client-ID " + clientId);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            URI viewUrl = new URI(imgurUrl + "/image/" + imageId);
            ResponseEntity<String> response = restTemplate.exchange(viewUrl, HttpMethod.GET, entity, String.class);

            String responseBody = response.getBody();
            LOGGER.info("Received response from service: {}", responseBody);
            return responseBody;
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                LOGGER.warn("Image with ID {} not found on Imgur", imageId);
                return null; // Return null to indicate image not found
            }
            handleException(e, "viewing image from Imgur");
        } catch (Exception e) {
            handleException(e, "viewing image from Imgur");
        }

        return null;
    }

    private void handleException(Exception e, String action) {
        LOGGER.error("Exception occurred while {} with error message: {}", action, e.getMessage(), e);
        String errorMsg = e.getMessage();
        throw new MethodFailureException("Exception occurred while " + action + " with error message: " + errorMsg);
    }
}
