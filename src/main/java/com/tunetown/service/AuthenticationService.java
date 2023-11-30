package com.tunetown.service;

import com.tunetown.model.authentication.AuthenticationRequest;
import com.tunetown.model.authentication.AuthenticationResponse;
import com.tunetown.model.authentication.RegisterRequest;
import com.tunetown.service.jwt.JwtService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    @Resource
    UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Allow user to register new account
     * @return Access Token after new account has been saved
     */
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles("USER")
                .build();

        // Create new User
        com.tunetown.model.User dbUser = new com.tunetown.model.User();

        dbUser.setUserName(request.getUserName());
        dbUser.setEmail(request.getEmail());
        dbUser.setPassword(request.getPassword());
        dbUser.setBirthDate(request.getBirthDate());
        dbUser.setRole("USER");
        userService.addUser(dbUser);

        // Return access_token after save new user
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().access_token(jwtToken).build();
    }

    /**
     * Verify user information
     * @param request AuthenticationRequest includes {email, password}
     * @return access token if email and password are valid
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        // verify email and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // get user's information from db
        com.tunetown.model.User dbUser = userService.getActiveUserByEmail(request.getEmail());
        UserDetails userDetails = User.builder()
                .username(dbUser.getEmail())
                .password(dbUser.getPassword())
                .roles(dbUser.getRole())
                .build();

        //return access_token
        String jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .access_token(jwtToken)
                .id(dbUser.getId())
                .userName(dbUser.getUserName())
                .role(dbUser.getRole())
                .build();
}
    @Transactional
    public void changePassword(String email, String newPassword) {
        com.tunetown.model.User dbUser = userService.getActiveUserByEmail(email);
        dbUser.setPassword(passwordEncoder.encode(newPassword));
    }
}
