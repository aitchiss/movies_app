package com.example.android.movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suzanneaitchison on 17/08/2017.
 */

public class Trailer implements Parcelable {

    private final String id;
    private final String name;
    private final String youTubeKey;

    public Trailer(String id, String name, String youTubeKey){
        this.id = id;
        this.name = name;
        this.youTubeKey = youTubeKey;
    }

    public Trailer(Parcel parcel){
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.youTubeKey = parcel.readString();
    }

    public String getName(){
        return this.name;
    }

    public String getYouTubeKey(){
        return this.youTubeKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(youTubeKey);
    }

    public final static Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>(){

        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[0];
        }
    };
}
