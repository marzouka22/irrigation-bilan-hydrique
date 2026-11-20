package tn.itbs.analyse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.analyse.entities.Modele;
import tn.itbs.analyse.repositories.ModeleRepository;

@Service
public class ModeleService {

	@Autowired
	private ModeleRepository modeleRepo;

	public List<Modele> lister() {
		return modeleRepo.findAllByOrderByVersionAsc();
	}

	public Modele chercherParId(Long id) {
		return modeleRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Modèle " + id + " introuvable"));
	}

	public Modele modeleParDefaut() {
		return modeleRepo.findFirstByParDefautTrue()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Aucun modèle par défaut n'est configuré"));
	}
}
