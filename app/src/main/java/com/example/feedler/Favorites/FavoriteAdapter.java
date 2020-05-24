package com.example.feedler.Favorites;

import android.content.Context;
import android.content.Intent;
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
import com.example.feedler.PostActivity;
import com.example.feedler.R;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private List<Post> posts;
    private final LayoutInflater mInflater;
    Context context;

    public FavoriteAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context= context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_window, parent, false);
        PostViewHolder holder = new PostViewHolder(view, context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
                if(context instanceof FavoriteActivity){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(context, PostActivity.class);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(posts!=null){
            return posts.size();
        }
        else {
            return 0;
        }
    }

    public void setPosts(List<Post> posts){
        this.posts = posts;
        notifyDataSetChanged();
    }

}
