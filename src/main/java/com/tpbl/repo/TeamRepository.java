// src/main/java/com/tpbl/repo/TeamRepository.java
package com.tpbl.repo;

import com.tpbl.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // 問多少個 username
    long countByUsername(String username);

    // 用 username 找 Team，給 Security 用
    Optional<Team> findByUsername(String username);

    // DraftService 啟動時拉出所有 active=true，並且依照 id 排序
    List<Team> findByActiveTrueOrderById();
}
