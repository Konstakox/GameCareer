package ru.gamecareer.GameCareer.user.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="register_keys")
public class RegisterKey {
    @Id
    @Column(name = "register_key_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "hash_key", nullable = false, length = 2000)
    private String key;
    @Column(name = "email", nullable = false, length = 60)
    private String email;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "exp", nullable = false)
    private LocalDateTime exp;
}
