package io.github.jonathan_arias.popularmoviesv2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>{
    private String API_KEY;
    private String BASE_POPULAR_URL;
    private String BASE_TOPRATED_URL;

    private RecyclerView recyclerView;
    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIcon;
    private final int NUMBEROFCOLUMNS = 2;

    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API_KEY = getResources().getString(R.string.API_KEY);
        BASE_POPULAR_URL = getResources().getString(R.string.base_popular_url);
        BASE_TOPRATED_URL = getResources().getString(R.string.base_toprated_url);

        String url = BASE_POPULAR_URL + API_KEY;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        tvErrorMessage = (TextView) findViewById(R.id.tv_error_msg);
        pbLoadingIcon = (ProgressBar) findViewById(R.id.pb_loading_icon);

        recyclerView.setLayoutManager(new GridLayoutManager(this, NUMBEROFCOLUMNS));

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<Movie> movies = new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject t = jsonArray.getJSONObject(i);
                                String title = t.getString("title");
                                double voteAverage = t.getDouble("vote_average");
                                String overview = t.getString("overview");
                                String releaseDate = t.getString("release_date");
                                String posterPath = t.getString("poster_path");
                                String backdropPath = t.getString("backdrop_path");
                                movies.add(new Movie(title, voteAverage, overview, releaseDate,
                                        posterPath, backdropPath));
                            }

                            movieAdapter = new MovieAdapter(getApplicationContext(), movies);
                            recyclerView.setAdapter(movieAdapter);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvErrorMessage.setVisibility(View.VISIBLE);
            }
        });

        queue.add(stringRequest);

    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            List<Movie> movies = null;

            @Override
            protected void onStartLoading() {
                if (movies != null){
                    deliverResult(movies);
                } else {
                    pbLoadingIcon.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Movie> loadInBackground() {
                String url = BASE_POPULAR_URL + API_KEY;
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return movies;
            }

            @Override
            public void deliverResult(@Nullable List<Movie> data) {
                movies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        pbLoadingIcon.setVisibility(View.INVISIBLE);
        movieAdapter = new MovieAdapter(getApplicationContext(), data);
        recyclerView.setAdapter(movieAdapter);
        if (null == data){
            recyclerView.setVisibility(View.INVISIBLE);
            tvErrorMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvErrorMessage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }
}
