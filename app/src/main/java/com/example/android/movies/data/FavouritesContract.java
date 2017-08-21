package com.example.android.movies.data;

import android.provider.BaseColumns;

/**
 * Created by suzanneaitchison on 21/08/2017.
 */

public class FavouritesContract {

    public static final class FavouritesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favourites";

//      TABLE COLUMNS - SUFFICIENT INFO TO BUILD A MOVIE OBJECT AND DISPLAY WHEN OFFLINE
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_POSTER_PATH = "moviePosterPath";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movieSynopsis";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_RATING = "movieRating";


    }
}
