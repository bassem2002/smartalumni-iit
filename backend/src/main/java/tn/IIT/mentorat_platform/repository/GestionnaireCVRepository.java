package tn.IIT.mentorat_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.GestionnaireCV;

import java.util.Optional;

@Repository
public interface GestionnaireCVRepository extends JpaRepository<GestionnaireCV, Long> {
    Optional<GestionnaireCV> findByUtilisateurId(Long utilisateurId);
    boolean existsByUtilisateurId(Long utilisateurId);
}
