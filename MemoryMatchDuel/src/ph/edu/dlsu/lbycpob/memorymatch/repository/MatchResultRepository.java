package ph.edu.dlsu.lbycpob.memorymatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.memorymatch.entity.MatchResultEntity;

public interface MatchResultRepository extends JpaRepository<MatchResultEntity, Long> {
}