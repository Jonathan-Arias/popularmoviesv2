package io.github.jonathan_arias.popularmoviesv2;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<Movie> movies;
    private final MovieAdapterOnClickHandler handler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie, ImageView sharedView);
    }

    public MovieAdapter(MovieAdapterOnClickHandler handler){
        this.handler = handler;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.imageview_movie);
        }
    }

    public void setMovieData(List<Movie> movieData){
        movies = movieData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recyclerview_movie_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Movie movie = movies.get(position);
        String posterUrl = NetworkUtils.BASE_IMAGE_URL + movie.getPosterPath();
        Picasso.with(holder.imageView.getContext()).load(posterUrl).into(holder.imageView);
        ViewCompat.setTransitionName(holder.imageView, movie.getTitle());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.onClick(movie, holder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (movies == null)
            return 0;
        return movies.size();
    }

}
