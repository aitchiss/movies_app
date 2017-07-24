package com.example.android.movies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private String sortOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.my_api_key);
        sortOption = "popular";

        mMovieRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        mMovieRecyclerView.setLayoutManager(layoutManager);

        mMoviesAdapter = new MoviesAdapter();
        mMovieRecyclerView.setAdapter(mMoviesAdapter);

        loadMovieData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort_highest_rated){
            if (sortOption == "top_rated"){
                return true;
            } else {
                sortOption = "top_rated";
                loadMovieData();
            }
        } else if (item.getItemId() == R.id.action_sort_most_popular){
            if (sortOption == "popular"){
                return true;
            } else {
                sortOption = "popular";
                loadMovieData();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadMovieData(){
        URL url = NetworkUtils.buildUrl(apiKey, sortOption);
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
