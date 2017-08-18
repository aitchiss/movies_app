package com.example.android.movies.utilities;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;



// used to execute API requests

public class NetworkUtils {

    private static final String BASE_API_URL = "https://api.themoviedb.org/3/movie";
    private static final String LANGUAGE_PARAM = "language";
    private static final String LANGUAGE_ENGLISH = "en-GB";
    private static final String API_KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";
    private static final String FIRST_PAGE = "1";
    // Limited to one page of data for consistency - in future would want a subsequent API call if user scrolls to bottom

    private static final String TRAILER_PATH = "videos";

    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";




    public static URL buildUrl(String apiKey, String sortOption){
        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendPath(sortOption)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_ENGLISH)
                .appendQueryParameter(PAGE_PARAM, FIRST_PAGE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildTrailerUrl(String apiKey, int movieId){
        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(TRAILER_PATH)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_ENGLISH)
                .appendQueryParameter(PAGE_PARAM, FIRST_PAGE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput){
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Uri getYouTubeTrailerUri(String trailerKey){
        return Uri.parse(YOUTUBE_BASE_URL + trailerKey);
    }
}
