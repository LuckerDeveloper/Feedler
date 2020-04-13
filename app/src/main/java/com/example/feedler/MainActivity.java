package com.example.feedler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Post> arrayList = new ArrayList<>();
    private PostViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
        }

        model= ViewModelProviders.of(this).get(PostViewModel.class);


        model.getAllPosts().observe(this, new Observer<ArrayList<Post>>() {
            @Override
            public void onChanged(ArrayList<Post> posts) {
                arrayList.addAll(posts);
            }
        });
    }
}
