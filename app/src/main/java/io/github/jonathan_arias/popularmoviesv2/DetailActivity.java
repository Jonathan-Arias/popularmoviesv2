package io.github.jonathan_arias.popularmoviesv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private Movie movie;
    private ImageView backdrop;
    private TextView release_date;
    private TextView voteAverage;
    private TextView overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backdrop = (ImageView) findViewById(R.id.backdrop);
        release_date = (TextView) findViewById(R.id.release_date);
        voteAverage = (TextView) findViewById(R.id.vote_average);
        overview = (TextView) findViewById(R.id.overview);

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra(Intent.EXTRA_TEXT)){
                movie = intent.getParcelableExtra(Intent.EXTRA_TEXT);
                populateUI();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateUI(){
        if (movie != null){
            String backdropPath = NetworkUtils.BASE_IMAGE_URL + movie.getBackdropPath();
            Picasso.with(this).load(backdropPath).into(backdrop);

            getSupportActionBar().setTitle(movie.getTitle());

            release_date.setText("Released: " + movie.getReleaseDate());
            voteAverage.setText("Average Rating: " + movie.getVoteAverage());
            overview.setText("Synopsis: " + movie.getOverview());
        }
    }
}
