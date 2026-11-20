package tn.itbs.collecte.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tn.itbs.collecte.entities.Capteur;
import tn.itbs.collecte.services.CapteurService;

@RestController
@RequestMapping("/capteurs")
public class CapteurController {

	@Autowired
	private CapteurService capteurServ;

	@PostMapping
	public ResponseEntity<String> ajouter(@Valid @RequestBody Capteur c) {
		return capteurServ.ajouter(c);
	}

	@GetMapping
	public List<Capteur> lister() {
		return capteurServ.lister();
	}

	@GetMapping("/{id}")
	public Capteur chercherParId(@PathVariable Long id) {
		return capteurServ.chercherParId(id);
	}

	@GetMapping("/parcelle/{parcelleId}")
	public List<Capteur> chercherParParcelle(@PathVariable Long parcelleId) {
		return capteurServ.chercherParParcelle(parcelleId);
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> modifier(@PathVariable Long id, @Valid @RequestBody Capteur c) {
		return capteurServ.modifier(id, c);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> supprimer(@PathVariable Long id) {
		return capteurServ.supprimer(id);
	}
}
