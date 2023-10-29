package com.tunetown.controller;

import com.tunetown.service.FirebaseStorageService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FirebaseController {
    @Resource
    FirebaseStorageService firebaseStorageService;

    @PostMapping(path = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadImage(@RequestBody MultipartFile image) throws IOException {
        return firebaseStorageService.uploadImage(image, image.getOriginalFilename());
    }

    @PostMapping(path = "/uploadMp3", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadMp3(@RequestBody MultipartFile mp3File) throws IOException {
        return firebaseStorageService.uploadMp3(mp3File, mp3File.getOriginalFilename(), 3);
    }
}
