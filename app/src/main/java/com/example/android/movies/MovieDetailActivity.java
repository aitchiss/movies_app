package com.example.android.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.data.FavouritesContract;
import com.example.android.movies.utilities.MovieDbJsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;


public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, TrailersAdapter.TrailerClickHandler {

    private static String apiKey;

    private static final String MOVIE = "movie";
    private static final int TRAILER_DETAILS_LOADER = 101;
    private static final int REVIEW_DETAILS_LOADER = 102;
    private static final String MOVIE_ID_EXTRA = "movieId";

    private Movie mCurrentMovie;
    private TextView mTitle;
    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mRating;
    private ImageView mPoster;

    private Trailer[] mTrailers;
    private TrailersAdapter mTrailersAdapter;
    private RecyclerView mTrailersRecyclerView;

    private Review[] mReviews;
    private RecyclerView mReviewsRecyclerView;
    private ReviewsAdapter mReviewsAdapter;


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
            getTrailerInfo();
            getReviewInfo();

        } else {
            movieDetailsLayout.setVisibility(View.INVISIBLE);
            movieDetailsErrorLayout.setVisibility(View.VISIBLE);
        }

        setUpTrailersAdapter();
        setUpReviewsAdapter();
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

    private void getTrailerInfo(){

        if (NetworkUtils.isOnlineOrConnecting(this)){
            // Create a query bundle for Loader to get trailer details
            Bundle trailerQueryBundle = new Bundle();
            trailerQueryBundle.putInt(MOVIE_ID_EXTRA, mCurrentMovie.getId());
            initOrRestartLoader(trailerQueryBundle, TRAILER_DETAILS_LOADER);
        } else {
//            TODO - ADD AN ERROR MESSAGE FOR WHEN TRAILERS CANT BE LOADED
        }

    }

    private void getReviewInfo(){
        if (NetworkUtils.isOnlineOrConnecting(this)){
            // Create a query bundle for Loader to get review details
            Bundle reviewQueryBundle = new Bundle();
            reviewQueryBundle.putInt(MOVIE_ID_EXTRA, mCurrentMovie.getId());
            initOrRestartLoader(reviewQueryBundle, REVIEW_DETAILS_LOADER);

        } else {
//            TODO - ADD AN ERROR MESSAGE FOR WHEN REVIEWS CANT BE LOADED
        }
    }

    private void initOrRestartLoader(Bundle bundle, int loaderId){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(loaderId);
        if (loader == null){
            loaderManager.initLoader(loaderId, bundle, this);
        } else {
            loaderManager.restartLoader(loaderId, bundle, this);
        }
    }


    private void setUpTrailersAdapter(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers_list);
        mTrailersRecyclerView.setLayoutManager(layoutManager);
        mTrailersAdapter = new TrailersAdapter(this);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
    }

    private void setUpReviewsAdapter(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews_list);
        mReviewsRecyclerView.setLayoutManager(layoutManager);
        mReviewsAdapter = new ReviewsAdapter();
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
    }

    public void onClick(Trailer trailer){
        String trailerKey = trailer.getYouTubeKey();
        Uri youTubeTrailerUri = NetworkUtils.getYouTubeTrailerUri(trailerKey);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(youTubeTrailerUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void onFavouritesClick(View view) {
//        On Favourites button click, add the movie to the Favourites db
//        TODO ALTER THIS SO THAT IT DOESN'T PUT DUPLICATES IN THE DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID, mCurrentMovie.getId());
        contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE, mCurrentMovie.getTitle());
        contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH, mCurrentMovie.getPosterPath());
        contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_RATING, mCurrentMovie.getRating());
        contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_RELEASE_DATE, mCurrentMovie.getReleaseDate());
        contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_SYNOPSIS, mCurrentMovie.getSynopsis());

        Uri uri = getContentResolver().insert(FavouritesContract.FavouritesEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(getBaseContext(), R.string.added_to_favourites_conf, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public Loader<String> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null){
                    return;
                }
                forceLoad();
//                TODO CONSIDER SETTING A LOADING INDICATOR FOR THIS VIEW
            }

            @Override
            public String loadInBackground() {
                int movieId = args.getInt(MOVIE_ID_EXTRA);
                URL url;

                switch(id){
                    case TRAILER_DETAILS_LOADER:
                        url = NetworkUtils.buildTrailerUrl(apiKey, movieId);
                        break;
                    case REVIEW_DETAILS_LOADER:
                        url = NetworkUtils.buildReviewUrl(apiKey, movieId);
                        break;
                    default:
                        url = null;
                }

                if (movieId < 0){
                    return null;
                }
                try {
                    String results = NetworkUtils.getResponseFromHttpUrl(url);
                    return results;
                } catch (IOException e){
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data == null){
            return;
        }

        try {
            if (loader.getId() == TRAILER_DETAILS_LOADER){
                mTrailers = MovieDbJsonUtils.convertJsonToTrailers(data);
                mTrailersAdapter.setTrailerData(mTrailers);
            } else if (loader.getId() == REVIEW_DETAILS_LOADER){
                mReviews = MovieDbJsonUtils.convertJsonToReviews(data);
                mReviewsAdapter.setReviewData(mReviews);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
