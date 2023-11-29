package com.tunetown.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // Extract user details from OAuth2User (assuming Google is returning OIDC claims)
        String email = oauth2User.getAttribute("email");

        // Print the email
        log.info("User's email: " + email);

        // You can process the retrieved user details here or return the OAuth2User as needed

        return oauth2User; // Return the OAuth2User or process and return custom UserDetails
    }
}