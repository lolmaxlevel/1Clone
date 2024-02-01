package com.lolmaxlevel.oneclone_backend.controller;


import com.lolmaxlevel.oneclone_backend.dto.ResponseMessage;
import com.lolmaxlevel.oneclone_backend.dto.UploadResponse;
import com.lolmaxlevel.oneclone_backend.model.File;
import com.lolmaxlevel.oneclone_backend.service.FileLocationService;
import com.lolmaxlevel.oneclone_backend.utils.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("api/files")
@CrossOrigin(origins = "http://localhost:5173")
class FileController {

    private final FileLocationService fileLocationService;

    public FileController(FileLocationService fileLocationService) {
        this.fileLocationService = fileLocationService;
    }

    @PostMapping("/upload")
    UploadResponse uploadImage(@RequestParam("file") MultipartFile file,
                               @RequestParam("owner") Long owner) {
        log.info("Upload request: {}", file.getOriginalFilename());
        try {
            File new_file = fileLocationService.save(file.getBytes(), file.getOriginalFilename(), owner);
            return new UploadResponse("Uploaded the file successfully!",
                    new_file.getName(),new_file.getId());
        } catch (Exception e) {
            return new UploadResponse("Error: " + e.getMessage());
        }
    }


    @GetMapping(value = "/download/{fileId}")
    FileSystemResource downloadFile(@PathVariable Long fileId, HttpServletResponse response) {
        log.info("Download request: {}", fileId);
        String fileName =
                Objects.requireNonNull(fileLocationService.find(fileId).getFilename()).split("-", 2)[1];
        fileName = StringUtils.convertCyrilic(fileName.toLowerCase());
        response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
        return fileLocationService.find(fileId);
    }
//
//    @GetMapping(value = "/all-files")
//    File[] getAllFiles() {
//        log.info("Get all files request");
//        return fileLocationService.getAllFiles();
//    }
//
    @PostMapping(value = "/delete-file")
    ResponseMessage deleteFile(@RequestParam Long id) {
        log.info("Delete request: {}", id);
        return new ResponseMessage(fileLocationService.deleteFile(id));
    }

}