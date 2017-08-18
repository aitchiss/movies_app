package com.example.android.movies;

/**
 * Created by suzanneaitchison on 17/08/2017.
 */

public class Trailer {

    private final String id;
    private final String name;
    private final String youTubeKey;

    public Trailer(String id, String name, String youTubeKey){
        this.id = id;
        this.name = name;
        this.youTubeKey = youTubeKey;
    }

    public String getName(){
        return this.name;
    }

    public String getYouTubeKey(){
        return this.youTubeKey;
    }

}
