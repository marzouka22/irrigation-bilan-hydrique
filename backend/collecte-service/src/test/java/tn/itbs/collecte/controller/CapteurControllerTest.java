package tn.itbs.collecte.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.collecte.entities.Capteur;
import tn.itbs.collecte.services.CapteurService;

@WebMvcTest(CapteurController.class)
class CapteurControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private CapteurService capteurServ;

	@Test
	void post_capteurValide_retourne201() throws Exception {
		when(capteurServ.ajouter(any(Capteur.class)))
				.thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("Capteur ajouté avec l'id 1"));

		mvc.perform(post("/capteurs")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"type":"PLUIE","parcelleId":1,"localisation":"Parcelle Nord - Menzel Bouzelfa"}
						"""))
			.andExpect(status().isCreated());
	}

	@Test
	void post_champsManquants_retourne400AvecDetails() throws Exception {
		mvc.perform(post("/capteurs")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"type":"PLUIE"}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.details.parcelleId").exists())
			.andExpect(jsonPath("$.details.localisation").exists());
	}

	@Test
	void post_typeInconnu_retourne400() throws Exception {
		mvc.perform(post("/capteurs")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"type":"VENT","parcelleId":1,"localisation":"Nord"}
						"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	void get_capteurInexistant_retourne404() throws Exception {
		when(capteurServ.chercherParId(99L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Capteur 99 introuvable"));

		mvc.perform(get("/capteurs/99"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.erreur").value("Capteur 99 introuvable"));
	}

	@Test
	void get_idNonNumerique_retourne400() throws Exception {
		mvc.perform(get("/capteurs/abc"))
			.andExpect(status().isBadRequest());
	}
}
