package com.tunetown.config;

import com.google.api.services.storage.Storage;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import jakarta.servlet.http.Part;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;


@Configuration
public class FirebaseConfig {

    InputStream serviceAccount;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        serviceAccount = new FileInputStream("tunetown-6b63a-firebase-adminsdk-9g0m6-193c149ad7.json");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://tunetown-6b63a-default-rtdb.firebaseio.com") // Replace with your Firebase Realtime Database URL
                .setStorageBucket("tunetown-6b63a.appspot.com")
                .build();

        return FirebaseApp.initializeApp(options);
<<<<<<< HEAD
    }}
=======
    }}
>>>>>>> 6965533f857d8e954b5b65eb5774732861f30023
