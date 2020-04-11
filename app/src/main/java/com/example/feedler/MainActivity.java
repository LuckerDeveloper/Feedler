package com.example.feedler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.feedler.PagedList.MyPositionalDataSource;
import com.example.feedler.PagedList.MySourceFactory;
import com.example.feedler.PagedList.PostAdapter;

import com.example.feedler.PagedList.PostStorage;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.util.VKUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PostAdapter adapter = new PostAdapter(new PostStorage.PostDiffUtilCallback());
    PostStorage.PostDiffUtilCallback diffUtilCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
        }

        recyclerView = findViewById(R.id.recyclerView);

        MySourceFactory sourceFactory = new MySourceFactory(new PostStorage());


// PagedList
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build();

        LiveData<PagedList<Post>> pagedListLiveData = new LivePagedListBuilder<>(sourceFactory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();


// Adapter

        adapter = new PostAdapter(diffUtilCallback);

        pagedListLiveData.observe(this, new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(@Nullable PagedList<Post> posts) {
                adapter.submitList(posts);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);




// RecyclerView
    }
        //работа с arrayList


    //arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        // listView.setAdapter(arrayAdapter);
    }
// DataSource




//       Это будет в другом файле
//       VKRequest request = new VKRequest("newsfeed.get", VKParameters.from(VKApiConst.FILTERS, "post")); //Запрос с фильтром post
//       request.executeWithListener(new VKRequest.VKRequestListener() {
    //               @Override
    //          public void onComplete(VKResponse response) {
    //               super.onComplete(response);

    //               try {
    //                   JSONObject jsonObject = (JSONObject) response.json.get("response"); //получаем JSON объект по запросу
    //                   JSONArray jsonArray = (JSONArray) jsonObject.get("items"); //Получаем конкретно посты
    //                   for (int i = 0; i < jsonArray.length(); i++) {
    //                      JSONObject post = (JSONObject) jsonArray.get(i);
    //                        arrayList.add(post.getString("text")); //Производим перебор и получаем то, что является телом поста
    //                   }
    //                 arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
    //                 listView.setAdapter(arrayAdapter); //пока обычный listView
    //               } catch (JSONException e) {
    //                  e.printStackTrace();
    //              }


    //          }
    //      });





