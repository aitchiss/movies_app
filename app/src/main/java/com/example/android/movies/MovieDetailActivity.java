package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;


public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static String apiKey;

    private static final String MOVIE = "movie";
    private static final int TRAILER_DETAILS_LOADER = 101;
    private static final String MOVIE_ID_EXTRA = "movieId";

    private Movie mCurrentMovie;
    private TextView mTitle;
    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mRating;
    private ImageView mPoster;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //      Retrieve key String from secrets values resource
        apiKey = getString(R.string.my_api_key);

//        Get a reference to the error layout and standard layout
        LinearLayout movieDetailsErrorLayout = (LinearLayout) findViewById(R.id.layout_movie_details_error);
        ScrollView movieDetailsLayout = (ScrollView) findViewById(R.id.layout_movie_details);

//        Get a reference to all of the TextViews and ImageView
        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
        mReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        mRating = (TextView) findViewById(R.id.tv_movie_rating);
        mPoster = (ImageView) findViewById(R.id.iv_movie_poster);

//        Unpack the extras from the intent to get chosen movie
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

//        Populate the movie details if possible, otherwise show the error view
        if (b.getParcelable(MOVIE) != null){
            mCurrentMovie = b.getParcelable(MOVIE);
            populateMovieDetails();

            // Create a query bundle for Loader to get trailer details
            Bundle trailerQueryBundle = new Bundle();
            trailerQueryBundle.putInt(MOVIE_ID_EXTRA, mCurrentMovie.getId());
            Log.d("loader", "creating");

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> trailersLoader = loaderManager.getLoader(TRAILER_DETAILS_LOADER);
            if (trailersLoader == null){
                Log.d("loader", "init");
                loaderManager.initLoader(TRAILER_DETAILS_LOADER, trailerQueryBundle, this);
            } else {
                loaderManager.restartLoader(TRAILER_DETAILS_LOADER, trailerQueryBundle, this);
            }

        } else {
            movieDetailsLayout.setVisibility(View.INVISIBLE);
            movieDetailsErrorLayout.setVisibility(View.VISIBLE);
        }


    }

    private void populateMovieDetails(){
        mTitle.setText(mCurrentMovie.getTitle());
        mSynopsis.setText(mCurrentMovie.getSynopsis());
        mReleaseDate.setText(mCurrentMovie.getReleaseDate());
        mRating.setText(getString(R.string.rating_score, mCurrentMovie.getRating()));
        populatePoster();
    }

    private void populatePoster(){
        Picasso.with(this)
                .load(mCurrentMovie.getPosterPath())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_broken_img)
                .fit()
                .into(mPoster);
    }


    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                Log.d("loader", "on start");
                if (args == null){
                    Log.d("loader", "null");
                    return;
                }
                forceLoad();
//                TODO CONSIDER SETTING A LOADING INDICATOR FOR THIS VIEW
            }

            @Override
            public String loadInBackground() {
                Log.d("loader", "now");
                int movieId = args.getInt(MOVIE_ID_EXTRA);
                if (movieId < 0){
                    return null;
                }
                try {
//                    TODO MAKE A FUNCTION IN NETWORK UTILS TO GRAB THE TRAILER INFO
                    URL url = NetworkUtils.buildTrailerUrl(apiKey, movieId);
                    String results = NetworkUtils.getResponseFromHttpUrl(url);
                    Log.d("loader results", results);
                    return null;
                } catch (IOException e){
                    e.printStackTrace();
                    return null;
                }
//                TODO RETURN THE RESULTS
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d("loader", "finished");
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
