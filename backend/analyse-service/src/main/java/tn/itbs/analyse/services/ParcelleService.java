package tn.itbs.analyse.services;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.analyse.entities.Parcelle;
import tn.itbs.analyse.repositories.ParcelleRepository;
import tn.itbs.analyse.repositories.RecommandationRepository;

@Service
public class ParcelleService {

	@Autowired
	private ParcelleRepository parcelleRepo;
	@Autowired
	private RecommandationRepository recommandationRepo;

	public ResponseEntity<String> ajouter(Parcelle p) {
		p.setId(null);
		if (p.getKc() == null) {
			p.setKc(p.getCulture().getKc()); // Kc de référence de la culture
		}
		Parcelle saved = parcelleRepo.save(p);
		return ResponseEntity.created(URI.create("/parcelles/" + saved.getId()))
				.body("Parcelle ajoutée avec l'id " + saved.getId());
	}

	public List<Parcelle> lister() {
		return parcelleRepo.findAll();
	}

	public Parcelle chercherParId(Long id) {
		return parcelleRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Parcelle " + id + " introuvable"));
	}

	public ResponseEntity<String> modifier(Long id, Parcelle p) {
		parcelleRepo.findById(id)
			.ifPresentOrElse(
				existante -> {
					existante.setNom(p.getNom());
					existante.setCulture(p.getCulture());
					existante.setKc(p.getKc() != null ? p.getKc() : p.getCulture().getKc());
					existante.setSurfaceHa(p.getSurfaceHa());
					existante.setLatitude(p.getLatitude());
					existante.setSystemeIrrigation(p.getSystemeIrrigation());
					parcelleRepo.save(existante);
				},
				() -> {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcelle " + id + " introuvable");
				});
		return ResponseEntity.ok("Parcelle mise à jour");
	}

	public ResponseEntity<String> supprimer(Long id) {
		Parcelle p = chercherParId(id);
		// On conserve l'historique des recommandations
		if (recommandationRepo.existsByParcelleId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"La parcelle " + id + " possède des recommandations : suppression refusée");
		}
		parcelleRepo.delete(p);
		return ResponseEntity.ok("Parcelle supprimée");
	}
}
