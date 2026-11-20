package tn.itbs.analyse.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tn.itbs.analyse.dtos.DemandeCalcul;
import tn.itbs.analyse.dtos.DemandeComparaison;
import tn.itbs.analyse.dtos.ResultatComparaison;
import tn.itbs.analyse.entities.Recommandation;
import tn.itbs.analyse.services.RecommandationService;

@RestController
@RequestMapping("/recommandations")
public class RecommandationController {

	@Autowired
	private RecommandationService recommandationServ;

	// Le résultat (dose, justification) est renvoyé directement : le client en a besoin pour l'affichage
	@PostMapping("/calcul")
	public ResponseEntity<Recommandation> calculer(@Valid @RequestBody DemandeCalcul demande) {
		Recommandation r = recommandationServ.calculer(demande);
		return ResponseEntity.created(URI.create("/recommandations/" + r.getId())).body(r);
	}

	@PostMapping("/comparaison")
	public List<ResultatComparaison> comparer(@Valid @RequestBody DemandeComparaison demande) {
		return recommandationServ.comparer(demande);
	}

	@GetMapping
	public List<Recommandation> lister() {
		return recommandationServ.lister();
	}

	@GetMapping("/{id}")
	public Recommandation chercherParId(@PathVariable Long id) {
		return recommandationServ.chercherParId(id);
	}

	@GetMapping("/parcelle/{parcelleId}")
	public List<Recommandation> parParcelle(@PathVariable Long parcelleId) {
		return recommandationServ.parParcelle(parcelleId);
	}
}
