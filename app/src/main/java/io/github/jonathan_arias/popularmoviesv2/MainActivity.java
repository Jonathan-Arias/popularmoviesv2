package io.github.jonathan_arias.popularmoviesv2;

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


public class MainActivity extends AppCompatActivity {
    private String API_KEY;
    private String BASE_POPULAR_URL;
    private String BASE_TOPRATED_URL;

    private RecyclerView recyclerView;
    private TextView textView;
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
        textView = (TextView) findViewById(R.id.tv_error);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUMBEROFCOLUMNS);

        recyclerView.setLayoutManager(gridLayoutManager);

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
                textView.setVisibility(View.VISIBLE);
            }
        });

        queue.add(stringRequest);

    }
}
