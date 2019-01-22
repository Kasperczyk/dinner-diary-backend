package de.kasperczyk.dinnerdiary.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kasperczyk.dinnerdiary.account.Account;
import de.kasperczyk.dinnerdiary.account.AccountDto;
import de.kasperczyk.dinnerdiary.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import static de.kasperczyk.dinnerdiary.security.SecurityConstants.*;

// Filter to handle user authentication (login)
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final ObjectMapper objectMapper;
    private final AccountService accountService;
    private final long expirationTimeInMinutes;

    JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                            ObjectMapper objectMapper,
                            AccountService accountService,
                            long expirationTimeInMinutes) {
        super.setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.accountService = accountService;
        this.expirationTimeInMinutes = expirationTimeInMinutes;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            AccountDto accountDto = objectMapper.readValue(request.getInputStream(), AccountDto.class);
            LOG.info("Trying to authenticate user with email address '" + accountDto.getEmailAddress() + "'");
            return super.getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(accountDto.getEmailAddress(), accountDto.getPassword()));
        } catch (IOException e) {
            LOG.error("IOException during authentication attempt: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException {
        Account account = (Account) authResult.getPrincipal();
        this.accountService.updateLastLoginTimestamp(account.getEmailAddress(), LocalDateTime.now());
        addBodyAndHeadersToResponse(response, account);
        LOG.info("Successful authentication attempt for user with email address '" + account.getEmailAddress() + "'");
    }

    private void addBodyAndHeadersToResponse(HttpServletResponse response, Account account) throws IOException {
        response.getWriter().write(objectMapper.writeValueAsString(account.getId()));
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeInMinutes * 60 * 1000);
        response.addHeader(HEADER_KEY_EXPIRES, objectMapper.writeValueAsString(expirationDate));
        response.addHeader(HEADER_KEY_AUTHORIZATION, TOKEN_PREFIX + createJwtToken(account.getEmailAddress(), expirationDate));
        response.addHeader(HEADER_KEY_ACCESS_CONTROL_EXPOSE_HEADERS, HEADER_KEY_AUTHORIZATION);
    }

    private String createJwtToken(String emailAddress, Date expiresAt) {
        return JWT.create()
                .withSubject(emailAddress)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC512(SECRET.getBytes()));
    }
}
