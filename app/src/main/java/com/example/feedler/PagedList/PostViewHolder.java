package com.example.feedler.PagedList;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.Post;
import com.example.feedler.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    final TextView postText;
    PostViewHolder(View view){
        super(view);
        postText = (TextView) view.findViewById(R.id.postText);


    }
    public void bind(Post post) {
        postText.setText(post.getPostText());
    }
}
