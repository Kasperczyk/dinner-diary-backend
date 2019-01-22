package de.kasperczyk.dinnerdiary.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.kasperczyk.dinnerdiary.AbstractMockMvcTest;
import de.kasperczyk.dinnerdiary.TestDataFactory;
import de.kasperczyk.dinnerdiary.error.AccountNotFoundException;
import de.kasperczyk.dinnerdiary.error.ApiErrorDto;
import de.kasperczyk.dinnerdiary.error.EmailAddressAlreadyInUseException;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void register_returns400BadRequestAndApiError_whenRequestBodyIsInvalid() throws Exception {
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
    public void getAccount_returns404NotFound_whenAccountIsNotFound() throws Exception {
        when(accountServiceMock.getAccount(ACCOUNT_ID)).thenThrow(AccountNotFoundException.class);
        mockMvc.perform(get(REQUEST_URL + PATH_PARAMETER, ACCOUNT_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedApiErrorDto(HttpStatus.NOT_FOUND)));
    }

    private String expectedApiErrorDto(HttpStatus httpStatus) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new ApiErrorDto(httpStatus.value(), httpStatus, null));
    }
}
