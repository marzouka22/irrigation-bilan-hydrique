package tn.itbs.collecte.repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.itbs.collecte.entities.Observation;

public interface ObservationRepository extends JpaRepository<Observation, Long> {

	List<Observation> findByCapteurIdOrderByDateDesc(Long capteurId);

	// Observations récentes d'un ensemble de capteurs (ceux d'une parcelle), de la plus récente à la plus ancienne
	List<Observation> findByCapteurIdInAndDateAfterOrderByDateDesc(Collection<Long> capteurIds, LocalDateTime depuis);

	boolean existsByCapteurId(Long capteurId);
}
