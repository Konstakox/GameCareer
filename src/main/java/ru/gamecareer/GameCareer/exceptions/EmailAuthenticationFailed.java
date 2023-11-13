package ru.gamecareer.GameCareer.exceptions;


import org.springframework.mail.MailAuthenticationException;

public class EmailAuthenticationFailed extends MailAuthenticationException {
    public EmailAuthenticationFailed(String msg) {
        super("Не проходит проверку на стороне почтового сервиса");
    }
}
