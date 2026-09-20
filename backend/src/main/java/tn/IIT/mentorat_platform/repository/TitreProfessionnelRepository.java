package tn.IIT.mentorat_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.TitreProfessionnel;
import tn.IIT.mentorat_platform.enums.StatutVerification;

import java.util.List;

@Repository
public interface TitreProfessionnelRepository extends JpaRepository<TitreProfessionnel, Long> {
    List<TitreProfessionnel> findByProfilId(Long profilId);
    List<TitreProfessionnel> findByStatutVerification(StatutVerification statut);
    long countByStatutVerification(StatutVerification statut);
}
