package io.github.jonathan_arias.popularmoviesv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private Movie movie;
    private ImageView backdropIV;
    private TextView releaseDateTV;
    private TextView voteAverageTV;
    private TextView overviewTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        supportPostponeEnterTransition();

        backdropIV = findViewById(R.id.backdrop);
        releaseDateTV = findViewById(R.id.release_date);
        voteAverageTV = findViewById(R.id.vote_average);
        overviewTV = findViewById(R.id.overview);

        Bundle extras = getIntent().getExtras();
        String transitionName = extras.getString(MainActivity.EXTRA_TRANSITION_NAME);
        backdropIV.setTransitionName(transitionName);
        movie = extras.getParcelable(MainActivity.EXTRA_MOVIE);

        populateUI();
    }

    private void populateUI(){
        if (movie != null){
            String releaseDateText = getResources().getString(R.string.populateUI_release) + movie.getReleaseDate();
            String voteAverageText = getResources().getString(R.string.populateUI_voteAvg) + movie.getVoteAverage();
            String overviewText = getResources().getString(R.string.populateUI_overview) + movie.getOverview();

            getSupportActionBar().setTitle(movie.getTitle());
            releaseDateTV.setText(releaseDateText);
            voteAverageTV.setText(voteAverageText);
            overviewTV.setText(overviewText);

            String backdropPath = NetworkUtils.BASE_IMAGE_URL + movie.getPosterPath();
            Picasso.with(this)
                    .load(backdropPath)
                    .into(backdropIV, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            supportStartPostponedEnterTransition();
                        }
                    });
        }
    }
}
