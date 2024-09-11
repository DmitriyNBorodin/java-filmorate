package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.CodeEntity;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    FilmService filmService;

    @GetMapping
    public List<CodeEntity> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/{genreCode}")
    public CodeEntity getGenreByCode(@PathVariable int genreCode) {
        return filmService.getGenreByCode(genreCode);
    }
}
