package io.github.jonathan_arias.popularmoviesv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
    implements MovieAdapter.MovieAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView rvMovies;
    private TextView tvErrorMsg;
    private ProgressBar pbLoadingIcon;
    private MovieAdapter movieAdapter;
    private final int NUM_COLUMNS = 2;
    private static final int MOVIE_LOADER_ID = 21;
    private static boolean PREFERENCES_UPDATED = false;

    public static final String EXTRA_MOVIE = "extra_movie";
    public static final String EXTRA_TRANSITION_NAME = "extra_transition_name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This prevents the action bar from flashing during animation
        getWindow().setExitTransition(null);

        rvMovies = findViewById(R.id.recyclerview_movies);
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        pbLoadingIcon = findViewById(R.id.pb_loading_icon);

        rvMovies.setLayoutManager(new GridLayoutManager(this, NUM_COLUMNS));
        movieAdapter = new MovieAdapter(this);
        rvMovies.setAdapter(movieAdapter);

        VolleySingleton.getInstance(this.getApplicationContext());

        StringRequest stringRequest = buildListStringRequest(NetworkUtils.buildListUrl(this));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    private StringRequest buildListStringRequest(String url){
        return new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String movieJsonResponse) {
                        pbLoadingIcon.setVisibility(View.INVISIBLE);
                        if (movieJsonResponse == null){
                            showLoadUnsuccessful();
                            return;
                        }
                        final String RESULTS = "results";
                        final String TITLE = "title";
                        final String VOTE_AVG = "vote_average";
                        final String OVERVIEW = "overview";
                        final String RELEASE_DATE = "release_date";
                        final String POSTER_PATH = "poster_path";
                        final String BACKDROP_PATH = "backdrop_path";
                        final String ID = "id";

                        ArrayList<Movie> movies = new ArrayList<>();
                        try {
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
                                int movie_id = tmp.getInt(ID);
                                movies.add(new Movie(title, vote_avg, overview, release_date, poster_path, backdrop_path, movie_id));
                            }
                        } catch (JSONException ex){
                            ex.printStackTrace();
                        }
                        movieAdapter.setMovieData(movies);
                        showLoadSuccessful();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoadUnsuccessful();
            }
        });
    }

    public void showLoadSuccessful(){
        rvMovies.setVisibility(View.VISIBLE);
        tvErrorMsg.setVisibility(View.INVISIBLE);
    }

    public void showLoadUnsuccessful(){
        rvMovies.setVisibility(View.INVISIBLE);
        tvErrorMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie selectedMovie, ImageView sharedView){
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, selectedMovie);
        intent.putExtra(EXTRA_TRANSITION_NAME, ViewCompat.getTransitionName(sharedView));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedView,
                ViewCompat.getTransitionName(sharedView));
        startActivity(intent, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPreferences();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        checkPreferences();
    }

    private void checkPreferences(){
        if (PREFERENCES_UPDATED){
            pbLoadingIcon.setVisibility(View.VISIBLE);
            rvMovies.setVisibility(View.INVISIBLE);
            String updated = NetworkUtils.buildListUrl(this);
            VolleySingleton.getInstance(this).addToRequestQueue(buildListStringRequest(updated));
            PREFERENCES_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_UPDATED = true;
    }
}
