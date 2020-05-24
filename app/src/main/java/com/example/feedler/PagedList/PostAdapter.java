package com.example.feedler.PagedList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.feedler.Images.Image;
import com.example.feedler.MainActivity;
import com.example.feedler.Post;
import com.example.feedler.PostActivity;
import com.example.feedler.PostViewModel;
import com.example.feedler.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends PagedListAdapter<Post, PostViewHolder> {

    Context context;
    private Listener listener ;

    public PostAdapter(DiffUtil.ItemCallback<Post> diffUtilCallback, Context context) {
        super(diffUtilCallback);
        this.listener= (Listener) context;
        this.context=context;
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
        Post post = getItem(position);
        holder.bind(post);

        holder.itemView.findViewById(R.id.post_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostViewModel.postForTransmission=post;
                Intent intent= new Intent(context, PostActivity.class);
                context.startActivity(intent);
            }
        });

        holder.favorite.setOnClickListener(v -> {
            if (post.favorite) {
                listener.deleteFavorite(post);
                holder.favorite.setBackground(holder.favorite.getContext().getDrawable(R.drawable.grade_empty));


            }  else  {
                post.favorite=true;
                listener.insertFavorite(post);
                holder.favorite.setBackground(holder.favorite.getContext().getDrawable(R.drawable.grade));
            }
        });
    }




    public interface Listener{
        void insertFavorite(Post post);

        void  deleteFavorite(Post post);

    }

}