package com.example.feedler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

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
    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (!VKSdk.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivity(intent);
        }

        listView = findViewById(R.id.listView);

        //работа с arrayList

        arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);


//       Это будет в другом файле
//
//        VKRequest request = new VKRequest("newsfeed.get", VKParameters.from(VKApiConst.FILTERS, "post")); //Запрос с фильтром post
//        request.executeWithListener(new VKRequest.VKRequestListener() {
//                @Override
//            public void onComplete(VKResponse response) {
//                super.onComplete(response);
//
//                try {
//                    JSONObject jsonObject = (JSONObject) response.json.get("response"); //получаем JSON объект по запросу
//                    JSONArray jsonArray = (JSONArray) jsonObject.get("items"); //Получаем конкретно посты
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject post = (JSONObject) jsonArray.get(i);
//                        arrayList.add(post.getString("text")); //Производим перебор и получаем то, что является телом поста
//                    }
//                    arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
//                    listView.setAdapter(arrayAdapter); //пока обычный listView
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        });


    }
}
