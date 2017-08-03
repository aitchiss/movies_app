package com.example.android.movies.utilities;

import android.util.Log;

import com.example.android.movies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        final String MDB_RATING = "vote_average";
        final String MDB_RESULTS = "results";
        final String MDB_POSTER_PATH_PREFIX = "http://image.tmdb.org/t/p/w185";

        JSONObject jsonMovies = new JSONObject(jsonMovieDataString);
        JSONArray jsonMovieArray = jsonMovies.getJSONArray(MDB_RESULTS);

        Movie[] movies =  new Movie[jsonMovieArray.length()];

//        Loops over the JSON array, and creates a Movie object, storing in the movies array

        for (int i = 0; i < jsonMovieArray.length(); i++){
            JSONObject jsonMovie = jsonMovieArray.getJSONObject(i);

            String title = jsonMovie.getString(MDB_TITLE);
            String poster = MDB_POSTER_PATH_PREFIX + jsonMovie.getString(MDB_POSTER);
            String synopsis = jsonMovie.getString(MDB_OVERVIEW);
            String releaseDate = convertToUKDate(jsonMovie.getString(MDB_RELEASE_DATE));
            int rating = jsonMovie.getInt(MDB_RATING);

            Movie movie = new Movie(title, poster, synopsis, releaseDate, rating);
            movies[i] = movie;
        }

        return movies;
    }

    private static String convertToUKDate(String date){
        String[] dateComponents = date.split("-");
        String formattedDate = dateComponents[2] + "/" + dateComponents[1] + "/" + dateComponents[0];
        return formattedDate;
    }

}
