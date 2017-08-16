package com.example.android.movies;

import android.os.Parcel;
import android.os.Parcelable;



public class Movie implements Parcelable {

    private final String title;
    private final String posterPath;
    private final String synopsis;
    private final String releaseDate;
    private final int rating;
    private final int id;

    public Movie(String title, String posterPath, String synopsis, String releaseDate, int rating, int id){
        this.title = title;
        this.posterPath = posterPath;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.id = id;
    }

    private Movie(Parcel parcel){
        this.title = parcel.readString();
        this.posterPath = parcel.readString();
        this.synopsis = parcel.readString();
        this.releaseDate = parcel.readString();
        this.rating = parcel.readInt();
        this.id = parcel.readInt();
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
        dest.writeInt(this.rating);
        dest.writeInt(this.id);
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

    public String getRating(){
        return String.valueOf(this.rating);
    }

    public int getId(){ return this.id; }

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
