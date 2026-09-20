package tn.IIT.mentorat_platform.service;

import org.springframework.data.domain.Page;
import tn.IIT.mentorat_platform.dto.request.AlumniFilterRequest;
import tn.IIT.mentorat_platform.dto.response.AlumniSummaryResponse;
import tn.IIT.mentorat_platform.dto.response.ProfilResponse;

public interface AlumniService {

    Page<AlumniSummaryResponse> searchAlumni(AlumniFilterRequest request);

    ProfilResponse getAlumniDetails(Long alumniId);

    void toggleDisponibiliteMentorat(Long alumniId);
}
