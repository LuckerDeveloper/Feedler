package com.example.feedler.Favorites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.PagedList.PostAdapter;
import com.example.feedler.PagedList.PostViewHolder;
import com.example.feedler.Post;
import com.example.feedler.R;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private List<Post> posts;
    private PostAdapter.Listener listener;

    public FavoriteAdapter( Context context, List<Post> posts) {
        this.listener= (PostAdapter.Listener) context;
        this.posts=posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_window, parent, false);
        PostViewHolder holder = new PostViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);

        holder.favorite.setOnClickListener(v -> {
            if (post.favorite) {
                holder.favorite.setBackground(holder.favorite.getContext().getDrawable(R.drawable.grade_empty));
                post.favorite = false;
                listener.replaceFavoriteVar(post);
                listener.deleteFavorite(post);
            }
            else  {
                holder.favorite.setBackground(holder.favorite.getContext().getDrawable(R.drawable.grade));
                post.favorite= true;
                listener.replaceFavoriteVar(post);
                listener.insertFavorite(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
