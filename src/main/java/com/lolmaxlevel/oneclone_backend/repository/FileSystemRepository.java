package com.lolmaxlevel.oneclone_backend.repository;

import org.springframework.core.io.FileSystemResource;

import java.io.IOException;

public interface FileSystemRepository {
    String save(byte[] content, String fileName) throws IOException;

    FileSystemResource findInFileSystem(String location);

    void deleteFile(FileSystemResource file);
}
