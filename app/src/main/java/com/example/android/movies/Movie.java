package com.example.android.movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suzanneaitchison on 25/07/2017.
 */

public class Movie implements Parcelable {

    private String title;
    private String posterPath;
    private String synopsis;
    private String releaseDate;

    public Movie(String title, String posterPath, String synopsis, String releaseDate){
        this.title = title;
        this.posterPath = posterPath;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
    }

    public Movie(Parcel parcel){
        this.title = parcel.readString();
        this.posterPath = parcel.readString();
        this.synopsis = parcel.readString();
        this.releaseDate = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.posterPath);
        dest.writeString(this.synopsis);
        dest.writeString(this.releaseDate);
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };
}
