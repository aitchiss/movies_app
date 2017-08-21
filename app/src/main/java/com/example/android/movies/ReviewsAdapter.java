package com.example.android.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by suzanneaitchison on 21/08/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder>{

    private Review[] mReviewdata;

    @Override
    public ReviewsAdapter.ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutId = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);
        return new ReviewsAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewsAdapterViewHolder holder, int position) {
        Review review = mReviewdata[position];
        holder.mAuthor.setText(review.getAuthor());
        holder.mContent.setText(review.getContent());

    }

    public void setReviewData(Review[] reviews){
        mReviewdata = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mReviewdata == null){
            return 0;
        } else {
            return mReviewdata.length;
        }
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView mAuthor;
        private TextView mContent;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            mContent = (TextView) itemView.findViewById(R.id.tv_review_content);
        }
    }
}

