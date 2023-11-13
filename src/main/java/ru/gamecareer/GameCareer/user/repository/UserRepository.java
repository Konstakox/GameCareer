package ru.gamecareer.GameCareer.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gamecareer.GameCareer.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    void deleteByEmail(String email);
}
