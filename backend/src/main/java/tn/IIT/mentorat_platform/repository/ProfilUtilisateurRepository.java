package tn.IIT.mentorat_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.ProfilUtilisateur;

import java.util.Optional;

@Repository
public interface ProfilUtilisateurRepository extends JpaRepository<ProfilUtilisateur, Long> {
    Optional<ProfilUtilisateur> findByUtilisateurId(Long utilisateurId);
    boolean existsByUtilisateurId(Long utilisateurId);
}
