package io.github.jonathan_arias.popularmoviesv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.MovieAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView recyclerView;
    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIcon;
    private MovieAdapter movieAdapter;
    private final int NUMBEROFCOLUMNS = 2;
    private static final int MOVIE_LOADER_ID = 21;
    private static boolean PREFERENCES_UPDATED = false;

    public static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    public static final String EXTRA_TRANSITION_NAME = "EXTRA_TRANSITION_NAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        tvErrorMessage = (TextView) findViewById(R.id.tv_error_msg);
        pbLoadingIcon = (ProgressBar) findViewById(R.id.pb_loading_icon);

        recyclerView.setLayoutManager(new GridLayoutManager(this, NUMBEROFCOLUMNS));
        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);

        LoaderManager.LoaderCallbacks<List<Movie>> callbacks = MainActivity.this;
        Bundle bundle = null;
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle, callbacks);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @SuppressLint("StaticFieldLeak")
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
                String preferredSortOrder = SettingsFragment.getPreferredSortOrder(getBaseContext());
                URL url = NetworkUtils.buildUrl(getBaseContext(), preferredSortOrder);
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                    List<Movie> movieData = NetworkUtils.getMovieDataFromJson(jsonResponse);
                    return movieData;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
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
        movieAdapter.setMovieData(data);
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
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
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
