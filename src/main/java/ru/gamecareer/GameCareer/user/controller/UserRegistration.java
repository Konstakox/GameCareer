package ru.gamecareer.GameCareer.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.gamecareer.GameCareer.user.dto.UserDtoRegistration;
import ru.gamecareer.GameCareer.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/karyeraSecurity")
@RequiredArgsConstructor
public class UserRegistration {
    private final UserService userService;

    @PostMapping
    @RequestMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public String addUser(@RequestBody @Valid UserDtoRegistration userDtoRegistration) {
        return userService.createUser(userDtoRegistration);
    }

    @PostMapping
    @RequestMapping("/confirmation")
    @ResponseStatus(HttpStatus.OK)
    public String confirmationUser(@NotBlank @RequestParam(name = "key") String key,
                                   @NotBlank @RequestParam(name = "email") String email) {
        return userService.confirmationRegistration(key, email);
    }

    @GetMapping
    @RequestMapping("/permit")
    @ResponseStatus(HttpStatus.OK)
    public String permitAll() {
        return "доступен всем";
    }

}