package com.example.android.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by suzanneaitchison on 24/07/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private Movie[] mMovieData;

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        MoviesAdapterViewHolder viewHolder = new MoviesAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {

    Picasso.with(holder.mMovieImageView.getContext())
            .load(mMovieData[position].getPosterPath())
            .placeholder(R.drawable.ic_img_placeholder)
            .error(R.drawable.ic_broken_image)
            .fit()
            .into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null){
            return 0;
        } else {
            return mMovieData.length;
        }
    }

    public void setMovieData(Movie[] movieData){
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder{

        public final ImageView mMovieImageView;

        public MoviesAdapterViewHolder(View itemView){
            super(itemView);
            mMovieImageView = (ImageView) itemView.findViewById(R.id.iv_movie_list_item);
        }
    }
}
