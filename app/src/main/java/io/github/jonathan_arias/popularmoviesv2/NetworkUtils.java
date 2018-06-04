package io.github.jonathan_arias.popularmoviesv2;

import android.content.Context;

public final class NetworkUtils {
    public static final String BASE_MOVIEDB_URL = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w342";
    public static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public static String buildListUrl(Context context){
        return new StringBuilder()
                .append(BASE_MOVIEDB_URL)
                .append(SettingsFragment.getPreferredSortOrder(context))
                .append("?")
                .append("api_key=")
                .append(context.getResources().getString(R.string.API_KEY))
                .toString();
    }

    // String req will either be videos or reviews
    public static String buildMovieDetailUrl(Context context, int id, String req){
        return new StringBuilder()
                .append(BASE_MOVIEDB_URL)
                .append(id)
                .append("/")
                .append(req)
                .append("?")
                .append("api_key=")
                .append(context.getResources().getString(R.string.API_KEY))
                .toString();
    }

    public static String buildYTUrl(Context context, String key){
        return new StringBuilder()
                .append(BASE_YOUTUBE_URL)
                .append(key)
                .append("?")
                .append("api_key=")
                .append(context.getResources().getString(R.string.API_KEY))
                .toString();
    }
}
