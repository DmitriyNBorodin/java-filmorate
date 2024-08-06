package ru.yandex.practicum.filmorate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

@Configuration
public class TestConfig {

    @Bean
    public FilmController filmController() {
        return new FilmController();
    }

    @Bean
    public UserController userController() {
        return new UserController();
    }
}
