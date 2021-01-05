package com.vz89.hometask.service;

import lombok.RequiredArgsConstructor;
import net.lingala.zip4j.ZipFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AmazonClient amazonClient;

    public static final String APPLICATION_PATH_FOLDER = "target/classes/zipFile.tmp";
    public static final String APPLICATION_FOLDER = "/zipFile.tmp";

    private void convertMultiPartToArchive(MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile);) {
            fos.write(file.getBytes());
            new ZipFile(convFile).extractAll(APPLICATION_PATH_FOLDER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(MultipartFile multipartFile) {
        convertMultiPartToArchive(multipartFile);
        if (!validateArchive()) return false;
        postToAwsS3();
        return true;
    }

    private void postToAwsS3() {
        try (Stream<Path> paths = Files.walk(Paths.get(getUri()))) {
            String finalDirPath = Paths.get(getUri()).toFile().getPath() + "\\";
            System.out.println(finalDirPath);
            paths.forEach(p -> {
                if (p.toFile().isFile()) {
                    String fileName = p.toAbsolutePath().toString().replace(finalDirPath, "");
                    amazonClient.uploadFileTos3bucket(fileName.replace("\\", "/"), p.toFile());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateArchive() {
        AtomicBoolean hasPomFile = new AtomicBoolean(false);
        AtomicBoolean hasDockerFile = new AtomicBoolean(false);
        try (Stream<Path> paths = Files.walk(Paths.get(getUri()))) {
            paths.forEach(p -> {
                if (p.getFileName().toString().contains("pom.xml")) hasPomFile.set(true);
                if (p.getFileName().toString().contains("Dockerfile")) hasDockerFile.set(true);

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return hasPomFile.get() || hasDockerFile.get();
    }

    private URI getUri() {
        try {
            return FileServiceImpl.class.getResource(APPLICATION_FOLDER).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
