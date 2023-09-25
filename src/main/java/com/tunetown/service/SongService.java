package com.tunetown.service;

import com.google.api.Http;
import com.google.api.services.storage.Storage;
import com.google.cloud.storage.BlobInfo;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import com.tunetown.config.FirebaseConfig;
import com.tunetown.model.Genre;
import com.tunetown.model.Song;
import com.tunetown.repository.SongRepository;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SongService {

    @Resource
    SongRepository songRepository;

    @Resource
    FirebaseConfig firebaseConfig;

    public Page<List<Song>> getAllSongs(int pageNumber){
        Pageable pageable = (Pageable) PageRequest.of(pageNumber, 100);
        return songRepository.findByStatus(1, pageable);
    }

    public void addSong(Song song){
        songRepository.save(song);
    }


    // Soft Delete song
    public void deleteSong(int id){
        boolean song = songRepository.existsById(id);
        if(!song){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song with id = "+id +" does not exist!");
        }
        getActiveSongById(id).setStatus(0);
        songRepository.save(getActiveSongById(id));
    }

    @Transactional
    public void updateSong(int id, String name, String poster, String data, Integer listens, Integer likes, Integer status){
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Song with id = " + id +" does not exist!"));


        // Check valid before update
        if(name != null && name.length() > 0 && !Objects.equals(song.getSongName(), name)) // Check the new name != current name
        {
            song.setSongName(name);
        }

        if(poster != null && poster.length() > 0 && !Objects.equals(song.getPoster(), poster)) // Check the new poster != current poster
        {
            song.setPoster(poster);
        }

        if(data != null && data.length() > 0 && !Objects.equals(song.getSongData(), data)) // Check the new data != current data
        {
            song.setSongData(data);
        }

        if(listens != null && !Objects.equals(song.getListens(), listens)) // Check the new listens != current listens
        {
            song.setListens(listens);
        }

        if(likes != null && !Objects.equals(song.getLikes(), likes)) // Check the new likes != current likes
        {
            song.setLikes(likes);
        }

        if(status != null && !Objects.equals(song.getStatus(), status)) // Check the new likes != current likes
        {
            song.setStatus(status);
        }
    }

    public Song getActiveSongById(int id){
        Optional<Song> optionalSong = songRepository.getSongById(id);
        if(optionalSong.isPresent()){
            return optionalSong.get();
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song with id: " + id + " does not exists!");
        }
    }







    // TODO : Firebase Config
    String appCheckToken = null;

    public String getAppCheckToken() {
        try {
            appCheckToken = Arrays.toString(FirebaseAuth.getInstance(firebaseConfig.firebaseApp())
                    .createCustomToken("tunetowntest-e968a")
                    .getBytes());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return appCheckToken;
    }
    // Upload Image to Firebase
//    Part filePart = req.getPart("songImage");
//    String fileName = filePart.getSubmittedFileName();
//    InputStream fileContent;
//    {
//        try {
//            fileContent = filePart.getInputStream();
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//
//    Storage storage;
//    {
//        try {
//            storage = (Storage) StorageClient.getInstance(firebaseConfig.firebaseApp()).bucket("tunetowntest-6b63a.appspot.com").getStorage();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    BlobInfo blobInfo = BlobInfo.newBuilder("tunetown-6b63a.appspot.com", "images/" + fileName)
//                .setContentType(filePart.getContentType())
//                .setMetadata(ImmutableMap.of("firebaseStorageDownloadTokens", appCheckToken))
//                .build();

        // Upload the file to Firebase Storage
//        storage.create(blobInfo, fileContent,
//                Storage.BlobWriteOption.userProject("tunetown-6b63a"),
//                Storage.BlobWriteOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
//
//        // Construct the download URL manually
//        downloadUrlImage = "https://firebasestorage.googleapis.com/v0/b/" +
//                "tunetowntest-e968a.appspot.com" +
//                "/o/" +
//                "images%2F" + fileName +
//                "?alt=media" ;

    // Upload MP3 file to storage
//    Part filePart2 = req.getPart("songData");
//    String fileName2 = filePart2.getSubmittedFileName();
//    InputStream fileContent2;
//
//    {
//        try {
//            fileContent2 = filePart2.getInputStream();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//        Storage storage;
//
//    {
//        try {
//            storage = (Storage) StorageClient.getInstance(firebaseConfig.firebaseApp()).bucket("tunetowntest-e968a.appspot.com").getStorage();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    BlobInfo blobInfo = BlobInfo.newBuilder("tunetowntest-e968a.appspot.com", "audios/" + fileName2)
//                .setContentType(filePart2.getContentType())
//                .setMetadata(ImmutableMap.of("firebaseStorageDownloadTokens", appCheckToken))
//                .build();
//
//        // Upload the file to Firebase Storage
//        storage.create(blobInfo, fileContent2,
//                Storage.BlobWriteOption.userProject("tunetowntest-e968a"),
//                Storage.BlobWriteOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
//
//        // Encode the token using Base64 encoding
//        String encodedFilePath = URLEncoder.encode("audios/" + fileName2, "UTF-8");
//        downloadUrlData = "https://firebasestorage.googleapis.com/v0/b/" +
//                "tunetowntest-e968a.appspot.com" +
//                "/o/" +
//                encodedFilePath +
//                "?alt=media";
//
//        String token = UUID.randomUUID().toString();
//        String encodedToken = URLEncoder.encode(token, "UTF-8");
//        downloadUrlData = downloadUrlData + "&token=" + encodedToken;
}
