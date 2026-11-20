package tn.itbs.collecte.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tn.itbs.collecte.dtos.ObservationDetails;
import tn.itbs.collecte.entities.Observation;
import tn.itbs.collecte.services.ObservationService;

// Une observation est une mesure : elle n'est ni modifiée ni supprimée (historique fiable)
@RestController
@RequestMapping("/observations")
public class ObservationController {

	@Autowired
	private ObservationService observationServ;

	@PostMapping
	public ResponseEntity<String> ajouter(@Valid @RequestBody Observation o) {
		return observationServ.ajouter(o);
	}

	@GetMapping
	public List<Observation> lister() {
		return observationServ.lister();
	}

	@GetMapping("/{id}")
	public Observation chercherParId(@PathVariable Long id) {
		return observationServ.chercherParId(id);
	}

	@GetMapping("/capteur/{capteurId}")
	public List<Observation> parCapteur(@PathVariable Long capteurId) {
		return observationServ.parCapteur(capteurId);
	}

	// Ex. : GET /observations/parcelle/1?heures=24
	@GetMapping("/parcelle/{parcelleId}")
	public List<ObservationDetails> dernieresParParcelle(@PathVariable Long parcelleId,
			@RequestParam(defaultValue = "24") int heures) {
		return observationServ.dernieresParParcelle(parcelleId, heures);
	}
}
