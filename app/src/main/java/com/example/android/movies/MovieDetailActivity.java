package com.example.android.movies;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b.getParcelable("movie") != null){
            Movie movieChoice = b.getParcelable("movie");
            Log.d("extras", movieChoice.getTitle());
        }


    }
}
