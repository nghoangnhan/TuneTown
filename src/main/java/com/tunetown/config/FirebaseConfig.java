package com.tunetown.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


@Configuration
public class FirebaseConfig {

    InputStream serviceAccount;


    /**
     * Initialize (Build) Firebase App
     * @throws IOException
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        serviceAccount = new FileInputStream("FirebaseCredentials/tunetown-6b63a-firebase-adminsdk-9g0m6-193c149ad7.json");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://tunetown-6b63a-default-rtdb.firebaseio.com") // Replace with your Firebase Realtime Database URL
                .setStorageBucket("tunetown-6b63a.appspot.com")
                .build();

        return FirebaseApp.initializeApp(options);
    }
}