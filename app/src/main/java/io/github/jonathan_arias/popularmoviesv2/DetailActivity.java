package io.github.jonathan_arias.popularmoviesv2;

import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private Movie movie;
    private ImageView backdropIV;
    private TextView releaseDateTV;
    private TextView voteAverageTV;
    private TextView overviewTV;
    private TextView videosVP;
    private TextView reviewsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        supportPostponeEnterTransition();

        // This prevents the action bar from flashing during animation
        getWindow().setEnterTransition(null);

        backdropIV = findViewById(R.id.backdrop);
        releaseDateTV = findViewById(R.id.release_date);
        voteAverageTV = findViewById(R.id.vote_average);
        overviewTV = findViewById(R.id.overview);
        videosVP = findViewById(R.id.tv_videos);
        reviewsTV = findViewById(R.id.tv_reviews);

        Bundle extras = getIntent().getExtras();
        String transitionName = extras.getString(MainActivity.EXTRA_TRANSITION_NAME);
        backdropIV.setTransitionName(transitionName);
        movie = extras.getParcelable(MainActivity.EXTRA_MOVIE);

        String movieVideosUrl = NetworkUtils.buildMovieDetailUrl(this, movie.getId(), "videos");
        String movieReviewsUrl = NetworkUtils.buildMovieDetailUrl(this, movie.getId(), "reviews");

        StringRequest videoRequest = buildStringRequestForVideos(movieVideosUrl);
        StringRequest reviewRequest = buildStringRequestForReviews(movieReviewsUrl);

        VolleySingleton.getInstance(this).addToRequestQueue(videoRequest);
        VolleySingleton.getInstance(this).addToRequestQueue(reviewRequest);

        populateUI();
    }

    private StringRequest buildStringRequestForReviews(String reviewsUrl){
        return new StringRequest(Request.Method.GET, reviewsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String reviewJsonResponse) {
                        try {
                            JSONObject jsonObject = new JSONObject(reviewJsonResponse);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject tmp = jsonArray.getJSONObject(i);
                                String author = tmp.getString("author");
                                String content = tmp.getString("content");
                                String url = tmp.getString("url");
                                String builder = new StringBuilder()
                                        .append(author)
                                        .append(": ")
                                        .append(content)
                                        .append("\n")
                                        .toString();
                                reviewsTV.append(builder);
                            }

                        } catch (JSONException ex){
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private StringRequest buildStringRequestForVideos(String videosUrl){
        return new StringRequest(Request.Method.GET, videosUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String movieJsonResponse) {
                        try {
                            JSONObject jsonObject = new JSONObject(movieJsonResponse);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject tmp = jsonArray.getJSONObject(i);
                                if (tmp.getString("site").equals("YouTube")){
                                    videosVP.append(tmp.getString("type"));
                                    videosVP.append(": ");
                                    videosVP.append(NetworkUtils.BASE_YOUTUBE_URL + tmp.getString("key"));
                                    videosVP.append("\n");
                                }
                            }
                        } catch (JSONException ex){
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateUI(){
        if (movie != null){
            String releaseDateText = getResources().getString(R.string.populateUI_release) + " " + movie.getReleaseDate();
            String voteAverageText = getResources().getString(R.string.populateUI_voteAvg) + " " + movie.getVoteAverage();
            String overviewText = getResources().getString(R.string.populateUI_overview) + " " + movie.getOverview();

            getSupportActionBar().setTitle(movie.getTitle());
            releaseDateTV.setText(releaseDateText);
            voteAverageTV.setText(voteAverageText);
            overviewTV.setText(overviewText);

            String backdropPath = NetworkUtils.BASE_IMAGE_URL + movie.getPosterPath();
            Picasso.with(this)
                    .load(backdropPath)
                    .noFade()
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
