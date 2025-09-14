package com.lolmaxlevel.oneclone_backend.service;

import com.lolmaxlevel.oneclone_backend.model.File;
import com.lolmaxlevel.oneclone_backend.repository.EmployeeRepository;
import com.lolmaxlevel.oneclone_backend.repository.FileDbRepository;
import com.lolmaxlevel.oneclone_backend.repository.FileSystemRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
public class FileLocationService {
    private final FileSystemRepositoryImpl fileSystemRepositoryImpl;
    private final FileDbRepository fileDbRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public FileLocationService(FileSystemRepositoryImpl fileSystemRepositoryImpl,
                               FileDbRepository fileDbRepository,
                               EmployeeRepository employeeRepository) {
        this.fileSystemRepositoryImpl = fileSystemRepositoryImpl;
        this.fileDbRepository = fileDbRepository;

        this.employeeRepository = employeeRepository;
    }

    public File save(byte[] bytes, String fileName, Long owner) throws IOException {
        String location = fileSystemRepositoryImpl.save(bytes, fileName);

        return fileDbRepository.save(new File(fileName, location, employeeRepository.findById(owner).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))));
    }

    public FileSystemResource find(Long fileId) {
        File file = fileDbRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        return fileSystemRepositoryImpl.findInFileSystem(file.getUri());
    }

    public String deleteFile(Long id) {
        fileSystemRepositoryImpl.deleteFile(this.find(id));
        fileDbRepository.deleteById(id);
        return "Deleted";
    }
}