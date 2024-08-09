package tn.zeros.template.repositories;

import tn.zeros.template.entities.FileResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileResponseRepository extends JpaRepository<FileResponse, Long> {
}
