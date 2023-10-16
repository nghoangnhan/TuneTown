package com.tunetown.service;

import com.google.cloud.storage.*;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import com.tunetown.config.FirebaseConfig;
import com.tunetown.repository.SongRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

@Service
public class FirebaseStorageService {
    @Resource
    FirebaseConfig firebaseConfig;

    String downloadUrlImage = "";
    String appCheckToken = "";
    String songData = "";
    String partDownloadUrl = "";

    /**
     * Generate App Token for handle with FirebaseStorage
     * @return Token
     */
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


    /**
     * - Get storage on firebase
     * - Set type for fileUpload and generateAppCheckToken
     * - Create a new fileUpload on storage
     * @param filePath: Get from computer
     * @param fileName: Get the name of file from filePath
     * @return downloadUrl used to add to songPoster field
     * @throws IOException
     */
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


    /**
     * - Get storage on firebase
     * - Set type for fileUpload and generateAppCheckToken
     * - Upload by each chunk of chunk size by separating file capacity
     * @param filePath: Get from computer
     * @param fileName: Get the name of file from filePath
     * @return downloadUrl used to add to songData field
     * @throws IOException
     */
    public String uploadMp3(String filePath, String fileName, int numberOfParts) throws IOException {
        // Read file MP3 from filePath
        InputStream fileContent2 = new FileInputStream(filePath);

        // Size of each part
        long fileLength = fileContent2.available(); // Get the total length of the file
        int chunkSize = (int) Math.ceil((double) fileLength / numberOfParts);

        int bytesRead;
        int partNumber = 1;

        // Firebase storage config
        try {
            Storage storage = StorageClient.getInstance(firebaseConfig.firebaseApp()).bucket("tunetown-6b63a.appspot.com").getStorage();

            byte[] chunk = new byte[chunkSize];
            // Read and upload each part
            while ((bytesRead = fileContent2.read(chunk)) > 0) {
                int subString = fileName.length() - 4;
                // Upload each part on firebase
                BlobInfo partInfo = BlobInfo.newBuilder(BlobId.of("tunetown-6b63a.appspot.com", "audios/" + fileName.substring(0, subString) + "/" + fileName.substring(0, subString) + "_" + partNumber + ".mp3"))
                        .setContentType("audio/mpeg")
                        .build();

                storage.create(partInfo, chunk);

                // Get the URL of each part
                partDownloadUrl = "https://storage.googleapis.com/tunetown-6b63a.appspot.com/audios/" + fileName.substring(0, subString) + "/" + fileName.substring(0, subString) + "_" + partNumber + ".mp3";
                partNumber++;
            }
            songData = partDownloadUrl.substring(0, partDownloadUrl.length() - 5);
            return songData;
        }
        catch (Exception e) {
            // Handle any other exception
            throw new RuntimeException(e);
        }
    }
}
