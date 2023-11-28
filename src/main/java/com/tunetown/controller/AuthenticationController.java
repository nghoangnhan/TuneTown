package com.tunetown.controller;

import com.tunetown.model.authentication.AuthenticationRequest;
import com.tunetown.model.authentication.AuthenticationResponse;
import com.tunetown.model.authentication.ForgetPasswordRequest;
import com.tunetown.model.authentication.RegisterRequest;
import com.tunetown.service.AuthenticationService;
import com.tunetown.service.MailService;
import com.tunetown.service.UserService;
import com.tunetown.utils.OTPUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AuthenticationController {
    @Resource
    private AuthenticationService authenticationService;
    @Resource
    private UserService userService;
    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody RegisterRequest request) {
        return authenticationService.register(request);
    }

    /**
     * Enter email and password for getting accessToken
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    //TODO: need to be implemented
    @PostMapping("/verifyAccessToken")
    public Map<String, Object> verifyAccessToken() {
        return Map.of(
                "access_token", ""
        );
    }

    @PostMapping(path = "/forgetPassword",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        if(forgetPasswordRequest.getType().equals("getOTP")) {
            if(userService.getActiveUserByEmail(forgetPasswordRequest.getEmail()) != null)
            {
                MailService.sendMail(forgetPasswordRequest.getEmail());
                return ResponseEntity.ok("OTP code has been sent to email " + forgetPasswordRequest.getEmail());
            }
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is not existed");
        }
        else if (forgetPasswordRequest.getType().equals("verifyOTP")) {
            if(OTPUtils.verifyOTP(forgetPasswordRequest.getEmail(), forgetPasswordRequest.getOtp()))
                return ResponseEntity.ok("OTP matches");
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP does not match");
        }
        else {
            authenticationService.changePassword(forgetPasswordRequest.getEmail(), forgetPasswordRequest.getNewPassword());
            return ResponseEntity.ok("Change password successfully");
        }

    }
}
