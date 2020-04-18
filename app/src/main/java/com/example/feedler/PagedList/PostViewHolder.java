package com.example.feedler.PagedList;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;


import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.Post;
import com.example.feedler.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
     TextView postText;
    TextView mShare;

   public PostViewHolder(View view) {
        super(view);
        postText = (TextView) view.findViewById(R.id.postText);

        mShare = (TextView) view.findViewById(R.id.share);
        mShare.setOnClickListener(v->{
            int pos = getAdapterPosition();
            String textPost = "не могу получить текст" ;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, textPost);
            Intent chosenIntent = Intent.createChooser(intent, textPost);
            mShare.getContext().startActivity(chosenIntent);
        });
    }


    public void bind(Post post) {
        postText.setText(post.getPostText());
    }
}
