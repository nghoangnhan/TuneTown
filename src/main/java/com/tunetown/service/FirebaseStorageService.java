package com.tunetown.service;

import com.google.cloud.storage.*;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import com.tunetown.config.FirebaseConfig;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageOptions;import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.UUID;

@Service
public class FirebaseStorageService {
    @Resource
    FirebaseConfig firebaseConfig;

    String downloadUrlImage = "";
    String downloadUrlData = "";

    String appCheckToken = "";

    private String generateAppCheckToken() {
        try {
            appCheckToken = Arrays.toString(FirebaseAuth.getInstance(firebaseConfig.firebaseApp())
                    .createCustomToken("tunetowntest-6b63a")
                    .getBytes());

            return appCheckToken;
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // Upload Image to Firebase
    public String uploadImage(String filePath, String fileName) throws IOException {
        InputStream fileContentImage = new FileInputStream(filePath);
        try {
            Storage storage = StorageClient.getInstance(firebaseConfig.firebaseApp()).bucket("tunetown-6b63a.appspot.com").getStorage();

            BlobInfo blobInfo = BlobInfo.newBuilder("tunetown-6b63a.appspot.com", "images/" + fileName)
                    .setContentType("image/jpeg")
                    .setMetadata(ImmutableMap.of("firebaseStorageDownloadTokens", generateAppCheckToken()))
                    .build();

            // Upload the file to Firebase Storage
            storage.create(blobInfo, fileContentImage,
                    Storage.BlobWriteOption.userProject("tunetown-6b63a"),
                    Storage.BlobWriteOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));

            String token = UUID.randomUUID().toString();
            String encodedToken = URLEncoder.encode(token, "UTF-8");

            // Construct the download URL manually
            downloadUrlImage = "https://firebasestorage.googleapis.com/v0/b/" +
                    "tunetown-6b63a.appspot.com" +
                    "/o/" +
                    "images%2F" + fileName +
                    "?alt=media" ;

            // TODO: Use downloadUrlImage to add to Song.Poster()
            downloadUrlImage = downloadUrlImage + "&token=" + encodedToken;


            return downloadUrlImage;

        } catch (Exception e) {
            // Handle any other exception
            throw new RuntimeException(e);
        }
    }


    // Upload MP3 file to storage
    public String uploadMp3(String filePath, String fileName) throws IOException {
        InputStream fileContent2 = new FileInputStream(filePath);
        try {
            Storage storage = StorageClient.getInstance(firebaseConfig.firebaseApp()).bucket("tunetown-6b63a.appspot.com").getStorage();

            BlobInfo blobInfo = BlobInfo.newBuilder("tunetown-6b63a.appspot.com", "audios/" + fileName)
                    .setContentType("audio/mpeg")
                    .setMetadata(ImmutableMap.of("firebaseStorageDownloadTokens", generateAppCheckToken()))
                    .build();

            // Upload the file to Firebase Storage
            storage.create(blobInfo, fileContent2,
                    Storage.BlobWriteOption.userProject("tunetown-6b63a"),
                    Storage.BlobWriteOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));

            // Encode the token using Base64 encoding
            String encodedFilePath = URLEncoder.encode("audios/" + fileName, "UTF-8");
            downloadUrlData = "https://firebasestorage.googleapis.com/v0/b/" +
                    "tunetown-6b63a.appspot.com" +
                    "/o/" +
                    encodedFilePath +
                    "?alt=media";

            String token = UUID.randomUUID().toString();
            String encodedToken = URLEncoder.encode(token, "UTF-8");

            // TODO: Use downloadUrlData to add to Song.Data()
            downloadUrlData = downloadUrlData + "&token=" + encodedToken;


            return downloadUrlData;
        }
        catch (Exception e) {
            // Handle any other exception
            throw new RuntimeException(e);
        }
    }
}
