package ru.gamecareer.GameCareer.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gamecareer.GameCareer.user.model.RegisterKey;

public interface RegisterKeyRepository extends JpaRepository<RegisterKey, Integer> {
    RegisterKey findByEmail(String email);
}
