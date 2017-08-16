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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.example.android.movies.utilities.MovieDbJsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickHandler {

//    String constants used to refer to API sort options and keys for bundles
    private static final String SORT_POPULAR = "popular";
    private static final String SORT_HIGHEST_RATED = "top_rated";
    private static final String SORT_OPTION = "sortOption";
    private static final String MOVIES = "movies";
    private static final String MOVIE = "movie";

    private String apiKey;
    private RecyclerView mMovieRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private String mSortOption;
    private ProgressBar mLoadingBar;
    private LinearLayout mLoadingErrorMessage;
    private Movie[] mCurrentMovies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//      Retrieve key String from secrets values resource
        apiKey = getString(R.string.my_api_key);

//        Get a reference to page elements
        mLoadingBar = (ProgressBar) findViewById(R.id.pb_loading_bar);
        mLoadingErrorMessage = (LinearLayout) findViewById(R.id.loading_error);
        mMovieRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies_list);

//        Set number of posters in grid according to device orientation
        int currentOrientation = getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager;

        if (currentOrientation == 1){
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 3);
        }

        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMoviesAdapter = new MoviesAdapter(this);
        mMovieRecyclerView.setAdapter(mMoviesAdapter);

//        Retain current list of movies without making another network call
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES)){
            mCurrentMovies = (Movie[]) savedInstanceState.getParcelableArray(MOVIES);
            showMovieView();
            mMoviesAdapter.setMovieData(mCurrentMovies);
            mSortOption = savedInstanceState.getString(SORT_OPTION);
        } else {
            mSortOption = SORT_POPULAR;
            loadMovieData();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(MOVIES, mCurrentMovies);
        outState.putString(SORT_OPTION, mSortOption);
        super.onSaveInstanceState(outState);
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
            if (mSortOption.equals(SORT_HIGHEST_RATED)){
                return true;
            } else {
                mSortOption = SORT_HIGHEST_RATED;
                loadMovieData();
                return true;
            }
        } else if (item.getItemId() == R.id.action_sort_most_popular){
            if (mSortOption.equals(SORT_POPULAR)){
                return true;
            } else {
                mSortOption = SORT_POPULAR;
                loadMovieData();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadMovieData(){
//        only attempt to load movies if device is online or connecting to internet
        if (isOnlineOrConnecting()){
            mLoadingBar.setVisibility(View.VISIBLE);
            URL url = NetworkUtils.buildUrl(apiKey, mSortOption);
            new FetchMoviesTask().execute(url);
        } else {
            showErrorView();
        }
    }

    private void showErrorView(){
        mLoadingErrorMessage.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingBar.setVisibility(View.INVISIBLE);
    }

    private void showMovieView(){
        mLoadingErrorMessage.setVisibility(View.INVISIBLE);
        mLoadingBar.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    private boolean isOnlineOrConnecting(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailsActivityIntent = new Intent(this, MovieDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE, movie);
        detailsActivityIntent.putExtras(bundle);

        startActivity(detailsActivityIntent);
    }

    public void onRetryButtonClick(View view){
        loadMovieData();
    }


    private class FetchMoviesTask extends AsyncTask<URL, Void, Movie[]>{

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
                mCurrentMovies = movieDetails;
                showMovieView();
                mMoviesAdapter.setMovieData(movieDetails);
            } else {
                showErrorView();
            }
        }
    }
}
