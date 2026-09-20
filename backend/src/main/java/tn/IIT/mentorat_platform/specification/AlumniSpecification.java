package tn.IIT.mentorat_platform.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import tn.IIT.mentorat_platform.dto.request.AlumniFilterRequest;
import tn.IIT.mentorat_platform.entity.Alumni;

import java.util.ArrayList;
import java.util.List;

public class AlumniSpecification {

    public static Specification<Alumni> withFilters(AlumniFilterRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getSecteur() != null && !request.getSecteur().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("secteur")), request.getSecteur().toLowerCase()));
            }

            if (request.getPosteActuel() != null && !request.getPosteActuel().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("posteActuel")), request.getPosteActuel().toLowerCase()));
            }

            if (request.getPays() != null && !request.getPays().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("pays")), request.getPays().toLowerCase()));
            }

            if (request.getDisponibleMentorat() != null) {
                predicates.add(cb.equal(root.get("disponibleMentorat"), request.getDisponibleMentorat()));
            }

            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String likeKeyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nom")), likeKeyword),
                        cb.like(cb.lower(root.get("prenom")), likeKeyword),
                        cb.like(cb.lower(root.get("posteActuel")), likeKeyword)
                ));
            }

            // Exclure les comptes inactifs ou suspendus
            predicates.add(cb.equal(root.get("statutCompte"), tn.IIT.mentorat_platform.enums.StatutCompte.ACTIF));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
