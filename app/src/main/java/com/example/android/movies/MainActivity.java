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
import android.view.View;
import android.widget.Button;
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
    private TextView errorView;
    private Button mRetryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.my_api_key);
        sortOption = "popular";
        errorView = (TextView) findViewById(R.id.tv_loading_error);
        mRetryButton = (Button) findViewById(R.id.btn_error_retry);

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

    public void showErrorView(){
        errorView.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void showMovieView(){
        errorView.setVisibility(View.INVISIBLE);
        mRetryButton.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    public void onRetryButtonClick(View view){
        loadMovieData();
    }


    public class FetchMoviesTask extends AsyncTask<URL, Void, HashMap<String, String>[]>{

        @Override
        protected HashMap<String, String>[] doInBackground(URL... params) {
            URL url = params[0];
            HashMap<String, String>[] parsedMovieDetails = null;
            try {
                String movieResults = NetworkUtils.getResponseFromHttpUrl(url);
                parsedMovieDetails = MovieDbJsonUtils.getMovieHashes(movieResults);
            } catch (Exception e){
                e.printStackTrace();
            }
            return parsedMovieDetails;
        }

        @Override
        protected void onPostExecute(HashMap<String, String>[] movieDetails) {
//            Check that the string isn't empty, and parse/display
            if (movieDetails != null){
                showMovieView();
                mMoviesAdapter.setMovieData(movieDetails);
            } else {
                showErrorView();
            }
        }
    }
}
