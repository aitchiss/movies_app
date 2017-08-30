package com.example.android.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by suzanneaitchison on 18/08/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    public interface TrailerClickHandler{
        void onClick(Trailer trailer);
    }

    private Trailer[] mTrailerData;
    private TrailerClickHandler mTrailerClickHandler;

    public TrailersAdapter(TrailerClickHandler trailerClickHandler){
        this.mTrailerClickHandler = trailerClickHandler;
    }


    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);
        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder holder, int position) {
        holder.mTrailerName.setText(mTrailerData[position].getName());
    }

    @Override
    public int getItemCount() {
        if (mTrailerData == null){
            return 0;
        } else {
            return mTrailerData.length;
        }
    }

    public void setTrailerData(Trailer[] trailers){
        mTrailerData = trailers;
        notifyDataSetChanged();
    }

    class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mTrailerName;

        public TrailersAdapterViewHolder(View itemView){
            super(itemView);
            mTrailerName = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Trailer trailer = mTrailerData[position];
            mTrailerClickHandler.onClick(trailer);
        }
    }
}
