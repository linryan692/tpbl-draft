// src/main/java/com/tpbl/repo/PickRepository.java
package com.tpbl.repo;

import com.tpbl.model.Pick;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PickRepository extends JpaRepository<Pick, Long> {
    /** 按「轮次」和「总順位」排序所有 Pick 记录 */
    List<Pick> findAllByOrderByRoundAscPickNumberAsc();
}
