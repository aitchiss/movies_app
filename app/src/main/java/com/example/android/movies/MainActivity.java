package com.example.android.movies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.android.movies.utilities.MovieDbJsonUtils;
import com.example.android.movies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String apiKey;
    private RecyclerView mMovieRecyclerView;
    private MoviesAdapter mMoviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.my_api_key);

        mMovieRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        mMovieRecyclerView.setLayoutManager(layoutManager);

        mMoviesAdapter = new MoviesAdapter();
        mMovieRecyclerView.setAdapter(mMoviesAdapter);

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
            return movieResults;
        }

        @Override
        protected void onPostExecute(String s) {
//            Check that the string isn't empty, and parse/display
            if (s != null && !s.equals("")){
                try {
                    HashMap<String, String>[] parsedMovieDetails = MovieDbJsonUtils.getMovieHashes(s);
                    mMoviesAdapter.setMovieData(parsedMovieDetails);
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }
    }
}
