package tn.itbs.analyse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.itbs.analyse.entities.Recommandation;

public interface RecommandationRepository extends JpaRepository<Recommandation, Long> {

	List<Recommandation> findByParcelleIdOrderByDateCalculDesc(Long parcelleId);

	List<Recommandation> findAllByOrderByDateCalculDesc();

	boolean existsByParcelleId(Long parcelleId);
}
