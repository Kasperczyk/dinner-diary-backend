package de.kasperczyk.dinnerdiary.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kasperczyk.dinnerdiary.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static de.kasperczyk.dinnerdiary.security.SecurityConstants.REGISTRATION_URL;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;
    private final AccountService accountService;

    @Value("${dd.expiration-time-minutes}")
    private long expirationTimeInMinutes;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          ObjectMapper objectMapper,
                          AccountService accountService) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.objectMapper = objectMapper;
        this.accountService = accountService;
    }

    @Bean
    public JwtAuthenticationFilter authenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(), objectMapper, accountService, expirationTimeInMinutes);
    }

    @Bean
    public JwtAuthorizationFilter authorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(authenticationManager());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, REGISTRATION_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(authenticationFilter())
                .addFilter(authorizationFilter())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManager) throws Exception {
        authenticationManager.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return corsConfigurationSource;
    }
}
