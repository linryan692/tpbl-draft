// src/main/java/com/tpbl/repo/TeamRepository.java
package com.tpbl.repo;

import com.tpbl.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TeamRepository extends JpaRepository<Team,Long> {
  long countByName(String name);
  Optional<Team> findByUsername(String username);
  List<Team> findByActiveTrueOrderById();
}
