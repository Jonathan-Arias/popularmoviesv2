package io.github.jonathan_arias.popularmoviesv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    private String API_KEY;
    private String BASE_POPULAR_URL;
    private String BASE_TOPRATED_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API_KEY = getResources().getString(R.string.API_KEY);
        BASE_POPULAR_URL = getResources().getString(R.string.base_popular_url);
        BASE_TOPRATED_URL = getResources().getString(R.string.base_toprated_url);

    }
}
