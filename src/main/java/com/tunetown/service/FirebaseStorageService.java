package com.tunetown.service;

import com.google.api.client.util.IOUtils;
import com.google.cloud.storage.*;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import com.tunetown.config.FirebaseConfig;
import com.tunetown.model.Song;
import com.tunetown.repository.SongRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
@Service
@Slf4j
public class FirebaseStorageService {
    @Resource
    FirebaseConfig firebaseConfig;
    private final Storage storage;
    String downloadUrlImage = "";
    String appCheckToken = "";
    String songData = "";
    String partDownloadUrl = "";
    @Resource
    SongRepository songRepository;


    public FirebaseStorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

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
        } catch (FirebaseAuthException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * - Get storage on firebase
     * - Set type for fileUpload and generateAppCheckToken
     * - Create a new fileUpload on storage
     * @param imageFile: Get from computer
     * @param fileName: Get the name of file from filePath
     * @return downloadUrl used to add to songPoster field
     */
    public String uploadImage(MultipartFile imageFile, String fileName) throws IOException {
        InputStream fileContentImage = imageFile.getInputStream();
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
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

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
     * @param mp3File: Get from computer
     * @param fileName: Get the name of file from filePath
     * @return downloadUrl used to add to songData field
     */
    public String uploadMp3(MultipartFile mp3File, String fileName, int numberOfParts) throws IOException {
        // Read file MP3 from filePath
        InputStream fileContent2 = mp3File.getInputStream();

        // Size of each part
        long fileLength = fileContent2.available(); // Get the total length of the file
        int chunkSize = (int) Math.ceil((double) fileLength / numberOfParts);

        int partNumber = 1;

        // Firebase storage config
        try {
            Storage storage = StorageClient.getInstance(firebaseConfig.firebaseApp()).bucket("tunetown-6b63a.appspot.com").getStorage();

            byte[] chunk = new byte[chunkSize];
            // Read and upload each part
            while ((fileContent2.read(chunk)) > 0) {
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
            songData = partDownloadUrl.substring(0, partDownloadUrl.length() - 6);
            return songData;
        }
        catch (Exception e) {
            // Handle any other exception
            throw new RuntimeException(e);
        }
    }

    public boolean checkValidImage(MultipartFile imgFile){
        try {
            InputStream contentFile = imgFile.getInputStream();

            Tika tika = new Tika();

            // Detect the content type of the file
            String contentType = tika.detect(contentFile);

            if (contentType.equals("image/png") || contentType.equals("image/jpeg")) {
                if(contentFile.available() < 1000000) // Do not allow the image > 1MB
                {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkValidMp3(MultipartFile mp3File){
        try {
            InputStream contentFile = mp3File.getInputStream();

            Tika tika = new Tika();

            // Detect the content type of the file
            String contentType = tika.detect(contentFile);

            if (contentType.equals("audio/mpeg")) {
                if(contentFile.available() < 10000000) // Do not allow the mp3 file > 10MB
                {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public byte[] combineMP3(Integer songId, String mp3Link) {
        try {
            String[] parts = new String[]{""};
            Optional<Song> optionalSong = songRepository.findById(songId);
            if (optionalSong.isEmpty()){
                parts = mp3Link.split("audios/");
            }
            else{
                Song song = optionalSong.get();
                parts = song.getSongData().split("audios/");
            }
            String fileName = parts[1].split("/")[0];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Download each part and write it to the output stream
            for (int partNumber = 1; partNumber <= 10; partNumber++) {
                String partObjectName = "audios/" + fileName + "/" + fileName + "_" + partNumber + ".mp3";
                Blob blob = storage.get("tunetown-6b63a.appspot.com", partObjectName);
                if (blob != null) {
                    byte[] blobBytes = blob.getContent();
                    outputStream.write(blobBytes);
                }
            }
            // Convert the ByteArrayOutputStream to a byte array
            byte[] combinedMP3Data = outputStream.toByteArray();
            // Close the output stream
            outputStream.close();
            return combinedMP3Data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
