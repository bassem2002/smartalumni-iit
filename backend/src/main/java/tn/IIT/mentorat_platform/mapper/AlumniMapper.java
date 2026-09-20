package tn.IIT.mentorat_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import tn.IIT.mentorat_platform.dto.response.AlumniSummaryResponse;
import tn.IIT.mentorat_platform.entity.Alumni;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlumniMapper {

    @Mapping(source = "profil.photo", target = "photo")
    @Mapping(source = "profil.bio", target = "bio")
    @Mapping(source = "profil.competences", target = "competences")
    @Mapping(target = "nombreDemandesRecues", expression = "java((long) alumni.getDemandesRecues().size())")
    AlumniSummaryResponse toSummaryResponse(Alumni alumni);
}
