package com.tunetown.controller;

import com.tunetown.service.FirebaseStorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@Slf4j
public class FirebaseController {
    @Resource
    FirebaseStorageService firebaseStorageService;

    @PostMapping(path = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadImage(@RequestParam(name = "image") MultipartFile image) throws IOException {
        return firebaseStorageService.uploadImage(image, image.getOriginalFilename());
    }

    @PostMapping(path = "/uploadMp3", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadMp3(@RequestParam(name = "mp3File") MultipartFile mp3File) throws IOException {
        return firebaseStorageService.uploadMp3(mp3File, mp3File.getOriginalFilename(), 10);
    }
}
