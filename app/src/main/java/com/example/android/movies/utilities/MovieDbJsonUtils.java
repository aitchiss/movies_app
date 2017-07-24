package com.example.android.movies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by suzanneaitchison on 24/07/2017.
 */

public class MovieDbJsonUtils {

    public static HashMap<String, String>[] getMovieHashes(String jsonMovieDataString) throws JSONException{

//        Keys to access the data we want to store and return:
        final String MDB_TITLE = "title";
        final String MDB_POSTER = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";


        JSONObject jsonMovies = new JSONObject(jsonMovieDataString);

        JSONArray movieArray = jsonMovies.getJSONArray("results");
        HashMap<String, String>[] movies =  new HashMap[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++){
            HashMap<String, String> movieInfo = new HashMap<>();
            JSONObject jsonMovie = movieArray.getJSONObject(i);

            movieInfo.put("title", jsonMovie.getString(MDB_TITLE));
            Log.d("title", jsonMovie.getString(MDB_TITLE));
            movieInfo.put("poster", jsonMovie.getString(MDB_POSTER));
            movieInfo.put("overview", jsonMovie.getString(MDB_OVERVIEW));
            movieInfo.put("releaseDate", jsonMovie.getString(MDB_RELEASE_DATE));

            movies[i] = movieInfo;

        }

        return movies;

    }
}
