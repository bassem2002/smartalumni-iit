package tn.IIT.mentorat_platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.IIT.mentorat_platform.dto.request.AlumniFilterRequest;
import tn.IIT.mentorat_platform.dto.response.AlumniSummaryResponse;
import tn.IIT.mentorat_platform.dto.response.ProfilResponse;
import tn.IIT.mentorat_platform.entity.Alumni;
import tn.IIT.mentorat_platform.exception.ResourceNotFoundException;
import tn.IIT.mentorat_platform.mapper.AlumniMapper;
import tn.IIT.mentorat_platform.mapper.ProfilMapper;
import tn.IIT.mentorat_platform.repository.AlumniRepository;
import tn.IIT.mentorat_platform.service.AlumniService;
import tn.IIT.mentorat_platform.specification.AlumniSpecification;

@Service
@RequiredArgsConstructor
public class AlumniServiceImpl implements AlumniService {

    private final AlumniRepository alumniRepository;
    private final AlumniMapper alumniMapper;
    private final ProfilMapper profilMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AlumniSummaryResponse> searchAlumni(AlumniFilterRequest request) {
        if (request == null) {
            request = new AlumniFilterRequest();
        }

        int page = Math.max(0, request.getPage());
        int size = request.getSize() > 0 ? request.getSize() : 10;
        String sortBy = (request.getSortBy() != null && !request.getSortBy().trim().isEmpty()) ? request.getSortBy()
                : "nom";

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy).ascending());

        Specification<Alumni> spec = AlumniSpecification.withFilters(request);

        return alumniRepository.findAll(spec, pageable)
                .map(alumniMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfilResponse getAlumniDetails(Long alumniId) {
        Alumni alumni = alumniRepository.findById(alumniId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni", "id", alumniId));

        ProfilResponse response = profilMapper.toResponse(alumni.getProfil());

        // Ajouter les champs spécifiques à l'alumni qui ne sont pas dans le profil
        response.setSecteur(alumni.getSecteur());
        response.setPosteActuel(alumni.getPosteActuel());
        response.setEntreprise(alumni.getEntreprise());
        response.setPays(alumni.getPays());
        response.setDisponibleMentorat(alumni.getDisponibleMentorat());

        return response;
    }

    @Override
    @Transactional
    public void toggleDisponibiliteMentorat(Long alumniId) {
        Alumni alumni = alumniRepository.findById(alumniId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni", "id", alumniId));

        alumni.setDisponibleMentorat(!alumni.getDisponibleMentorat());
        alumniRepository.save(alumni);
    }

}
