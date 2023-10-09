package com.tunetown.service;

import com.tunetown.model.authentication.AuthenticationRequest;
import com.tunetown.model.authentication.AuthenticationResponse;
import com.tunetown.model.authentication.RegisterRequest;
import com.tunetown.service.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthenticationService {
    @Resource
    UserService userService;
    @Resource
    JwtService jwtService;
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles("USER")
                .build();
        userService.addUser((com.tunetown.model.User) user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
                )
        );
        var user = userService.getActiveUserByEmail(request.getEmail());
        var jwtToken = jwtService.generateToken((UserDetails) user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();}
}
