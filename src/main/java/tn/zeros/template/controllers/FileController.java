package tn.zeros.template.controllers;

import tn.zeros.template.entities.FileResponse;
import tn.zeros.template.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService service;

    // Upload an image file
    @PostMapping("/upload")
    public ResponseEntity<FileResponse> upload(@RequestPart("file") MultipartFile file) {
        try {
            FileResponse response = service.upload(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Process the uploaded file by its ID
    @PostMapping("/process/{fileId}")
    public ResponseEntity<String> process(@PathVariable Long fileId) {
        try {
            String extractionResult = service.processFile(fileId);
            return ResponseEntity.ok(extractionResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing file.");
        }
    }

    // Get all uploaded files

}
