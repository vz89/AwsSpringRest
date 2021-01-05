package com.vz89.hometask.controller;

import com.vz89.hometask.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/file")
    public ResponseEntity<?> postFile(@RequestParam("file") MultipartFile multipartFile) {
        final boolean execute = fileService.execute(multipartFile);
        if (execute) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("file not valid", HttpStatus.BAD_REQUEST);
        }
    }
}
