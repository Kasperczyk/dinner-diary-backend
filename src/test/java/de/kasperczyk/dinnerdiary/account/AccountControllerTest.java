package de.kasperczyk.dinnerdiary.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.kasperczyk.dinnerdiary.AbstractMockMvcTest;
import de.kasperczyk.dinnerdiary.TestDataFactory;
import de.kasperczyk.dinnerdiary.error.AccountNotFoundException;
import de.kasperczyk.dinnerdiary.error.ApiErrorDto;
import de.kasperczyk.dinnerdiary.error.EmailAddressAlreadyInUseException;
import de.kasperczyk.dinnerdiary.error.IncorrectPasswordException;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.webmvc.json.patch.Patch;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountControllerTest extends AbstractMockMvcTest {

    private static final String REQUEST_URL = "/accounts";
    private static final String PATH_PARAMETER = "/{accountId}";
    private static final UUID ACCOUNT_ID = UUID.fromString("ba613fea-8526-4787-86ac-681a57434466");

    @MockBean
    private AccountService accountServiceMock;

    @Test
    public void register_returns201Created_whenAccountIsSuccessfullyRegistered() throws Exception {
        AccountDto accountDto = TestDataFactory.createDefaultTestAccountDto();
        mockMvc.perform(post(REQUEST_URL)
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
        verify(accountServiceMock).registerAccount(any(Account.class));
    }

    @Test
    public void register_returns409ConflictAndApiErrorDto_whenEmailAddressIsAlreadyInUse() throws Exception {
        doThrow(EmailAddressAlreadyInUseException.class).when(accountServiceMock).registerAccount(any(Account.class));
        AccountDto accountDto = TestDataFactory.createDefaultTestAccountDto();
        mockMvc.perform(post(REQUEST_URL)
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isConflict())
                .andExpect(content().string(expectedApiErrorDto(HttpStatus.CONFLICT)));
    }

    @Test
    public void register_returns400BadRequestAndApiErrorDto_whenRequestBodyIsInvalid() throws Exception {
        AccountDto accountDto = TestDataFactory.createDefaultTestAccountDto();
        accountDto.setUsername(null);
        mockMvc.perform(post(REQUEST_URL)
                .content(objectMapper.writeValueAsString(accountDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatusCode", is(400)))
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())));
    }

    @Test
    public void getAccount_returns200OkAndResponseBody_whenAccountIsFound() throws Exception {
        Account account = TestDataFactory.createDefaultTestAccount();
        AccountDto accountDto = modelMapper.map(account, AccountDto.class);
        when(accountServiceMock.getAccount(ACCOUNT_ID)).thenReturn(account);
        mockMvc.perform(get(REQUEST_URL + PATH_PARAMETER, ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(accountDto)));
    }

    @Test
    public void getAccount_returns404NotFoundAndApiErrorDto_whenAccountIsNotFound() throws Exception {
        when(accountServiceMock.getAccount(ACCOUNT_ID)).thenThrow(AccountNotFoundException.class);
        mockMvc.perform(get(REQUEST_URL + PATH_PARAMETER, ACCOUNT_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedApiErrorDto(HttpStatus.NOT_FOUND)));
    }

    @Test
    public void deleteAccount_returns200Ok_whenAccountIsSuccessfullyDeleted() throws Exception {
        doNothing().when(accountServiceMock).deleteAccount(ACCOUNT_ID);
        mockMvc.perform(delete(REQUEST_URL + PATH_PARAMETER, ACCOUNT_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void patchAccount_returns200Ok_whenUsernameAndEmailAddressAreSuccessfullyUpdated() throws Exception {
        doNothing().when(accountServiceMock).patchUsernameAndEmailAddress(any(UUID.class), any(Patch.class), anyString());
        mockMvc.perform(patch(REQUEST_URL + PATH_PARAMETER, ACCOUNT_ID)
                .content("[{\"op\": \"replace\", \"path\": \"/username\", \"value\": \"Test\"}," +
                          "{\"op\": \"replace\", \"path\": \"/emailAddress\", \"value\": \"kasperczyk.rene@test.com\"}]")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void patchAccount_returns200Ok_whenPasswordIsSuccessfullyUpdated() throws Exception {
        doNothing().when(accountServiceMock).patchPassword(any(UUID.class), any(Patch.class), anyString());
        mockMvc.perform(patch(REQUEST_URL + PATH_PARAMETER, ACCOUNT_ID)
                .param("currentPassword", "currentPassword")
                .content("[{\"op\": \"replace\", \"path\": \"/password\", \"value\": \"newPassword\"}]")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void patchAccount_returns400BadRequestAndApiErrorDto_whenRequestContainsMalformedJsonPatchBody() throws Exception {
        doNothing().when(accountServiceMock).patchPassword(any(UUID.class), any(Patch.class), anyString());
        mockMvc.perform(patch(REQUEST_URL + PATH_PARAMETER, ACCOUNT_ID)
                .content("[{\"oops\" \"replace\", \"path\": \"/username\", \"value\": \"Test\"}," +
                         "{\"op\": \"replace\", \"path\": \"/emailAddress\", \"value\": \"kasperczyk.rene@test.com\"}]")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatusCode", is(400)))
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())));
    }

    @Test
    public void patchAccount_returns400BadRequestAndApiErrorDto_whenIncorrectPasswordIsPassed() throws Exception {
        doThrow(IncorrectPasswordException.class).when(accountServiceMock).patchPassword(any(UUID.class), any(Patch.class), anyString());
        mockMvc.perform(patch(REQUEST_URL + PATH_PARAMETER, ACCOUNT_ID)
                .param("currentPassword", "currentPassword")
                .content("[{\"op\": \"replace\", \"path\": \"/password\", \"value\": \"newPassword\"}]")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatusCode", is(400)))
                .andExpect(jsonPath("$.httpStatus", is(HttpStatus.BAD_REQUEST.name())));
    }

    private String expectedApiErrorDto(HttpStatus httpStatus) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new ApiErrorDto(httpStatus.value(), httpStatus, null));
    }
}
