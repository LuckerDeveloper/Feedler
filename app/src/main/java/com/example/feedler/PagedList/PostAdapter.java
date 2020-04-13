package com.example.feedler.PagedList;

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

    public PostAdapter(DiffUtil.ItemCallback<Post> diffUtilCallback) {
        super(diffUtilCallback);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        PostViewHolder holder = new PostViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(getItem(position));
    }



}