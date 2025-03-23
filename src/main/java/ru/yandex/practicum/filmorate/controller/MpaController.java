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
@RequestMapping("/mpa")
public class MpaController {
    private FilmService filmService;

    @GetMapping("/{mpaCode}")
    public CodeEntity getMpaByCode(@PathVariable int mpaCode) {
        return filmService.getMpaByCode(mpaCode);
    }

    @GetMapping
    public List<CodeEntity> getMpaList() {
        return filmService.getAllMpaCodes();
    }
}
