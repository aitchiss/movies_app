package com.example.android.movies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android.movies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private String apiKey;
    private TextView mMoviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.my_api_key);

        mMoviesList = (TextView) findViewById(R.id.tv_movies_list);
        loadMovieData();
    }

    public void loadMovieData(){
        URL url = NetworkUtils.buildUrl(apiKey);
        new FetchMoviesTask().execute(url);
    }


    public class FetchMoviesTask extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String movieResults = null;
            try {
                movieResults = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e){
                e.printStackTrace();
            }
            Log.d("retrieved results", movieResults);
            return movieResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")){
                mMoviesList.setText(s);

            }
        }
    }
}
