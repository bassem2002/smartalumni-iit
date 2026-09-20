package tn.IIT.mentorat_platform.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class FormationScraperService {

    private static final String IIT_URL = "https://iit.tn/";

    /**
     * Retourne une map contenant les filières comme clés (ex: "Filières Ingénieur", "Licence")
     * et la liste de leurs spécialités comme valeurs.
     */
    // @Cacheable("formations") // Si vous avez activé le cache Spring, vous pouvez décommenter
    public Map<String, List<String>> getFormationsFromSite() {
        Map<String, List<String>> formations = new LinkedHashMap<>();

        try {
            // 1. Se connecter au site
            Document doc = Jsoup.connect(IIT_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10000)
                    .get();

            // 2. Chercher le menu Formation (basé sur le lien href)
            Elements formationLinks = doc.select("a[href*='formation/'], a:containsOwn(FORMATION), a:containsOwn(Formation)");
            
            if (!formationLinks.isEmpty()) {
                Element formationMenu = formationLinks.first().parent();
                
                // Si c'est un menu déroulant typique WordPress (li > ul > li)
                if (formationMenu != null) {
                    Elements subMenus = formationMenu.select("ul.sub-menu > li");
                    
                    for (Element subMenu : subMenus) {
                        Element categoryLink = subMenu.selectFirst("> a");
                        if (categoryLink != null) {
                            String categoryName = categoryLink.text().trim();
                            
                            // Chercher les sous-catégories (spécialités)
                            Elements specialtyLinks = subMenu.select("ul.sub-menu > li > a");
                            List<String> specialties = new ArrayList<>();
                            
                            for (Element specLink : specialtyLinks) {
                                String specName = specLink.text().trim();
                                if (!specName.isEmpty()) {
                                    specialties.add(specName);
                                }
                            }
                            
                            // Ajouter uniquement si la catégorie a un nom
                            if (!categoryName.isEmpty()) {
                                // Si pas de sous-menu, on la considère comme une spécialité d'elle-même (ex: Architecture)
                                if (specialties.isEmpty()) {
                                    specialties.add(categoryName);
                                }
                                formations.put(categoryName, specialties);
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            log.error("Erreur lors du scraping du site IIT : {}", e.getMessage());
        }

        // 3. Fallback : Si le scraping échoue ou que la structure du site a changé,
        // on retourne les valeurs par défaut pour ne pas bloquer les inscriptions.
        if (formations.isEmpty()) {
            log.warn("Le scraping a échoué ou retourné un résultat vide. Utilisation des formations par défaut.");
            return getDefaultFormations();
        }

        return formations;
    }

    private Map<String, List<String>> getDefaultFormations() {
        Map<String, List<String>> fallback = new LinkedHashMap<>();
        
        fallback.put("Filières Ingénieur", Arrays.asList(
                "Génie Informatique", 
                "Génie Civil", 
                "Génie des procédés", 
                "Génie Mécanique", 
                "Génie Industriel"
        ));
        fallback.put("Licence", Arrays.asList(
                "Licence Génie Logiciel et Système d’Information", 
                "Licence en Management des Systèmes Industriels"
        ));
        fallback.put("Mastère", Collections.singletonList("Mastère"));
        fallback.put("Architecture", Collections.singletonList("Architecture"));
        fallback.put("Cycle Préparatoire", Collections.singletonList("Cycle Préparatoire"));
        
        return fallback;
    }
}
