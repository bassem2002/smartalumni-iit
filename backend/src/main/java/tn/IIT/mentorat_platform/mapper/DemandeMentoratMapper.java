package tn.IIT.mentorat_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import tn.IIT.mentorat_platform.dto.response.DemandeMentoratResponse;
import tn.IIT.mentorat_platform.entity.DemandeMentorat;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DemandeMentoratMapper {

    @Mapping(source = "etudiant.id", target = "etudiantId")
    @Mapping(source = "etudiant.nom", target = "etudiantNom")
    @Mapping(source = "etudiant.prenom", target = "etudiantPrenom")
    @Mapping(source = "etudiant.profil.photo", target = "etudiantPhoto")
    @Mapping(source = "alumni.id", target = "alumniId")
    @Mapping(source = "alumni.nom", target = "alumniNom")
    @Mapping(source = "alumni.prenom", target = "alumniPrenom")
    @Mapping(source = "alumni.profil.photo", target = "alumniPhoto")
    @Mapping(source = "alumni.posteActuel", target = "alumniPoste")
    @Mapping(source = "conversation.id", target = "conversationId")
    DemandeMentoratResponse toResponse(DemandeMentorat demande);
}
