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
public class FileRestControllerV1 {
    private final FileService fileService;

    @PostMapping("v1/files")
    public ResponseEntity<?> postFile(@RequestParam("file") MultipartFile multipartFile) {
        final String execute = fileService.execute(multipartFile);
        if (execute.contains("not valid"))
            return new ResponseEntity<>("file not valid", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(execute, HttpStatus.OK);
    }
}
