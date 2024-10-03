package com.lunionlab.booking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lunionlab.booking.emum.JwtAudience;
import com.lunionlab.booking.models.UserModel;
import com.lunionlab.booking.repository.ProfileRepository;

import java.util.Map;
import java.util.List;
import java.util.Arrays;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    JwtService jwtService;

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String token) {
        Map<String, String> jwtData = jwtService.getIdentifierFromToken(token);
        if (jwtData.get("audience").equalsIgnoreCase(JwtAudience.USER)) {
            UserModel userModel = userService.getUserByEmail(jwtData.get("identifier"));
            if (userModel != null) {
                // obtenir le role de user sil lorsqu'il a un role
                // String role = "ROLE_" + user.getRole().getLibelle().toUpperCase();
                SimpleGrantedAuthority[] rolesArr = new SimpleGrantedAuthority[] {
                        // new SimpleGrantedAuthority(role),
                        new SimpleGrantedAuthority("ROLE_" + JwtAudience.USER)
                };
                List<SimpleGrantedAuthority> roles = Arrays.asList(rolesArr);
                return new User(userModel.getEmail(), userModel.getPassword(), roles);
            }

        }
        throw new UsernameNotFoundException("token invalide");
    }

}
