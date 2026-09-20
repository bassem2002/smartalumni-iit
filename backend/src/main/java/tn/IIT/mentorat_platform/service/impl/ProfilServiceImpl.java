package tn.IIT.mentorat_platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.IIT.mentorat_platform.dto.request.ProfilUpdateRequest;
import tn.IIT.mentorat_platform.dto.response.ProfilResponse;
import tn.IIT.mentorat_platform.entity.Alumni;
import tn.IIT.mentorat_platform.entity.Etudiant;
import tn.IIT.mentorat_platform.entity.ProfilUtilisateur;
import tn.IIT.mentorat_platform.entity.Utilisateur;
import tn.IIT.mentorat_platform.exception.ResourceNotFoundException;
import tn.IIT.mentorat_platform.mapper.ProfilMapper;
import tn.IIT.mentorat_platform.repository.ProfilUtilisateurRepository;
import tn.IIT.mentorat_platform.repository.UtilisateurRepository;
import tn.IIT.mentorat_platform.service.ProfilService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfilServiceImpl implements ProfilService {

    private final UtilisateurRepository utilisateurRepository;
    private final ProfilUtilisateurRepository profilUtilisateurRepository;
    private final tn.IIT.mentorat_platform.repository.TitreProfessionnelRepository titreProfessionnelRepository;
    private final ProfilMapper profilMapper;
    private final tn.IIT.mentorat_platform.mapper.TitreMapper titreMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public ProfilResponse getMyProfil(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        ProfilUtilisateur profil = profilUtilisateurRepository.findByUtilisateurId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil non trouvé pour l'utilisateur: " + email));

        ProfilResponse response = profilMapper.toResponse(profil);

        if (user instanceof Alumni alumni) {
            response.setSecteur(alumni.getSecteur());
            response.setPosteActuel(alumni.getPosteActuel());
            response.setEntreprise(alumni.getEntreprise());
            response.setPays(alumni.getPays());
            response.setDisponibleMentorat(alumni.getDisponibleMentorat());
            response.setDemandeStatut(alumni.getDemandeStatut() != null ? alumni.getDemandeStatut().name() : null);
        } else if (user instanceof Etudiant etudiant) {
            response.setNiveauEtude(etudiant.getNiveauEtude());
            response.setSpecialite(etudiant.getSpecialite());
            response.setAnneePromotion(etudiant.getAnneePromotion());
        }

        return response;
    }

    @Override
    @Transactional
    public ProfilResponse updateProfil(String email, ProfilUpdateRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        ProfilUtilisateur profil = profilUtilisateurRepository.findByUtilisateurId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil non trouvé pour l'utilisateur: " + email));

        profilMapper.updateProfilFromRequest(request, profil);
        profilUtilisateurRepository.save(profil);

        if (user instanceof Alumni alumni) {
            if (request.getSecteur() != null) alumni.setSecteur(request.getSecteur());
            if (request.getPosteActuel() != null) alumni.setPosteActuel(request.getPosteActuel());
            if (request.getEntreprise() != null) alumni.setEntreprise(request.getEntreprise());
            if (request.getPays() != null) alumni.setPays(request.getPays());
            utilisateurRepository.save(alumni);
        } else if (user instanceof Etudiant etudiant) {
            if (request.getNiveauEtude() != null) etudiant.setNiveauEtude(request.getNiveauEtude());
            if (request.getSpecialite() != null) etudiant.setSpecialite(request.getSpecialite());
            if (request.getAnneePromotion() != null) etudiant.setAnneePromotion(request.getAnneePromotion());
            utilisateurRepository.save(etudiant);
        }

        return getMyProfil(email);
    }

    @Override
    @Transactional
    public ProfilResponse addTitre(String email, tn.IIT.mentorat_platform.dto.request.TitreProfessionnelRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        ProfilUtilisateur profil = profilUtilisateurRepository.findByUtilisateurId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Profil non trouvé"));

        tn.IIT.mentorat_platform.entity.TitreProfessionnel titre = titreMapper.toEntity(request);
        titre.setProfil(profil);
        titre.setStatutVerification(tn.IIT.mentorat_platform.enums.StatutVerification.EN_ATTENTE);
        titreProfessionnelRepository.save(titre);

        return getMyProfil(email);
    }

    @Override
    @Transactional
    public ProfilResponse deleteTitre(String email, Long titreId) {
        Utilisateur user = utilisateurRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        ProfilUtilisateur profil = profilUtilisateurRepository.findByUtilisateurId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Profil non trouvé"));

        tn.IIT.mentorat_platform.entity.TitreProfessionnel titre = titreProfessionnelRepository.findById(titreId)
                .orElseThrow(() -> new ResourceNotFoundException("Titre", "id", titreId));

        if (!titre.getProfil().getId().equals(profil.getId())) {
            throw new tn.IIT.mentorat_platform.exception.UnauthorizedException("Accès refusé");
        }

        titreProfessionnelRepository.delete(titre);
        return getMyProfil(email);
    }

    @Override
    @Transactional
    public void changePassword(String email, tn.IIT.mentorat_platform.dto.request.ChangePasswordRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getMotDePasse())) {
            throw new tn.IIT.mentorat_platform.exception.BusinessException("Ancien mot de passe incorrect");
        }
        user.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        utilisateurRepository.save(user);
    }

    @Override
    @Transactional
    public void soumettreDemandeDevenir(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        if (user instanceof Alumni alumni) {
            alumni.setDemandeStatut(tn.IIT.mentorat_platform.enums.StatutDemandeMentorat.EN_ATTENTE);
            utilisateurRepository.save(alumni);
        }
    }
}
