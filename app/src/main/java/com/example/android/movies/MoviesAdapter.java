package com.example.android.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by suzanneaitchison on 24/07/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private HashMap<String, String>[] mMovieData;

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
        holder.mMovieTextView.setText(mMovieData[position].get("title"));
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null){
            return 0;
        } else {
            return mMovieData.length;
        }
    }

    public void setMovieData(HashMap<String, String>[] movieData){
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder{

        public final TextView mMovieTextView;

        public MoviesAdapterViewHolder(View itemView){
            super(itemView);
            mMovieTextView = (TextView) itemView.findViewById(R.id.tv_movie_title);
        }
    }
}
