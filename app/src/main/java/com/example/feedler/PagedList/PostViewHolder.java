package com.example.feedler.PagedList;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.Post;
import com.example.feedler.R;

class PostViewHolder extends RecyclerView.ViewHolder {
    final private TextView postText;
    PostViewHolder(View view){
        super(view);
        postText = view.findViewById(R.id.postText);


    }
    void bind(Post post) {
        postText.setText(post.getDate()+' '+post.getGroupName());
    }
}
