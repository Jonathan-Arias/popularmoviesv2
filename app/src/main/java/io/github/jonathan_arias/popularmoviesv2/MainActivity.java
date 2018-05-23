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

import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>{

    private RecyclerView recyclerView;
    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIcon;
    private MovieAdapter movieAdapter;
    private final int NUMBEROFCOLUMNS = 2;
    private static final int MOVIE_LOADER_ID = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        tvErrorMessage = (TextView) findViewById(R.id.tv_error_msg);
        pbLoadingIcon = (ProgressBar) findViewById(R.id.pb_loading_icon);

        recyclerView.setLayoutManager(new GridLayoutManager(this, NUMBEROFCOLUMNS));
        movieAdapter = new MovieAdapter();
        movieAdapter.setContext(this);
        recyclerView.setAdapter(movieAdapter);

        LoaderManager.LoaderCallbacks<List<Movie>> callbacks = MainActivity.this;
        Bundle bundle = null;
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle, callbacks);
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
                String preferredSortOrder = "popular";
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
}
