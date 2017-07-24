package com.example.android.movies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.movies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by suzanneaitchison on 24/07/2017.
 */

// used to execute API requests

public class NetworkUtils {

    private static final String BASE_API_URL = "https://api.themoviedb.org/3/movie";
    private static final String LANGUAGE_PARAM = "language";
    private static final String LANGUAGE_ENGLISH = "en-GB";
    private static final String API_KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";




    public static URL buildUrl(String apiKey, String sortOption){
        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendPath(sortOption)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_ENGLISH)
                .appendQueryParameter(PAGE_PARAM, "1")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d("URL Built: ", String.valueOf(url));
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
}
