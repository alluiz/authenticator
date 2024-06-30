package com.example.authenticator.controllers;

import com.example.authenticator.dtos.user.ResetPasswordResponse;
import com.example.authenticator.dtos.user.UserRequest;
import com.example.authenticator.enums.ResultCodeEnum;
import com.example.authenticator.models.ResultCodeAndData;
import com.example.authenticator.services.PasswordService;
import com.example.authenticator.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.authenticator.controllers.ControllerTestHelper.asJsonString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordService passwordService;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Returns a created user status")
    public void shouldReturnUserWasCreatedWhenValidUser() throws Exception {

        var userRequest = new UserRequest("test", false);

        when(userService.create(userRequest)).thenReturn(ResultCodeEnum.SUCCESS_CREATE_USER_CODE);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("004"))
                .andExpect(jsonPath("$.message").value("User was created."))
                .andExpect(jsonPath("$.operationTime").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Returns a created user status with temporary password when service flag is enabled.")
    public void shouldReturnUserWasCreatedWithTempPasswordWhenValidUserAndServiceEnabled() throws Exception {

        var userRequest = new UserRequest("test", true);
        String temporaryPassword = "temp-password";

        when(userService.create(userRequest)).thenReturn(ResultCodeEnum.SUCCESS_CREATE_USER_CODE);
        when(passwordService.resetPassword(userRequest.username())).thenReturn(
                new ResultCodeAndData<>(ResultCodeEnum.SUCCESS_RESET_CODE,
                        new ResetPasswordResponse(temporaryPassword)));
        when(passwordService.isEnabledShowTemporaryPassword()).thenReturn(true);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("004"))
                .andExpect(jsonPath("$.message").value("User was created."))
                .andExpect(jsonPath("$.operationTime").isNotEmpty())
                .andExpect(jsonPath("$.data.temporaryPassword").value(temporaryPassword));
    }

    @Test
    @DisplayName("Returns a created user status without temporary password when service flag is disabled.")
    public void shouldReturnUserWasCreatedWithTempPasswordWhenValidUserAndServiceDisabled() throws Exception {

        var userRequest = new UserRequest("test", true);
        String temporaryPassword = "temp-password";

        when(userService.create(userRequest)).thenReturn(ResultCodeEnum.SUCCESS_CREATE_USER_CODE);
        when(passwordService.resetPassword(userRequest.username())).thenReturn(
                new ResultCodeAndData<>(ResultCodeEnum.SUCCESS_RESET_CODE,
                        new ResetPasswordResponse(temporaryPassword)));
        when(passwordService.isEnabledShowTemporaryPassword()).thenReturn(false);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("004"))
                .andExpect(jsonPath("$.message").value("User was created."))
                .andExpect(jsonPath("$.operationTime").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Returns an conflict error when user already exists")
    public void shouldReturnConflictWhenUserAlreadyExists() throws Exception {

        var userRequest = new UserRequest("test", false);

        when(userService.create(userRequest)).thenReturn(ResultCodeEnum.ERROR_USER_ALREADY_EXISTS_CODE);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("106"))
                .andExpect(jsonPath("$.message").value("User already exists."))
                .andExpect(jsonPath("$.operationTime").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Returns an error when reset fails")
    public void shouldReturnErrorWhenResetFails() throws Exception {

        var userRequest = new UserRequest("test", true);

        when(userService.create(userRequest)).thenReturn(ResultCodeEnum.SUCCESS_CREATE_USER_CODE);
        when(userService.remove(userRequest.username())).thenReturn(ResultCodeEnum.SUCCESS_DELETE_USER_CODE);
        when(passwordService.resetPassword(userRequest.username())).thenReturn(new ResultCodeAndData<>(ResultCodeEnum.ERROR_NOTIFICATION_CODE));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRequest)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("103"))
                .andExpect(jsonPath("$.message").value("The notification service is UNAVAILABLE now. Try again later."))
                .andExpect(jsonPath("$.operationTime").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Returns an error when reset fails with generic error")
    public void shouldReturnErrorWhenResetFailsWithGenericError() throws Exception {

        var userRequest = new UserRequest("test", true);

        when(userService.create(userRequest)).thenReturn(ResultCodeEnum.SUCCESS_CREATE_USER_CODE);
        when(userService.remove(userRequest.username())).thenReturn(ResultCodeEnum.SUCCESS_DELETE_USER_CODE);
        when(passwordService.resetPassword(userRequest.username())).thenReturn(new ResultCodeAndData<>(ResultCodeEnum.ERROR_CODE));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("100"))
                .andExpect(jsonPath("$.message").value("An unknown error has ocurred. Try again later, if the error persists, contact the administrator."))
                .andExpect(jsonPath("$.operationTime").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
