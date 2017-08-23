package com.example.android.movies.data;

import android.database.Cursor;

import com.example.android.movies.Movie;

/**
 * Created by suzanneaitchison on 22/08/2017.
 */

public class FavouritesCursorUtils {


    public static Movie[] convertCursorDataToMovies(Cursor cursor){

        Movie[] movies = new Movie[cursor.getCount()];

        int movieIdColumn = cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID);
        int posterColumn = cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH);
        int titleColumn = cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE);
        int ratingColumn = cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_RATING);
        int synopsisColumn = cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_SYNOPSIS);
        int releaseDateColumn = cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_RELEASE_DATE);

        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);

            int movieId = cursor.getInt(movieIdColumn);
            String posterPath = cursor.getString(posterColumn);
            String title = cursor.getString(titleColumn);
            int rating = cursor.getInt(ratingColumn);
            String synopsis = cursor.getString(synopsisColumn);
            String releaseDate = cursor.getString(releaseDateColumn);

            Movie movie = new Movie(title, posterPath, synopsis, releaseDate, rating, movieId);
//            TODO MOVIE ID AND RATING SHOULD BE INTS WHEN SAVED TO DB - EDIT
            movies[i] = movie;
        }

        return movies;

    }



}
