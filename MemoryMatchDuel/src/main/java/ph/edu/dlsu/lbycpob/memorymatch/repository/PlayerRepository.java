package ph.edu.dlsu.lbycpob.memorymatch.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.memorymatch.entity.PlayerEntity;

import java.util.List;
import java.util.Optional;

// UNDERSTAND: A Spring Data repository interface for PlayerEntity — extending JpaRepository already
// gives free save/find/delete methods; the two extra methods below are custom lookups.
// DECISION: findByUsername and findAllByOrderByHighestScoreDesc were written as method names only (no
// method body) instead of hand-written SQL, because Spring Data JPA auto-generates the query just from
// how the method is named, saving boilerplate.
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    Optional<PlayerEntity> findByUsername(String username);

    List<PlayerEntity> findAllByOrderByHighestScoreDesc(Pageable pageable);
}