// src/main/java/com/tpbl/repo/PlayerRepository.java
package com.tpbl.repo;

import com.tpbl.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByAvailableTrue();

    // 新增：
    boolean existsByName(String name);
    long countByName(String name);
}
