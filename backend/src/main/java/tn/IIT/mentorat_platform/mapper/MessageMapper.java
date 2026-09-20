package tn.IIT.mentorat_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import tn.IIT.mentorat_platform.dto.response.MessageResponse;
import tn.IIT.mentorat_platform.entity.Message;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    @Mapping(source = "expediteur.id", target = "expediteurId")
    @Mapping(source = "expediteur.nom", target = "expediteurNom")
    @Mapping(source = "expediteur.prenom", target = "expediteurPrenom")
    @Mapping(source = "expediteur.profil.photo", target = "expediteurPhoto")
    MessageResponse toResponse(Message message);
}
