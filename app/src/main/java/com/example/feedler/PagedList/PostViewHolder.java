package com.example.feedler.PagedList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.Favorites.FavoriteActivity;
import com.example.feedler.MainActivity;
import com.example.feedler.Post;
import com.example.feedler.R;

import java.util.ArrayList;

public class PostViewHolder extends RecyclerView.ViewHolder {
    private TextView postText;
    private TextView mShare;
    private TextView dataText;
    private TextView groupName;
    public Button favorite;
    private ArrayList<String> arrayList = new ArrayList<>();

   public PostViewHolder(View view) {
        super(view);
        postText =  view.findViewById(R.id.postText);
        dataText = view.findViewById(R.id.postTime);
        groupName= view.findViewById(R.id.groupName);
        mShare = view.findViewById(R.id.share);
        favorite = view.findViewById(R.id.favoriteButton);
   }


    public void bind(Post post) {
        postText.setText(post.getPostText());
        dataText.setText(post.getDate());
        groupName.setText(post.getGroupName());
        if (post.favorite){
            favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade));
        } else {
            favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade_empty));
        }
        mShare.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, post.getPostText());
            Intent chosenIntent = Intent.createChooser(intent, post.getPostText());
            mShare.getContext().startActivity(chosenIntent);
        });


    }

}
