package io.github.jonathan_arias.popularmoviesv2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class NetworkUtils {
    private static final String BASE_MOVIEDB_URL = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w342";
    public static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public static URL buildUrl(Context context, String preferredSortOrder){
        StringBuilder builder = new StringBuilder()
                .append(BASE_MOVIEDB_URL)
                .append(preferredSortOrder + "?")
                .append("api_key=" + context.getResources().getString(R.string.API_KEY));
        URL url = null;
        try {
            url = new URL(builder.toString());
        } catch (MalformedURLException ex){
            ex.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<Movie> getMovieDataFromJson(String movieJsonResponse) throws JSONException {
        if (movieJsonResponse == null){
            return null;
        }
        final String RESULTS = "results";
        final String TITLE = "title";
        final String VOTE_AVG = "vote_average";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String BACKDROP_PATH = "backdrop_path";

        ArrayList<Movie> movies = new ArrayList<>();
        JSONObject movieJson = new JSONObject(movieJsonResponse);
        JSONArray jsonArray = movieJson.getJSONArray(RESULTS);

        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject tmp = jsonArray.getJSONObject(i);
            String title = tmp.getString(TITLE);
            Double vote_avg = tmp.getDouble(VOTE_AVG);
            String overview = tmp.getString(OVERVIEW);
            String release_date = tmp.getString(RELEASE_DATE);
            String poster_path = tmp.getString(POSTER_PATH);
            String backdrop_path = tmp.getString(BACKDROP_PATH);
            int movie_id = tmp.getInt("id");
            movies.add(new Movie(title, vote_avg, overview, release_date, poster_path, backdrop_path, movie_id));
        }

        return movies;
    }
}
