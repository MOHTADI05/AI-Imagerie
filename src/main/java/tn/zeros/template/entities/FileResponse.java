package tn.zeros.template.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Files")
public class FileResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private long fileSize;
    private String fileType;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String jsonResponse;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
