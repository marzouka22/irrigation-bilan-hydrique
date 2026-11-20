package tn.itbs.analyse.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.analyse.calcul.DonneesClimatiques;
import tn.itbs.analyse.calcul.DonneesInsuffisantesException;
import tn.itbs.analyse.calcul.ResultatCalcul;
import tn.itbs.analyse.config.BilanHydriqueCalculateur;
import tn.itbs.analyse.dtos.DemandeCalcul;
import tn.itbs.analyse.dtos.DemandeComparaison;
import tn.itbs.analyse.dtos.ResultatComparaison;
import tn.itbs.analyse.entities.Fiabilite;
import tn.itbs.analyse.entities.Modele;
import tn.itbs.analyse.entities.Parcelle;
import tn.itbs.analyse.entities.Recommandation;
import tn.itbs.analyse.repositories.RecommandationRepository;

@Service
public class RecommandationService {

	@Autowired
	private RecommandationRepository recommandationRepo;
	@Autowired
	private ParcelleService parcelleService;
	@Autowired
	private ModeleService modeleService;
	@Autowired
	private BilanHydriqueCalculateur calculateur;

	// Calcule une recommandation avec un modèle (celui par défaut si aucun n'est précisé) et l'enregistre
	public Recommandation calculer(DemandeCalcul demande) {
		Parcelle parcelle = parcelleService.chercherParId(demande.parcelleId());
		Modele modele = demande.modeleId() == null
				? modeleService.modeleParDefaut()
				: modeleService.chercherParId(demande.modeleId());
		verifier(demande.donnees());

		ResultatCalcul r = calculateur.pourVersion(modele.getVersion())
				.calculer(parcelle.versParametres(), demande.donnees());

		Recommandation reco = new Recommandation(null, parcelle.getId(), r.quantiteEauM3(), r.doseBruteMm(),
				r.justification(), LocalDateTime.now(), modele.getId(), modele.getVersion(), Fiabilite.NORMALE);
		return recommandationRepo.save(reco);
	}

	// Applique tous les modèles aux mêmes données, sans rien enregistrer
	public List<ResultatComparaison> comparer(DemandeComparaison demande) {
		Parcelle parcelle = parcelleService.chercherParId(demande.parcelleId());
		verifier(demande.donnees());

		List<ResultatComparaison> resultats = new ArrayList<>();
		for (Modele m : modeleService.lister()) {
			try {
				ResultatCalcul r = calculateur.pourVersion(m.getVersion())
						.calculer(parcelle.versParametres(), demande.donnees());
				resultats.add(new ResultatComparaison(m.getId(), m.getVersion(), m.getPrecision(), r, null));
			} catch (DonneesInsuffisantesException e) {
				// Un modèle inapplicable n'empêche pas la comparaison des autres
				resultats.add(new ResultatComparaison(m.getId(), m.getVersion(), m.getPrecision(), null, e.getMessage()));
			}
		}
		return resultats;
	}

	public List<Recommandation> lister() {
		return recommandationRepo.findAllByOrderByDateCalculDesc();
	}

	public Recommandation chercherParId(Long id) {
		return recommandationRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Recommandation " + id + " introuvable"));
	}

	public List<Recommandation> parParcelle(Long parcelleId) {
		parcelleService.chercherParId(parcelleId); // 404 si la parcelle n'existe pas
		return recommandationRepo.findByParcelleIdOrderByDateCalculDesc(parcelleId);
	}

	// Contrôle de cohérence des données climatiques (400 si incohérentes)
	static void verifier(DonneesClimatiques d) {
		if (d.pluie() != null && (d.pluie() < 0 || d.pluie() > 500)) {
			throw badRequest("La pluie doit être comprise entre 0 et 500 mm");
		}
		if (d.et0() != null && (d.et0() < 0 || d.et0() > 20)) {
			throw badRequest("L'ET0 doit être comprise entre 0 et 20 mm/j");
		}
		if ((d.temperatureMin() != null && (d.temperatureMin() < -30 || d.temperatureMin() > 60))
				|| (d.temperatureMax() != null && (d.temperatureMax() < -30 || d.temperatureMax() > 60))) {
			throw badRequest("Les températures doivent être comprises entre -30 et 60 °C");
		}
		if (d.temperatureMin() != null && d.temperatureMax() != null && d.temperatureMax() < d.temperatureMin()) {
			throw badRequest("La température maximale doit être supérieure ou égale à la minimale");
		}
		if (d.date() != null && d.date().isAfter(LocalDate.now())) {
			throw badRequest("La date ne peut pas être dans le futur");
		}
	}

	private static ResponseStatusException badRequest(String message) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
	}
}
