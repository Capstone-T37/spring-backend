package com.mycompany.myapp.web.rest;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.firebase.auth.FirebaseAuth;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.web.rest.vm.LoginVM;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserJWTController} REST controller.
 */
@AutoConfigureMockMvc
@IntegrationTest
class UserJWTControllerIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    void testAuthorize() throws Exception {
        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = Mockito.mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockFirebaseAuthInstance = mock(FirebaseAuth.class);

            // Mock the static method call
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuthInstance);

            // Mock the instance method call
            when(mockFirebaseAuthInstance.createCustomToken(anyString())).thenReturn("MockedFirebaseToken");

            User user = new User();
            user.setLogin("user-jwt-controller");
            user.setEmail("user-jwt-controller@example.com");
            user.setActivated(true);
            user.setPassword(passwordEncoder.encode("test"));

            userRepository.saveAndFlush(user);

            LoginVM login = new LoginVM();
            login.setUsername("user-jwt-controller");
            login.setPassword("test");
            mockMvc
                .perform(
                    post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_token").isString())
                .andExpect(jsonPath("$.id_token").isNotEmpty())
                .andExpect(header().string("Authorization", not(nullValue())))
                .andExpect(header().string("Authorization", not(is(emptyString()))));

            // Assertions
            // Verify interactions
            verify(mockFirebaseAuthInstance).createCustomToken(anyString());
        }
    }

    @Test
    @Transactional
    void testAuthorizeWithRememberMe() throws Exception {
        // Mock FirebaseAuth and its instance
        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = Mockito.mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockFirebaseAuthInstance = mock(FirebaseAuth.class);

            // Mock the static method call
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuthInstance);

            // Mock the instance method call
            when(mockFirebaseAuthInstance.createCustomToken(anyString())).thenReturn("MockedFirebaseToken");

            User user = new User();
            user.setLogin("user-jwt-controller-remember-me");
            user.setEmail("user-jwt-controller-remember-me@example.com");
            user.setActivated(true);
            user.setPassword(passwordEncoder.encode("test"));

            userRepository.saveAndFlush(user);

            LoginVM login = new LoginVM();
            login.setUsername("user-jwt-controller-remember-me");
            login.setPassword("test");
            login.setRememberMe(true);
            mockMvc
                .perform(
                    post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_token").isString())
                .andExpect(jsonPath("$.id_token").isNotEmpty())
                .andExpect(header().string("Authorization", not(nullValue())))
                .andExpect(header().string("Authorization", not(is(emptyString()))));
            // Assertions
            // Verify interactions
            verify(mockFirebaseAuthInstance).createCustomToken(anyString());
        }
    }

    @Test
    void testAuthorizeFails() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("wrong-user");
        login.setPassword("wrong password");
        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.id_token").doesNotExist())
            .andExpect(header().doesNotExist("Authorization"));
    }
}
