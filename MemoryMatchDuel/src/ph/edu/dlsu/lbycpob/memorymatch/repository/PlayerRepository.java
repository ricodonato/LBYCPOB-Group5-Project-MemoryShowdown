package ph.edu.dlsu.lbycpob.memorymatch.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ph.edu.dlsu.lbycpob.memorymatch.entity.PlayerEntity;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    Optional<PlayerEntity> findByUsername(String username);

    List<PlayerEntity> findAllByOrderByHighestScoreDesc(Pageable pageable);
}