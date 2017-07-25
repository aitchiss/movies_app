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
import android.widget.ProgressBar;
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
    private TextView mErrorView;
    private Button mRetryButton;
    private ProgressBar mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.my_api_key);
//        use sharedPrefs or similar to check what sort option should be on create?
        sortOption = "popular";
        mErrorView = (TextView) findViewById(R.id.tv_loading_error);
        mRetryButton = (Button) findViewById(R.id.btn_error_retry);
        mLoadingBar = (ProgressBar) findViewById(R.id.pb_loading_bar);

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
        mLoadingBar.setVisibility(View.VISIBLE);
        URL url = NetworkUtils.buildUrl(apiKey, sortOption);
        new FetchMoviesTask().execute(url);
    }

    public void showErrorView(){
        mErrorView.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingBar.setVisibility(View.INVISIBLE);
    }

    public void showMovieView(){
        mErrorView.setVisibility(View.INVISIBLE);
        mRetryButton.setVisibility(View.INVISIBLE);
        mLoadingBar.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    public void onRetryButtonClick(View view){
        loadMovieData();
    }


    public class FetchMoviesTask extends AsyncTask<URL, Void, Movie[]>{

        @Override
        protected Movie[] doInBackground(URL... params) {
            URL url = params[0];
            Movie[] parsedMovieDetails = null;
            try {
                String movieResults = NetworkUtils.getResponseFromHttpUrl(url);
                parsedMovieDetails = MovieDbJsonUtils.convertJsonToMovies(movieResults);
            } catch (Exception e){
                e.printStackTrace();
            }
            return parsedMovieDetails;
        }

        @Override
        protected void onPostExecute(Movie[] movieDetails) {
            if (movieDetails != null){
                showMovieView();
                mMoviesAdapter.setMovieData(movieDetails);
            } else {
                showErrorView();
            }
        }
    }
}
