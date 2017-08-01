package com.example.android.movies;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MovieDetailActivity extends AppCompatActivity {

    private LinearLayout mMovieDetailsErrorLayout;
    private ScrollView mMovieDetailsLayout;
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

//        Get a reference to the error layout and standard layout
        mMovieDetailsErrorLayout = (LinearLayout) findViewById(R.id.layout_movie_details_error);
        mMovieDetailsLayout = (ScrollView) findViewById(R.id.layout_movie_details);

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
        if (b.getParcelable("movie") != null){
            Movie movieChoice = b.getParcelable("movie");
            mCurrentMovie = movieChoice;
            populateMovieDetails();
        } else {
            mMovieDetailsLayout.setVisibility(View.INVISIBLE);
            mMovieDetailsErrorLayout.setVisibility(View.VISIBLE);
        }
    }

    private void populateMovieDetails(){
        mTitle.setText(mCurrentMovie.getTitle());
        mSynopsis.setText(mCurrentMovie.getSynopsis());
        mReleaseDate.setText(mCurrentMovie.getReleaseDate());
        mRating.setText(String.valueOf(mCurrentMovie.getRating()) + " / 10");
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
}
