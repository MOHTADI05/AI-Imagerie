package tn.zeros.template.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class FileRequest {
    private MultipartFile file;
}
