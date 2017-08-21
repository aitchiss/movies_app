package com.example.android.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by suzanneaitchison on 21/08/2017.
 */

public class FavouritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favourites.db";
    private static final int DATABASE_VERSION = 1;

    public FavouritesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + FavouritesContract.FavouritesEntry.TABLE_NAME + " (" +
                FavouritesContract.FavouritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT, " +
                FavouritesContract.FavouritesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, " +
                FavouritesContract.FavouritesEntry.COLUMN_MOVIE_RATING + " INTEGER, " +
                FavouritesContract.FavouritesEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT)";

        db.execSQL(SQL_CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouritesContract.FavouritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
