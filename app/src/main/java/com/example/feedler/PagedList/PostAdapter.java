package com.example.feedler.PagedList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.feedler.Post;
import com.example.feedler.R;

public class PostAdapter extends PagedListAdapter<Post, PostViewHolder> {

    private Listener listener ;

    public PostAdapter(DiffUtil.ItemCallback<Post> diffUtilCallback, Context context) {
        super(diffUtilCallback);
//        this.listener= (Listener) context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        final Post post=getItem(position);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.insertFavorite(post);
//            }
//        });
//
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                listener.deleteFavorite(post);
//                return true;
//            }
//        });

        holder.bind(getItem(position));
    }


    public interface Listener{
        void insertFavorite(Post post);

        void  deleteFavorite(Post post);
    }
}