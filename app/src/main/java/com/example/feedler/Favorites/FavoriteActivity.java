package com.example.feedler.Favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.MainActivity;
import com.example.feedler.PagedList.PostAdapter;
import com.example.feedler.Post;
import com.example.feedler.PostViewModel;
import com.example.feedler.R;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements PostAdapter.Listener {

    RecyclerView recyclerView;
    FavoriteAdapter adapter;
    PostViewModel model;
    List<Post> posts;
    Button mainBut;
    Toolbar toolbarF;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_list);

        toolbarF = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbarF);

        recyclerView = findViewById(R.id.recyclerViewFavorite);

        model = new ViewModelProvider(this).get(PostViewModel.class);
        posts = model.getFavoritePost();
        adapter = new FavoriteAdapter(this, posts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mainBut = findViewById(R.id.mainFeedFAV);
        mainBut.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
            {
                model.deleteFavoriteAll();
                posts.clear();
                Intent intent=new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void insertFavorite(Post post) {
        List<Post> postList=model.getFavoritePost();
        if (postList.size()!=0) {
            post.id=postList.get(postList.size()-1).id+1;
        } else {
            post.id=0;
        }
        model.insertFavorite(post);
    }

    @Override
    public void deleteFavorite(Post post) {
        List<Post> favoritePost = model.getFavoritePost();
        if(favoritePost!=null){
            for (int i =0 ; i<favoritePost.size(); i++){
                if ( post.isEquals(favoritePost.get(i)) ){
                    model.deleteFavorite(favoritePost.get(i));
                    break;
                }
            }
        }

    }

    @Override
    public void replaceFavoriteVar(Post post) {
        model.replaceFavoriteVar(post);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
