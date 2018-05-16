package io.github.jonathan_arias.popularmoviesv2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private Context context;
    private List<Movie> movies;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ViewHolder(View view){
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview_movie);
        }

        @Override
        public void onClick(View view) {
            return;
        }
    }

    MovieAdapter(Context context, List<Movie> movies){
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recyclerview_movie_item, parent, false);
        view.setFocusable(true);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        String posterUrl = context.getResources().getString(R.string.base_image_url) + movie.getPosterPath();
        Picasso.with(context).load(posterUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

}
