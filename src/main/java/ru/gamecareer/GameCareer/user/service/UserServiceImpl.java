package ru.gamecareer.GameCareer.user.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gamecareer.GameCareer.exceptions.ErrorPassword;
import ru.gamecareer.GameCareer.exceptions.ExceptionNotFound;
import ru.gamecareer.GameCareer.exceptions.UserAlreadyExistException;
import ru.gamecareer.GameCareer.mailSender.EmailService;
import ru.gamecareer.GameCareer.user.Profession;
import ru.gamecareer.GameCareer.user.Role;
import ru.gamecareer.GameCareer.user.Status;
import ru.gamecareer.GameCareer.user.dto.UserDtoRegistration;
import ru.gamecareer.GameCareer.user.model.RegisterKey;
import ru.gamecareer.GameCareer.user.model.User;
import ru.gamecareer.GameCareer.user.repository.RegisterKeyRepository;
import ru.gamecareer.GameCareer.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RegisterKeyRepository registerKeyRepository;

    @Override
    public String createUser(UserDtoRegistration userDtoRegistration) {
        if (!userDtoRegistration.getPassword().equals(userDtoRegistration.getPasswordConfirm())) {
            throw new ErrorPassword("Пароли не совпадают.");
        }
        User userIsExist = userRepository.findByEmail(userDtoRegistration.getEmail());
        if (userIsExist != null) {
            if (Objects.equals(userIsExist.getEmail(), userDtoRegistration.getEmail())) {
                throw new UserAlreadyExistException("Пользователь с таким Email уже зарегистрирован: "
                        + userDtoRegistration.getEmail());
            }
        }

        User user = User.builder()
                .name(userDtoRegistration.getName())
                .email(userDtoRegistration.getEmail())
                .password(passwordEncoder.encode(userDtoRegistration.getPassword()))
                .created(LocalDateTime.now())
                .role(Role.ROLE_UNAPPROVED)
                .status(Status.UNAPPROVED)
                .profession(Profession.UNAPPROVED)
                .build();

        String key = UUID.randomUUID().toString();
        Set<String> to = new HashSet<>(Collections.singleton(user.getEmail()));

        // from email
        emailService.sendMail("email", to, "Регистрация в Карьера",
                "Для завершения регистрации перейдите по ссылке " +
                        "http://localhost:8080/karyeraSecurity/confirmation?key=" + key + "&email=" + user.getEmail());

        User saveUser = userRepository.save(user);

        RegisterKey registerKey = RegisterKey.builder()
                .key(key)
                .email(saveUser.getEmail())
                .userId(saveUser.getId())
                .exp(LocalDateTime.now().plusDays(2))
                .build();


        registerKeyRepository.save(registerKey);

        return "Проверьте электронную почту, перейдите по ссылке чтобы завершить регистрацию.";
    }

    @Override
    public String confirmationRegistration(String key, String email) {
        RegisterKey registerKey = registerKeyRepository.findByEmail(email);

        if (registerKey.getKey() == null) {
            throw new ExceptionNotFound("Подтверждающий ключ не найден.");
        }


        if (!registerKey.getKey().equals(key) && registerKey.getEmail().equals(email)) {
            throw new ExceptionNotFound("Несовпадение ключа или почты.");
        }
        if (registerKey.getExp().isBefore(LocalDateTime.now())) {
            registerKeyRepository.delete(registerKey);
            userRepository.deleteByEmail(email);
            throw new ExceptionNotFound("Время подтверждения вышло.");
        }

        User user = userRepository.findById(registerKey.getUserId()).orElseThrow(
                () -> new ExceptionNotFound("Пользователь не найден, id " + registerKey.getUserId()));

        user.setRole(Role.ROLE_USER);
        user.setStatus(Status.READY);
        user.setProfession(Profession.WORKER);
        user.setCreated(LocalDateTime.now());

        registerKeyRepository.delete(registerKey);
        userRepository.save(user);

        return "Успех, регистрация подтверждена.";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ExceptionNotFound("Пользователя нет в базе" + username);
        }
        return user;
    }
}
