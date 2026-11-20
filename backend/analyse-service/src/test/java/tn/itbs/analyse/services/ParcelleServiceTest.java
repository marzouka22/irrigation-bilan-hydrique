package tn.itbs.analyse.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import tn.itbs.analyse.entities.Culture;
import tn.itbs.analyse.entities.Parcelle;
import tn.itbs.analyse.entities.SystemeIrrigation;
import tn.itbs.analyse.repositories.ParcelleRepository;
import tn.itbs.analyse.repositories.RecommandationRepository;

@ExtendWith(MockitoExtension.class)
class ParcelleServiceTest {

	@Mock
	private ParcelleRepository parcelleRepo;
	@Mock
	private RecommandationRepository recommandationRepo;
	@InjectMocks
	private ParcelleService parcelleService;

	@Test
	void ajouter_sansKc_utiliseLeKcDeReferenceDeLaCulture() {
		Parcelle p = new Parcelle(null, "Tomates Korba", Culture.TOMATE, null, 1.5, 36.57, SystemeIrrigation.GOUTTE_A_GOUTTE);
		when(parcelleRepo.save(any(Parcelle.class))).thenAnswer(inv -> {
			Parcelle arg = inv.getArgument(0);
			arg.setId(1L);
			return arg;
		});

		parcelleService.ajouter(p);

		assertThat(p.getKc()).isEqualTo(1.15);
	}

	@Test
	void supprimer_avecRecommandations_leve409() {
		Parcelle p = new Parcelle(1L, "Agrumes", Culture.AGRUMES, 0.65, 3.0, 36.6, SystemeIrrigation.ASPERSION);
		when(parcelleRepo.findById(1L)).thenReturn(Optional.of(p));
		when(recommandationRepo.existsByParcelleId(1L)).thenReturn(true);

		assertThatThrownBy(() -> parcelleService.supprimer(1L))
				.isInstanceOf(ResponseStatusException.class)
				.satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
						.isEqualTo(HttpStatus.CONFLICT));
		verify(parcelleRepo, never()).delete(any());
	}
}
