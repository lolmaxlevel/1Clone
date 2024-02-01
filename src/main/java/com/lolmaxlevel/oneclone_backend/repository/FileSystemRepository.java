package com.lolmaxlevel.oneclone_backend.repository;

import org.springframework.core.io.FileSystemResource;

public interface FileSystemRepository {
    String save(byte[] content, String fileName) throws Exception;

    FileSystemResource findInFileSystem(String location);

    void deleteFile(FileSystemResource file);
}
