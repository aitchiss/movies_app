package com.example.android.movies;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie mCurrentMovie;
    private TextView mTitle;
    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

//        Get a reference to all of the TextViews
        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
        mReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        mRating = (TextView) findViewById(R.id.tv_movie_rating);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b.getParcelable("movie") != null){
            Movie movieChoice = b.getParcelable("movie");
            mCurrentMovie = movieChoice;
            populateMovieDetails();
        } else {
//            add in some kind of error handling
        }

    }

    private void populateMovieDetails(){
        mTitle.setText(mCurrentMovie.getTitle());
        mSynopsis.setText(mCurrentMovie.getSynopsis());
        mReleaseDate.setText(mCurrentMovie.getReleaseDate());
        mRating.setText(String.valueOf(mCurrentMovie.getRating()));
    }
}
