package com.example.android.movies.utilities;


import com.example.android.movies.Movie;
import com.example.android.movies.Review;
import com.example.android.movies.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MovieDbJsonUtils {


    public static Movie[] convertJsonToMovies(String jsonMovieDataString) throws JSONException{

//        Keys to access the data we want to store and return:
        final String MDB_ID = "id";
        final String MDB_TITLE = "title";
        final String MDB_POSTER = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_RATING = "vote_average";
        final String MDB_RESULTS = "results";
        final String MDB_POSTER_PATH_PREFIX = "http://image.tmdb.org/t/p/w185";

        JSONObject jsonMovies = new JSONObject(jsonMovieDataString);
        JSONArray jsonMovieArray = jsonMovies.getJSONArray(MDB_RESULTS);

        Movie[] movies =  new Movie[jsonMovieArray.length()];

//        Loops over the JSON array, and creates a Movie object, storing in the movies array

        for (int i = 0; i < jsonMovieArray.length(); i++){
            JSONObject jsonMovie = jsonMovieArray.getJSONObject(i);

            int id = jsonMovie.getInt(MDB_ID);
            String title = jsonMovie.getString(MDB_TITLE);
            String poster = MDB_POSTER_PATH_PREFIX + jsonMovie.getString(MDB_POSTER);
            String synopsis = jsonMovie.getString(MDB_OVERVIEW);
            String releaseDate = convertToUKDate(jsonMovie.getString(MDB_RELEASE_DATE));
            int rating = jsonMovie.getInt(MDB_RATING);

            Movie movie = new Movie(title, poster, synopsis, releaseDate, rating, id);
            movies[i] = movie;
        }

        return movies;
    }

    public static Trailer[] convertJsonToTrailers(String jsonTrailerString) throws JSONException {
        final String TRAILER_ID = "id";
        final String TRAILER_RESULTS = "results";
        final String TRAILER_NAME = "name";
        final String TRAILER_KEY = "key";

        JSONObject jsonTrailers = new JSONObject(jsonTrailerString);
        JSONArray jsonTrailerArray = jsonTrailers.getJSONArray(TRAILER_RESULTS);

        Trailer[] trailers = new Trailer[jsonTrailerArray.length()];

        for (int i = 0; i < jsonTrailerArray.length(); i++){
            JSONObject jsonTrailer = jsonTrailerArray.getJSONObject(i);

            String id = jsonTrailer.getString(TRAILER_ID);
            String name = jsonTrailer.getString(TRAILER_NAME);
            String youTubeKey = jsonTrailer.getString(TRAILER_KEY);

            Trailer trailer = new Trailer(id, name, youTubeKey);
            trailers[i] = trailer;
        }
        return trailers;
    }

    public static Review[] convertJsonToReviews(String jsonReviewString) throws JSONException{
        final String REVIEW_ID = "id";
        final String REVIEW_RESULTS = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        JSONObject jsonReviews = new JSONObject(jsonReviewString);
        JSONArray jsonReviewsArray = jsonReviews.getJSONArray(REVIEW_RESULTS);

        Review[] reviews = new Review[jsonReviewsArray.length()];

        for (int i = 0; i < jsonReviewsArray.length(); i++){
            JSONObject jsonReview = jsonReviewsArray.getJSONObject(i);

            String id = jsonReview.getString(REVIEW_ID);
            String author = jsonReview.getString(REVIEW_AUTHOR);
            String content = jsonReview.getString(REVIEW_CONTENT);

            Review review = new Review(id, author, content);
            reviews[i] = review;

        }
        return reviews;
    }

    private static String convertToUKDate(String date){
        String[] dateComponents = date.split("-");
        return dateComponents[2] + "/" + dateComponents[1] + "/" + dateComponents[0];
    }

}
