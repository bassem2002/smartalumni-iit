package tn.IIT.mentorat_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import tn.IIT.mentorat_platform.dto.request.ProfilUpdateRequest;
import tn.IIT.mentorat_platform.dto.response.ProfilResponse;
import tn.IIT.mentorat_platform.entity.ProfilUtilisateur;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfilMapper {

    @Mapping(source = "utilisateur.id", target = "id")
    @Mapping(source = "utilisateur.nom", target = "nom")
    @Mapping(source = "utilisateur.prenom", target = "prenom")
    @Mapping(source = "utilisateur.email", target = "email")
    @Mapping(source = "type", target = "typeUser")
    ProfilResponse toResponse(ProfilUtilisateur profil);

    void updateProfilFromRequest(ProfilUpdateRequest request, @MappingTarget ProfilUtilisateur profil);
}
