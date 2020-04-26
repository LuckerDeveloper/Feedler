package com.example.feedler.Favorites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedler.MainActivity;
import com.example.feedler.PagedList.PostAdapter;
import com.example.feedler.Post;
import com.example.feedler.PostRepository;
import com.example.feedler.PostViewModel;
import com.example.feedler.R;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements PostAdapter.Listener {

    RecyclerView recyclerView;
    FavoriteAdapter adapter;
    PostViewModel model;
    List<Post> posts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_list);

        recyclerView = findViewById(R.id.recyclerViewFavorite);

        model= new ViewModelProvider(this).get(PostViewModel.class);
        posts=model.getFavoritePost();
        adapter = new FavoriteAdapter(this, posts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



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
            case R.id.action_settings:
            {
                //меню настроек
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
}
