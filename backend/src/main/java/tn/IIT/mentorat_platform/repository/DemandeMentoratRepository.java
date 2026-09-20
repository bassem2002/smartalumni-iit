package tn.IIT.mentorat_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.DemandeMentorat;
import tn.IIT.mentorat_platform.enums.StatutDemande;

import java.util.List;

@Repository
public interface DemandeMentoratRepository extends JpaRepository<DemandeMentorat, Long> {

    List<DemandeMentorat> findByEtudiantId(Long etudiantId);
    List<DemandeMentorat> findByAlumniId(Long alumniId);
    List<DemandeMentorat> findByEtudiantIdAndStatut(Long etudiantId, StatutDemande statut);
    List<DemandeMentorat> findByAlumniIdAndStatut(Long alumniId, StatutDemande statut);

    boolean existsByEtudiantIdAndAlumniIdAndStatut(Long etudiantId, Long alumniId, StatutDemande statut);
    boolean existsByEtudiantIdAndAlumniId(Long etudiantId, Long alumniId);

    @Query("SELECT COUNT(d) FROM DemandeMentorat d WHERE d.statut = :statut")
    long countByStatut(StatutDemande statut);
}
