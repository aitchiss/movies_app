package com.example.android.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;




public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    public interface MovieClickHandler{
        void onClick(Movie movie);
    }

    private Movie[] mMovieData;
    private final MovieClickHandler mClickHandler;

    public MoviesAdapter(MovieClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {

    Picasso.with(holder.mMovieImageView.getContext())
            .load(mMovieData[position].getPosterPath())
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_broken_img)
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

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mMovieImageView;

        public MoviesAdapterViewHolder(View itemView){
            super(itemView);
            mMovieImageView = (ImageView) itemView.findViewById(R.id.iv_movie_list_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie selectedMovie = mMovieData[position];
            mClickHandler.onClick(selectedMovie);
        }
    }
}
