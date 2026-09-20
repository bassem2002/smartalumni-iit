package tn.IIT.mentorat_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.Alumni;

@Repository
public interface AlumniRepository extends JpaRepository<Alumni, Long>,
        JpaSpecificationExecutor<Alumni> {

    Page<Alumni> findByDisponibleMentorat(Boolean disponible, Pageable pageable);
    Page<Alumni> findBySecteur(String secteur, Pageable pageable);
    Page<Alumni> findByPays(String pays, Pageable pageable);
    Page<Alumni> findByDisponibleMentoratAndSecteur(Boolean disponible, String secteur, Pageable pageable);
    long countByDisponibleMentorat(Boolean disponible);
    java.util.List<Alumni> findByDemandeStatut(tn.IIT.mentorat_platform.enums.StatutDemandeMentorat statut);
    java.util.List<Alumni> findByDisponibleMentorat(Boolean disponible);
}
