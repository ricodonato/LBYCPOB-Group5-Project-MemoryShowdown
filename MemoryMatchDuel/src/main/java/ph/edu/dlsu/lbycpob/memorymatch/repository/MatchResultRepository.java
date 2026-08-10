package ph.edu.dlsu.lbycpob.memorymatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.memorymatch.entity.MatchResultEntity;

// UNDERSTAND: A Spring Data repository for MatchResultEntity. It's left empty because JpaRepository
// already provides save(), findAll(), findById(), etc. — no custom queries are needed for this entity yet.
public interface MatchResultRepository extends JpaRepository<MatchResultEntity, Long> {
}