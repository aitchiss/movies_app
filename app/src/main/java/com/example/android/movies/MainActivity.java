package com.example.android.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.example.android.movies.data.FavouritesContract;
import com.example.android.movies.data.FavouritesCursorUtils;
import com.example.android.movies.utilities.MovieDbJsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import java.net.URL;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

//    String constants used to refer to API sort options and keys for bundles
    private static final String SORT_POPULAR = "popular";
    private static final String SORT_HIGHEST_RATED = "top_rated";
    private static final String SORT_FAVOURITES = "favourites";
    private static final String SORT_OPTION = "sortOption";
    private static final String MOVIES = "movies";
    private static final String MOVIE = "movie";

    private static final int FAVOURITES_LOADER_ID = 10;
    private static final int MOVIES_LOADER_ID = 11;

    @BindString(R.string.my_api_key) String mApiKey;

    @BindView(R.id.recyclerview_movies_list) RecyclerView mMovieRecyclerView;
    @BindView(R.id.pb_loading_bar) ProgressBar mLoadingBar;
    @BindView(R.id.loading_error) LinearLayout mLoadingErrorMessage;

    private MoviesAdapter mMoviesAdapter;
    private String mSortOption;
    private Movie[] mCurrentMovies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
        } else if (item.getItemId() == R.id.action_favourites){
            if (mSortOption.equals(SORT_FAVOURITES)){
                return true;
            } else {
                mSortOption = SORT_FAVOURITES;
                loadFavourites();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadMovieData(){
//        only attempt to load movies if device is online or connecting to internet
        if (NetworkUtils.isOnlineOrConnecting(this)){
            mLoadingBar.setVisibility(View.VISIBLE);
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader loader = loaderManager.getLoader(MOVIES_LOADER_ID);
            if (loader == null){
                loaderManager.initLoader(MOVIES_LOADER_ID, null, new MovieLoaderCallbacks());
            } else {
                loaderManager.restartLoader(MOVIES_LOADER_ID, null, new MovieLoaderCallbacks());
            }
        } else {
            showErrorView();
        }
    }

    private void loadFavourites(){
//        use content provider to display favourite movies.
//        Favourites held in local db, and can be displayed when offline
        mLoadingBar.setVisibility(View.VISIBLE);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Cursor> loader = loaderManager.getLoader(FAVOURITES_LOADER_ID);
        if (loader == null){
            loaderManager.initLoader(FAVOURITES_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(FAVOURITES_LOADER_ID, null, this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (mSortOption.equals(SORT_FAVOURITES)){
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
//                Get all the favourite movies and display them in alphabetical order
                try {
                    return getContentResolver().query(FavouritesContract.FavouritesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE,
                            null);
                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingBar.setVisibility(View.INVISIBLE);
        if (data != null){
            Movie[] movies = FavouritesCursorUtils.convertCursorDataToMovies(data);
            mCurrentMovies = movies;
            showMovieView();
            mMoviesAdapter.setMovieData(movies);
        } else {
            showErrorView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


//    Handles the callbacks for grabbing the list of movies
    private class MovieLoaderCallbacks implements LoaderManager.LoaderCallbacks<Movie[]>{

        @Override
        public Loader<Movie[]> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<Movie[]>(getBaseContext()) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public Movie[] loadInBackground() {
                    URL url = NetworkUtils.buildUrl(mApiKey, mSortOption);
                    Movie[] parsedMovieDetails = null;
                    try {
                        String movieResults = NetworkUtils.getResponseFromHttpUrl(url);
                        parsedMovieDetails = MovieDbJsonUtils.convertJsonToMovies(movieResults);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return parsedMovieDetails;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
            if (data != null){
                mCurrentMovies = data;
                showMovieView();
                mMoviesAdapter.setMovieData(data);
            } else {
                showErrorView();
            }
        }

        @Override
        public void onLoaderReset(Loader<Movie[]> loader) {

        }
    }


}
