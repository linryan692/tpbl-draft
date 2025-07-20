package com.tpbl.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "team",
  uniqueConstraints = @UniqueConstraint(columnNames = "username")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 是否仍在選秀中 */
    @Column(nullable = false)
    private boolean active;

    /** 球隊中文名稱 */
    @Column(nullable = false)
    private String name;

    /** 登入用帳號 (唯一) */
    @Column(nullable = false, unique = true)
    private String username;

    /** BCrypt 加密後密碼 */
    @Column(nullable = false)
    private String password;

    /**
     * --- 新增的輔助建構子 ---
     * 讓 DataInitializer 能繼續呼叫 new Team(null, name, true)
     */
    public Team(Long id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }
}
