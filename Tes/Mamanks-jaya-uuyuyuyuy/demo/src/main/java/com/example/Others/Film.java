package com.example.Others;

//getter setter untuk poster dan nama film
public class Film {
    private String name;
    private String posterPath;

    public Film(String name, String posterPath) {
        this.name = name;
        this.posterPath = posterPath;
    }

    public String getName() {
        return name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    @Override
    public String toString() {
        return name; // Override toString() to return the film name
    }
}
