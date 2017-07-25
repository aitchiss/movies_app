package com.example.android.movies;

/**
 * Created by suzanneaitchison on 25/07/2017.
 */

public class Movie {

    private String title;
    private String posterPath;
    private String synopsis;
    private String releaseDate;

    public Movie(String title, String posterPath, String synopsis, String releaseDate){
        this.title = title;
        this.posterPath = posterPath;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
