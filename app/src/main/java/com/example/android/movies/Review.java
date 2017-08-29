package com.example.android.movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suzanneaitchison on 21/08/2017.
 */

public class Review implements Parcelable{

    private String id;
    private String author;
    private String content;

    public Review(String id, String author, String content){
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public Review(Parcel parcel){
        this.id = parcel.readString();
        this.author = parcel.readString();
        this.content = parcel.readString();
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
    }

    public final static Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){

        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[0];
        }
    };
}
