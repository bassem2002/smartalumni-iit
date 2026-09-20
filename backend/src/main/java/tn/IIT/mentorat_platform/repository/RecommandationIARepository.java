package tn.IIT.mentorat_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.IIT.mentorat_platform.entity.RecommandationIA;

import java.util.List;

@Repository
public interface RecommandationIARepository extends JpaRepository<RecommandationIA, Long> {
    List<RecommandationIA> findByEtudiantIdOrderByScoreMatchingDesc(Long etudiantId);
    List<RecommandationIA> findByAlumniId(Long alumniId);
    void deleteByEtudiantId(Long etudiantId);
}
