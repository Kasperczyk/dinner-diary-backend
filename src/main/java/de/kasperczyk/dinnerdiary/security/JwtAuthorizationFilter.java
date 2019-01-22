package de.kasperczyk.dinnerdiary.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static de.kasperczyk.dinnerdiary.security.SecurityConstants.*;

// Filter to handle authorization, i.e. is the user allowed to access a resource
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_KEY_AUTHORIZATION);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthenticationToken(header);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String header) {
        String emailAddress = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(header.replace(TOKEN_PREFIX, ""))
                .getSubject();
        if (emailAddress != null) {
            return new UsernamePasswordAuthenticationToken(emailAddress, null, Collections.emptyList());
        }
        return null;
    }
}
