package com.example.android.movies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class MovieDetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerClickHandler {

    private static String apiKey;

    private static final String MOVIE = "movie";
    private static final String TRAILERS = "trailers";
    private static final String REVIEWS = "reviews";

    private static final int TRAILER_DETAILS_LOADER = 101;
    private static final int REVIEW_DETAILS_LOADER = 102;
    private static final int FAVOURITES_DELETE_LOADER = 103;
    private static final int FAVOURITES_INSERT_LOADER = 104;
    private static final String MOVIE_ID_EXTRA = "movieId";
    private static final String MOVIE_TITLE_EXTRA = "movieTitle";
    private static final String MOVIE_POSTER_EXTRA = "moviePoster";
    private static final String MOVIE_RELEASE_EXTRA = "movieRelease";
    private static final String MOVIE_RATING_EXTRA = "movieRating";
    private static final String MOVIE_SYNOPSIS_EXTRA = "movieSynopsis";

    private Movie mCurrentMovie;
    private TextView mTitle;
    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mRating;
    private ImageView mPoster;
    private TextView mFavouritesText;
    private ImageView mFavouritesIcon;
    private LinearLayout mTrailerLoadingError;
    private LinearLayout mReviewLoadingError;

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
        mFavouritesText = (TextView) findViewById(R.id.tv_favourites_text);
        mFavouritesIcon = (ImageView) findViewById(R.id.iv_favourite_icon);
        mTrailerLoadingError = (LinearLayout) findViewById(R.id.trailer_loading_error);
        mReviewLoadingError = (LinearLayout) findViewById(R.id.review_loading_error);

//        Unpack the extras from the intent to get chosen movie
        Intent intent = getIntent();
        Bundle b = intent.getExtras();


        setUpTrailersAdapter();
        setUpReviewsAdapter();


        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE)){
            mCurrentMovie = savedInstanceState.getParcelable(MOVIE);
            mReviews = (Review[]) savedInstanceState.getParcelableArray(REVIEWS);
            mTrailers = (Trailer[]) savedInstanceState.getParcelableArray(TRAILERS);
            populateMovieDetails();
            updateFavouritesView();
            mReviewsAdapter.setReviewData(mReviews);
            mTrailersAdapter.setTrailerData(mTrailers);

        } else {
            //        Populate the movie details if possible, otherwise show the error view
            if (b.getParcelable(MOVIE) != null){
                mCurrentMovie = b.getParcelable(MOVIE);
                populateMovieDetails();
                getTrailerInfo();
                getReviewInfo();
                updateFavouritesView();

            } else {
                movieDetailsLayout.setVisibility(View.INVISIBLE);
                movieDetailsErrorLayout.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE, mCurrentMovie);
        outState.putParcelableArray(TRAILERS, mTrailers);
        outState.putParcelableArray(REVIEWS, mReviews);

        super.onSaveInstanceState(outState);
    }


    //    Checks if the current movie is already in favourites, and offers delete option if it is, and add to faves option if not
    private void updateFavouritesView(){
        if (isAlreadyInFavourites()){
            mFavouritesText.setText(R.string.remove_from_favourites);
            mFavouritesIcon.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_favourited, null));
        } else {
            mFavouritesText.setText(R.string.add_to_favourites);
            mFavouritesIcon.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star, null));
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

    private void getTrailerInfo(){
//      Only load trailer details via network call if online
        if (NetworkUtils.isOnlineOrConnecting(this)){
            // Create a query bundle for Loader to get trailer details
            Bundle trailerQueryBundle = new Bundle();
            trailerQueryBundle.putInt(MOVIE_ID_EXTRA, mCurrentMovie.getId());
            initOrRestartLoader(trailerQueryBundle, TRAILER_DETAILS_LOADER);
        } else {
            mTrailerLoadingError.setVisibility(View.VISIBLE);
        }

    }

    private void getReviewInfo(){
//        Only load review details via network call if online
        if (NetworkUtils.isOnlineOrConnecting(this)){
            // Create a query bundle for Loader to get review details
            Bundle reviewQueryBundle = new Bundle();
            reviewQueryBundle.putInt(MOVIE_ID_EXTRA, mCurrentMovie.getId());
            initOrRestartLoader(reviewQueryBundle, REVIEW_DETAILS_LOADER);

        } else {
            mReviewLoadingError.setVisibility(View.VISIBLE);
        }
    }

    private void initOrRestartLoader(Bundle bundle, int loaderId){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader loader = loaderManager.getLoader(loaderId);

        if (loader == null){
            if (loaderId == FAVOURITES_DELETE_LOADER || loaderId == FAVOURITES_INSERT_LOADER){
                loaderManager.initLoader(loaderId, bundle, new IntegerLoaderCallbacks());
            } else {
                loaderManager.initLoader(loaderId, bundle, new StringLoaderCallbacks());
            }

        } else {
            if (loaderId == FAVOURITES_DELETE_LOADER || loaderId == FAVOURITES_INSERT_LOADER){
                loaderManager.restartLoader(loaderId, bundle, new IntegerLoaderCallbacks());
            } else {
                loaderManager.restartLoader(loaderId, bundle, new StringLoaderCallbacks());
            }
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
//        On Favourites button click, either add or remove the movie from the Favourites DB
        if (isAlreadyInFavourites()){
            removeFromFavourites();
        } else {
            addToFavourites();
        }

    }

    public void removeFromFavourites(){
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID_EXTRA, mCurrentMovie.getId());
        initOrRestartLoader(bundle, FAVOURITES_DELETE_LOADER);
    }

    public void addToFavourites(){

        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID_EXTRA, mCurrentMovie.getId());
        bundle.putString(MOVIE_TITLE_EXTRA, mCurrentMovie.getTitle());
        bundle.putString(MOVIE_POSTER_EXTRA, mCurrentMovie.getPosterPath());
        bundle.putString(MOVIE_RATING_EXTRA, mCurrentMovie.getRating());
        bundle.putString(MOVIE_RELEASE_EXTRA, mCurrentMovie.getReleaseDate());
        bundle.putString(MOVIE_SYNOPSIS_EXTRA, mCurrentMovie.getSynopsis());

        initOrRestartLoader(bundle, FAVOURITES_INSERT_LOADER);
    }

    public boolean isAlreadyInFavourites(){
//        Check if the movie is already in the Favourites database
        Uri uri = FavouritesContract.FavouritesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mCurrentMovie.getId())).build();
        Cursor resultsCursor = getContentResolver().query(uri, null, null, null, null);
        if (resultsCursor.getCount() > 0){
            return true;
        } else {
            return false;
        }
    }


    public class IntegerLoaderCallbacks implements LoaderManager.LoaderCallbacks<Integer>{

        @Override
        public Loader<Integer> onCreateLoader(final int id, final Bundle args) {
            return new AsyncTaskLoader<Integer>(getBaseContext()) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (args == null){
                        return;
                    }
                    forceLoad();
                }

                @Override
                public Integer loadInBackground() {
                    String movieId = String.valueOf(args.getInt(MOVIE_ID_EXTRA));
//                    To update the UI, we need to know that the requested update has been made to at least one matching row
                    int updatedRows;
                    try {
                        switch(id){
                            case FAVOURITES_DELETE_LOADER:
                                Uri uri = FavouritesContract.FavouritesEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
                                updatedRows = getContentResolver().delete(uri,null, null);
                                break;
                            case FAVOURITES_INSERT_LOADER:
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID, args.getInt(MOVIE_ID_EXTRA));
                                contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE, args.getString(MOVIE_TITLE_EXTRA));
                                contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH, args.getString(MOVIE_POSTER_EXTRA));
                                contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_RATING, args.getString(MOVIE_RATING_EXTRA));
                                contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_RELEASE_DATE, args.getString(MOVIE_RELEASE_EXTRA));
                                contentValues.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_SYNOPSIS, args.getString(MOVIE_SYNOPSIS_EXTRA));

                                Uri returnUri = getContentResolver().insert(FavouritesContract.FavouritesEntry.CONTENT_URI, contentValues);

                                if (returnUri != null) {
                                    updatedRows =  1;
                                } else {
                                    updatedRows = 0;
                                }
                                break;
                            default:
                                updatedRows = 0;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        updatedRows = 0;
                    }

                    return updatedRows;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Integer> loader, Integer data) {

            if (data <= 0 || data == null){
                return;
            }
//            As long as at least one row has been updated, reflect the update (deletion or insertion) in the UI
            if (loader.getId() == FAVOURITES_DELETE_LOADER){
                updateFavouritesView();
                Toast.makeText(getBaseContext(), R.string.confirm_fav_deleted, Toast.LENGTH_LONG).show();
            } else if (loader.getId() == FAVOURITES_INSERT_LOADER){
                Toast.makeText(getBaseContext(), R.string.added_to_favourites_conf, Toast.LENGTH_LONG).show();
                updateFavouritesView();
            }
        }

        @Override
        public void onLoaderReset(Loader<Integer> loader) {

        }
    }

    public class StringLoaderCallbacks implements LoaderManager.LoaderCallbacks<String>{

        @Override
        public Loader<String> onCreateLoader(final int id, final Bundle args) {
            return new AsyncTaskLoader<String>(getBaseContext()) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (args == null){
                        return;
                    }
                    forceLoad();
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
                if (loader.getId() == TRAILER_DETAILS_LOADER && data != null){
                    mTrailers = MovieDbJsonUtils.convertJsonToTrailers(data);
                    mTrailerLoadingError.setVisibility(View.INVISIBLE);
                    mTrailersAdapter.setTrailerData(mTrailers);
                } else if (loader.getId() == REVIEW_DETAILS_LOADER && data != null){
                    mReviewLoadingError.setVisibility(View.INVISIBLE);
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


}
