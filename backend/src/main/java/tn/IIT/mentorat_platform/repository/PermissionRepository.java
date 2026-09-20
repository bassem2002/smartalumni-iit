package tn.IIT.mentorat_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.Permission;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCodeAction(String codeAction);
    boolean existsByCodeAction(String codeAction);
}
