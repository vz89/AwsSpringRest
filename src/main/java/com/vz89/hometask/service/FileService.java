package com.vz89.hometask.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    boolean execute(MultipartFile multipartFile);
}
