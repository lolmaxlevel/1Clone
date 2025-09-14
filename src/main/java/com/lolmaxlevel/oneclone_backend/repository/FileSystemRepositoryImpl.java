package com.lolmaxlevel.oneclone_backend.repository;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;


@Repository
public class FileSystemRepositoryImpl implements FileSystemRepository{

    //TODO change to config
    String RESOURCES_DIR = "C:\\aboba\\files\\";

    public String save(byte[] content, String fileName) throws IOException {
        Path newFile = Paths.get(RESOURCES_DIR + new Date().getTime() + "-" + fileName);
        Optional.ofNullable(newFile.getParent()).ifPresent(parent -> {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new RuntimeException("Could not create directories for file", e);
            }
        });

        Files.write(newFile, content);

        return newFile.toAbsolutePath()
                .toString();
    }

    public FileSystemResource findInFileSystem(String location) {
        return new FileSystemResource(Paths.get(location));
    }

    public void deleteFile(FileSystemResource file){
        try {
            Files.delete(Paths.get(file.getPath()));
        } catch (IOException e) {
            // Handle access or file not found problems.
            throw new RuntimeException();
        }
    }
}