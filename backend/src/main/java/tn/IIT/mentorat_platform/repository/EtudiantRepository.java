package tn.IIT.mentorat_platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.Etudiant;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long>,
        JpaSpecificationExecutor<Etudiant> {

    Page<Etudiant> findBySpecialite(String specialite, Pageable pageable);
    Page<Etudiant> findByNiveauEtude(String niveauEtude, Pageable pageable);
}
