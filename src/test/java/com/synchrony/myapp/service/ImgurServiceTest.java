package com.synchrony.myapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ImgurServiceTest {

    private ImgurService imgurService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        imgurService = new ImgurService();
        ReflectionTestUtils.setField(imgurService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(imgurService, "imgurUrl", "https://api.imgur.com/3");
        ReflectionTestUtils.setField(imgurService, "clientId", "483ed9087ff3f01");
    }

    @Test
    void testUpload_SuccessfulUpload_ReturnsResponseBody() {
        String imageData = "your-image-data";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Client-ID 483ed9087ff3f01");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("image", imageData);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("response-body", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), eq(requestEntity), eq(String.class)))
                .thenReturn(responseEntity);

        String response = imgurService.upload(imageData);

        assertEquals("response-body", response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), eq(requestEntity), eq(String.class));
    }

    @Test
    void testUpload_HttpClientErrorException_ReturnsNull() {
        String imageData = "your-image-data";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Client-ID 483ed9087ff3f01");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("image", imageData);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), eq(requestEntity), eq(String.class)))
                .thenThrow(HttpClientErrorException.class);

        String response = imgurService.upload(imageData);

        assertNull(response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), eq(requestEntity), eq(String.class));
    }

    @Test
    void testDelete_SuccessfulDelete_ReturnsResponseBody() {
        String deleteHash = "your-delete-hash";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Client-ID 483ed9087ff3f01");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("response-body", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), eq(requestEntity), eq(String.class)))
                .thenReturn(responseEntity);

        String response = imgurService.delete(deleteHash);

        assertEquals("response-body", response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), eq(requestEntity), eq(String.class));
    }

    @Test
    void testDelete_HttpClientErrorException_ReturnsNull() {
        String deleteHash = "your-delete-hash";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Client-ID 483ed9087ff3f01");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), eq(requestEntity), eq(String.class)))
                .thenThrow(HttpClientErrorException.class);

        String response = imgurService.delete(deleteHash);

        assertNull(response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), eq(requestEntity), eq(String.class));
    }

    @Test
    void testGetImageByImageId_SuccessfulView_ReturnsResponseBody() {
        String imageId = "your-image-id";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Client-ID 483ed9087ff3f01");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>("response-body", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(requestEntity), eq(String.class)))
                .thenReturn(responseEntity);

        String response = imgurService.getImageByImageId(imageId);

        assertEquals("response-body", response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), eq(requestEntity), eq(String.class));
    }

    @Test
    void testGetImageByImageId_HttpClientErrorException_ReturnsNull() {
        String imageId = "your-image-id";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Client-ID 483ed9087ff3f01");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(requestEntity), eq(String.class)))
                .thenThrow(HttpClientErrorException.class);

        String response = imgurService.getImageByImageId(imageId);

        assertNull(response);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), eq(requestEntity), eq(String.class));
    }

}
