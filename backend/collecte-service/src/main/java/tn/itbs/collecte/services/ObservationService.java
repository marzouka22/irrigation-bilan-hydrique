package tn.itbs.collecte.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.collecte.dtos.ObservationDetails;
import tn.itbs.collecte.entities.Capteur;
import tn.itbs.collecte.entities.Observation;
import tn.itbs.collecte.entities.TypeCapteur;
import tn.itbs.collecte.repositories.CapteurRepository;
import tn.itbs.collecte.repositories.ObservationRepository;

@Service
public class ObservationService {

	public static final int HEURES_MAX = 720; // 30 jours

	@Autowired
	private ObservationRepository observationRepo;
	@Autowired
	private CapteurRepository capteurRepo;
	@Autowired
	private CapteurService capteurService;

	public ResponseEntity<String> ajouter(Observation o) {
		Capteur capteur = capteurService.chercherParId(o.getCapteurId()); // 404 si inconnu

		TypeCapteur type = capteur.getType();
		if (!type.valeurValide(o.getValeur())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Valeur " + o.getValeur() + " hors plage pour un capteur " + type
					+ " [" + type.getMin() + " ; " + type.getMax() + "] " + type.getUnite());
		}

		o.setId(null);
		o.setUnite(type.getUnite()); // l'unité découle toujours du type du capteur
		if (o.getDate() == null) {
			o.setDate(LocalDateTime.now());
		}
		Observation saved = observationRepo.save(o);
		return ResponseEntity.created(URI.create("/observations/" + saved.getId()))
				.body("Observation ajoutée avec l'id " + saved.getId());
	}

	public List<Observation> lister() {
		return observationRepo.findAll(Sort.by(Sort.Direction.DESC, "date"));
	}

	public Observation chercherParId(Long id) {
		return observationRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Observation " + id + " introuvable"));
	}

	public List<Observation> parCapteur(Long capteurId) {
		capteurService.chercherParId(capteurId); // 404 si le capteur n'existe pas
		return observationRepo.findByCapteurIdOrderByDateDesc(capteurId);
	}

	// Dernières observations d'une parcelle (tous ses capteurs) sur les N dernières heures
	public List<ObservationDetails> dernieresParParcelle(Long parcelleId, int heures) {
		if (heures < 1 || heures > HEURES_MAX) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Le paramètre heures doit être compris entre 1 et " + HEURES_MAX);
		}

		Map<Long, Capteur> capteurs = capteurRepo.findByParcelleId(parcelleId).stream()
				.collect(Collectors.toMap(Capteur::getId, Function.identity()));
		if (capteurs.isEmpty()) {
			return List.of();
		}

		LocalDateTime depuis = LocalDateTime.now().minusHours(heures);
		return observationRepo.findByCapteurIdInAndDateAfterOrderByDateDesc(capteurs.keySet(), depuis)
				.stream()
				.map(o -> ObservationDetails.of(o, capteurs.get(o.getCapteurId())))
				.toList();
	}
}
