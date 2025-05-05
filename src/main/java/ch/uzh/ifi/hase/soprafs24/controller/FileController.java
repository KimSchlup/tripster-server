package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.ifi.hase.soprafs24.service.GoogleCloudStorageService;

@RestController
public class FileController {

    private final GoogleCloudStorageService storageService;

    @Value("mapmates-object-store")
    private String bucketName;

    public FileController(GoogleCloudStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/images")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        try {
            String fileUrl = storageService.uploadFile(file, bucketName);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName,
            @RequestHeader("Authorization") String token) {
        byte[] fileContent = storageService.downloadFile(bucketName, fileName);
        return ResponseEntity.ok(fileContent);
    }

    @DeleteMapping("/images/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName,
            @RequestHeader("Authorization") String token) {
        storageService.deleteFile(bucketName, fileName);
        return ResponseEntity.ok("File deleted successfully.");
    }
}