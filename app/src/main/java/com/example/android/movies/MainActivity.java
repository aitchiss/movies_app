package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies.utilities.MovieDbJsonUtils;
import com.example.android.movies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickHandler {

    private String apiKey;
    private RecyclerView mMovieRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private String sortOption;
    private ProgressBar mLoadingBar;
    private LinearLayout mLoadingErrorMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey = getString(R.string.my_api_key);
//        use sharedPrefs or similar to check what sort option should be on create?
        sortOption = "popular";
        mLoadingBar = (ProgressBar) findViewById(R.id.pb_loading_bar);
        mLoadingErrorMessage = (LinearLayout) findViewById(R.id.loading_error);


        mMovieRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies_list);

        int currentOrientation = getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager = null;
        if (currentOrientation == 1){
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 3);
        }

        mMovieRecyclerView.setLayoutManager(layoutManager);

        mMoviesAdapter = new MoviesAdapter(this);
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
//        if a new sort option has been selected, reload the correct movies, otherwise ignore
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
//        only attempt to load movies if device is online or connecting to internet
        if (isOnlineOrConnecting()){
            mLoadingBar.setVisibility(View.VISIBLE);
            URL url = NetworkUtils.buildUrl(apiKey, sortOption);
            new FetchMoviesTask().execute(url);
        } else {
            showErrorView();
        }
    }

    public void showErrorView(){
        mLoadingErrorMessage.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingBar.setVisibility(View.INVISIBLE);
    }

    public void showMovieView(){
        mLoadingErrorMessage.setVisibility(View.INVISIBLE);
        mLoadingBar.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    public boolean isOnlineOrConnecting(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailsActivityIntent = new Intent(this, MovieDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movie);
        detailsActivityIntent.putExtras(bundle);

        startActivity(detailsActivityIntent);
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
