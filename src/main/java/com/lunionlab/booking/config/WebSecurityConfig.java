package com.lunionlab.booking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lunionlab.booking.services.AuthEntryPointService;
import com.lunionlab.booking.services.AuthFilterService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {
    @Autowired
    AuthEntryPointService authEntryPointService;
    @Autowired
    AuthFilterService authFilterService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(cor -> {

        }).csrf(csrfprotect -> {
            csrfprotect.disable();
        });

        httpSecurity.formLogin(form -> {
            form.disable();
        });

        httpSecurity.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/test/**", "/error", "/api/auth/user", "/api/auth/code/verification",
                    "/api/auth/refresh/token").permitAll()
                    .anyRequest().authenticated();
        });

        httpSecurity.exceptionHandling(exception -> {
            exception.authenticationEntryPoint(authEntryPointService);
        });

        httpSecurity.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
        httpSecurity.addFilterBefore(authFilterService, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
