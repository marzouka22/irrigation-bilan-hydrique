package tn.itbs.analyse.controller;

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
import tn.itbs.analyse.entities.Parcelle;
import tn.itbs.analyse.services.ParcelleService;

@RestController
@RequestMapping("/parcelles")
public class ParcelleController {

	@Autowired
	private ParcelleService parcelleServ;

	@PostMapping
	public ResponseEntity<String> ajouter(@Valid @RequestBody Parcelle p) {
		return parcelleServ.ajouter(p);
	}

	@GetMapping
	public List<Parcelle> lister() {
		return parcelleServ.lister();
	}

	@GetMapping("/{id}")
	public Parcelle chercherParId(@PathVariable Long id) {
		return parcelleServ.chercherParId(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> modifier(@PathVariable Long id, @Valid @RequestBody Parcelle p) {
		return parcelleServ.modifier(id, p);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> supprimer(@PathVariable Long id) {
		return parcelleServ.supprimer(id);
	}
}
