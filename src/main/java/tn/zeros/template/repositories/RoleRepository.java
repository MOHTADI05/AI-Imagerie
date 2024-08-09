package tn.zeros.template.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.zeros.template.entities.Role;
import tn.zeros.template.entities.enums.TypeRole;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByAuthority(String authority);
    Optional<Role> findByType(TypeRole type);

}
