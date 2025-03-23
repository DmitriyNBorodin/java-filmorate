package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> likes;
    private List<Map<String, Integer>> genres;
    private Map<String, Integer> mpa;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        likes = new HashSet<>();
        genres = new ArrayList<>();
        mpa = new HashMap<>();
    }

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void removeLike(int userId) {
        likes.remove(userId);
    }

    public Integer obtainAmountOfLikes() {
        return likes.size();
    }

    public void addGenre(int genreId) {
        HashMap<String, Integer> newGenre = new HashMap<>();
        newGenre.put("id", genreId);
        genres.add(newGenre);
    }

    public void setMpaRating(Integer rating) {
        mpa.put("id", rating);
    }
}
