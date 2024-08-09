package tn.zeros.template.services;

import org.springframework.web.client.RestTemplate;
import tn.zeros.template.entities.ExtractedData;
import tn.zeros.template.entities.FileResponse;
import tn.zeros.template.entities.User;
import tn.zeros.template.repositories.FileResponseRepository;
import tn.zeros.template.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileResponseRepository repository;
    private final UserRepository userRepository;

    private static final String FILE_UPLOAD_PATH = "C:/Users/user/IdeaProjects/AI-Imagerie-Rayhana/FileUploads";
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100 MB
    private static final String PYTHON_API_URL = "http://localhost:5000/extract";

    @Transactional
    public FileResponse upload(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new IllegalArgumentException("Only JPG, PDF, and PNG files are allowed.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 100 MB.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = generateShortUUID() + "_" + truncateFileName(originalFileName);

        Path filePath = Paths.get(FILE_UPLOAD_PATH, uniqueFileName);
        Files.copy(file.getInputStream(), filePath);

        FileResponse response = new FileResponse();
        response.setFileName(uniqueFileName);
        response.setFileSize(file.getSize());
        response.setFileType(contentType);
        response.setUser(user);
        repository.save(response);

        return response;
    }

    @Transactional
    public String processFile(Long fileId) {
        FileResponse fileResponse = repository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String extractionResult = callPythonApi(fileResponse.getFileName());
        fileResponse.setJsonResponse(extractionResult);
        repository.save(fileResponse);

        return extractionResult;
    }

    public String callPythonApi(String fileName) {
        RestTemplate restTemplate = new RestTemplate();
        String url = PYTHON_API_URL;
        Map<String, String> request = new HashMap<>();
        request.put("filePath", FILE_UPLOAD_PATH + "/" + fileName);

        try {
            // Expecting a raw JSON response
            String response = restTemplate.postForObject(url, request, String.class);

            // Optionally, you can parse this JSON response into ExtractedData objects if needed
            // ExtractedData[] data = new ObjectMapper().readValue(response, ExtractedData[].class);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Python API", e);
        }
    }

    private boolean isValidFileType(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("application/pdf") || contentType.equals("image/png"));
    }

    private String generateShortUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8);
    }

    private String truncateFileName(String fileName) {
        if (fileName.length() > 100) {
            int extensionIndex = fileName.lastIndexOf('.');
            String extension = (extensionIndex > 0) ? fileName.substring(extensionIndex) : "";
            fileName = fileName.substring(0, 100 - extension.length()) + extension;
        }
        return fileName;
    }
}
