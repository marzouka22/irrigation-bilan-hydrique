package tn.itbs.analyse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tn.itbs.analyse.calcul.DonneesInsuffisantesException;
import tn.itbs.analyse.dtos.DemandeCalcul;
import tn.itbs.analyse.entities.Fiabilite;
import tn.itbs.analyse.entities.Recommandation;
import tn.itbs.analyse.services.RecommandationService;

@WebMvcTest(RecommandationController.class)
class RecommandationControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private RecommandationService recommandationServ;

	@Test
	void calcul_valide_retourne201AvecLaRecommandation() throws Exception {
		when(recommandationServ.calculer(any(DemandeCalcul.class))).thenReturn(new Recommandation(5L, 1L, 89.4, 4.47,
				"Modèle BH 2.0 ...", LocalDateTime.now(), 2L, "2.0", Fiabilite.NORMALE));

		mvc.perform(post("/recommandations/calcul")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"parcelleId":1,
						 "donnees":{"pluie":0,"temperatureMin":22,"temperatureMax":33,"date":"2026-07-15"}}
						"""))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "/recommandations/5"))
			.andExpect(jsonPath("$.quantiteEau").value(89.4))
			.andExpect(jsonPath("$.fiabilite").value("NORMALE"));

		// Les données climatiques JSON sont bien converties
		ArgumentCaptor<DemandeCalcul> captor = ArgumentCaptor.forClass(DemandeCalcul.class);
		verify(recommandationServ).calculer(captor.capture());
		assertThat(captor.getValue().donnees().temperatureMin()).isEqualTo(22.0);
		assertThat(captor.getValue().donnees().temperatureMax()).isEqualTo(33.0);
		assertThat(captor.getValue().donnees().date()).isEqualTo(LocalDate.of(2026, 7, 15));
	}

	@Test
	void calcul_sansDonnees_retourne400() throws Exception {
		mvc.perform(post("/recommandations/calcul")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"parcelleId":1}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.details.donnees").exists());
	}

	@Test
	void calcul_donneesInsuffisantesPourLeModele_retourne422() throws Exception {
		when(recommandationServ.calculer(any(DemandeCalcul.class)))
				.thenThrow(new DonneesInsuffisantesException("Le modèle BH 1.0 nécessite une ET0 mesurée"));

		mvc.perform(post("/recommandations/calcul")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"parcelleId":1,"modeleId":1,"donnees":{"pluie":2}}
						"""))
			.andExpect(status().is(422));
	}
}
