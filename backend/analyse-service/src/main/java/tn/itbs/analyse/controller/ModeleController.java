package tn.itbs.analyse.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.itbs.analyse.entities.Modele;
import tn.itbs.analyse.services.ModeleService;

// Les modèles correspondent à des algorithmes du code : ils sont consultables, pas créés par l'API
@RestController
@RequestMapping("/modeles")
public class ModeleController {

	@Autowired
	private ModeleService modeleServ;

	@GetMapping
	public List<Modele> lister() {
		return modeleServ.lister();
	}

	@GetMapping("/{id}")
	public Modele chercherParId(@PathVariable Long id) {
		return modeleServ.chercherParId(id);
	}
}
