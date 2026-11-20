package tn.itbs.collecte.services;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.collecte.entities.Capteur;
import tn.itbs.collecte.repositories.CapteurRepository;
import tn.itbs.collecte.repositories.ObservationRepository;

@Service
public class CapteurService {

	@Autowired
	private CapteurRepository capteurRepo;
	@Autowired
	private ObservationRepository observationRepo;

	public ResponseEntity<String> ajouter(Capteur c) {
		c.setId(null); // l'id est généré par la base
		Capteur saved = capteurRepo.save(c);
		return ResponseEntity.created(URI.create("/capteurs/" + saved.getId()))
				.body("Capteur ajouté avec l'id " + saved.getId());
	}

	public List<Capteur> lister() {
		return capteurRepo.findAll();
	}

	public Capteur chercherParId(Long id) {
		return capteurRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Capteur " + id + " introuvable"));
	}

	public List<Capteur> chercherParParcelle(Long parcelleId) {
		return capteurRepo.findByParcelleId(parcelleId);
	}

	public ResponseEntity<String> modifier(Long id, Capteur c) {
		capteurRepo.findById(id)
			.ifPresentOrElse(
				existant -> {
					existant.setType(c.getType());
					existant.setParcelleId(c.getParcelleId());
					existant.setLocalisation(c.getLocalisation());
					capteurRepo.save(existant);
				},
				() -> {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Capteur " + id + " introuvable");
				});
		return ResponseEntity.ok("Capteur mis à jour");
	}

	public ResponseEntity<String> supprimer(Long id) {
		Capteur c = chercherParId(id);
		// On conserve l'historique : un capteur qui a déjà des observations ne peut pas être supprimé
		if (observationRepo.existsByCapteurId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Le capteur " + id + " possède des observations : suppression refusée");
		}
		capteurRepo.delete(c);
		return ResponseEntity.ok("Capteur supprimé");
	}
}
