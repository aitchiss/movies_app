package com.example.android.movies.utilities;

import android.util.Log;

import com.example.android.movies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by suzanneaitchison on 24/07/2017.
 */

public class MovieDbJsonUtils {

    public static Movie[] convertJsonToMovies(String jsonMovieDataString) throws JSONException{

//        Keys to access the data we want to store and return:
        final String MDB_TITLE = "title";
        final String MDB_POSTER = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";


        JSONObject jsonMovies = new JSONObject(jsonMovieDataString);

        JSONArray movieArray = jsonMovies.getJSONArray("results");
        Movie[] movies =  new Movie[movieArray.length()];

//        Loops over the JSON array, and stores each movie as a HashMap in the movies array

        for (int i = 0; i < movieArray.length(); i++){
            JSONObject jsonMovie = movieArray.getJSONObject(i);

            String title = jsonMovie.getString(MDB_TITLE);
            String poster = "http://image.tmdb.org/t/p/w185" + jsonMovie.getString(MDB_POSTER);
            String synopsis = jsonMovie.getString(MDB_OVERVIEW);
            String releaseDate = jsonMovie.getString(MDB_RELEASE_DATE);

            Movie movie = new Movie(title, poster, synopsis, releaseDate);
            movies[i] = movie;
        }

        return movies;

    }
}
