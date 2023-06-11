package com.synchrony.myapp.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchrony.myapp.UsermanagementApplication;
import com.synchrony.myapp.model.ImageRequestDTO;
import com.synchrony.myapp.model.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = UsermanagementApplication.class)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSaveUser() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO(null, "pankaj", "123", "pankaj@gmail.com");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/user/registration")
                .content(asJsonString(userDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").exists());
    }

    @Test
    void testSaveUserWhenRequestIsInvalid() throws Exception {
        // Arrange
        LOGGER.info("====== VALIDATION FOR USERNAME ============");
        UserDTO userDTOWithNullUsername = new UserDTO(null, null, "123", "email4@mail.com");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/user/registration")
                .content(asJsonString(userDTOWithNullUsername))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        LOGGER.info("====== VALIDATION FOR PASSWORD ============");
        UserDTO userDTOWithNullPassword = new UserDTO(null, "subbuchinnam", null, "email4@mail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/registration")
                .content(asJsonString(userDTOWithNullPassword))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        LOGGER.info("====== VALIDATION FOR EMAIL ============");
        UserDTO userDTOWithNullEmail = new UserDTO(null, "subbuchinnam", "123", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/registration")
                .content(asJsonString(userDTOWithNullEmail))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testViewUser() throws Exception {
        // Arrange
        String username = "subbuchinnam";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").exists());
    }

    @Test
    void testDeleteImage() throws Exception {
        // Arrange
        ImageRequestDTO imageRequestDTO = new ImageRequestDTO("gLFMAGvQCPMFWw0", "subbuchinnam", "123");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/image")
                .content(asJsonString(imageRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
