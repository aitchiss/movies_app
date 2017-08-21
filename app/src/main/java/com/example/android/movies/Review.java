package com.example.android.movies;

/**
 * Created by suzanneaitchison on 21/08/2017.
 */

public class Review {

    private String id;
    private String author;
    private String content;

    public Review(String id, String author, String content){
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
