package com.tunetown.controller;

import com.tunetown.model.authentication.AuthenticationRequest;
import com.tunetown.model.authentication.AuthenticationResponse;
import com.tunetown.model.authentication.RegisterRequest;
import com.tunetown.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AuthenticationController {
    @Resource
    private AuthenticationService authenticationService;
    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody RegisterRequest request) {
        return authenticationService.register(request);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
