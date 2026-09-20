package tn.IIT.mentorat_platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import tn.IIT.mentorat_platform.dto.request.TitreProfessionnelRequest;
import tn.IIT.mentorat_platform.dto.response.TitreProfessionnelResponse;
import tn.IIT.mentorat_platform.entity.TitreProfessionnel;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TitreMapper {

    TitreProfessionnel toEntity(TitreProfessionnelRequest request);

    TitreProfessionnelResponse toResponse(TitreProfessionnel entity);
}
