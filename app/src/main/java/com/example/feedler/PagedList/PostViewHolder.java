package com.example.feedler.PagedList;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.Post;
import com.example.feedler.R;

import java.util.ArrayList;

public class PostViewHolder extends RecyclerView.ViewHolder {
    TextView postText;
    TextView mShare;
    Button favorite;
    ArrayList<String> arrayList = new ArrayList<>();
    boolean addTo;

   public PostViewHolder(View view) {
        super(view);
        postText = (TextView) view.findViewById(R.id.postText);

        mShare = (TextView) view.findViewById(R.id.share);
        favorite = (Button) view.findViewById(R.id.favoriteButton);

        favorite.setOnClickListener(this::onClickToFavorite);
       favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade_empty));

        mShare.setOnClickListener(v -> {
            int pos = getAdapterPosition();
            String textPost = arrayList.get(pos);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, textPost);
            Intent chosenIntent = Intent.createChooser(intent, textPost);
            mShare.getContext().startActivity(chosenIntent);
        });
    }


    private void onClickToFavorite(View view) {
       if (addTo==false) {
           favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade));
           addTo = true;
       }
      else  {
          favorite.setBackground(favorite.getContext().getDrawable(R.drawable.grade_empty));
           addTo = false;
      }
    }


    public void bind(Post post) {
        postText.setText(post.getPostText());
        arrayList.add(post.getPostText());
    }
}
