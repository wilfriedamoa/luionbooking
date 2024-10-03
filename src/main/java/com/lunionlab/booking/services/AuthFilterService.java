package com.lunionlab.booking.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthFilterService implements Filter {
    @Autowired
    UserDetailService userDetailService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String bearerToken = httpRequest.getHeader("Authorization");
        String tokenName = "Bearer ";
        if (bearerToken != null && bearerToken.startsWith(tokenName)) {
            String token = bearerToken.substring(tokenName.length());
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetail = userDetailService.loadUserByUsername(token);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetail,
                            null, userDetail.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } catch (UsernameNotFoundException usernameEx) {
                    System.out.println(usernameEx);
                }

            }
        }
        filterChain.doFilter(httpRequest, response);
    }
}
