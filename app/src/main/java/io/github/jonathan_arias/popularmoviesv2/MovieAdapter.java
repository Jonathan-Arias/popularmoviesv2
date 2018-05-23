package io.github.jonathan_arias.popularmoviesv2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<Movie> movies;
    private Context context;

    final private MovieAdapterOnClickHandler handler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler handler){
        this.handler = handler;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView imageView;
        ViewHolder(View view){
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview_movie);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie selectedMovie = movies.get(adapterPosition);
            handler.onClick(selectedMovie);
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
        view.setFocusable(true);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        String posterUrl = NetworkUtils.BASE_IMAGE_URL + movie.getPosterPath();
        Picasso.with(context).load(posterUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (movies == null)
            return 0;
        return movies.size();
    }

}
