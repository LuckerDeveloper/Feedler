package com.example.feedler;

import android.app.Application;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    private PostDao postDao;
    private MutableLiveData<ArrayList<String>> liveDataArrayList;
    private ArrayList<String> arrayList= new ArrayList<>();

    PostRepository(Application application) {
    }

    public LiveData<ArrayList<String>> getAllPosts(){


        liveDataArrayList = new MutableLiveData<>();
        //нужно сделать новый метод загрузки

        VKRequest request = new VKRequest("newsfeed.get", VKParameters.from(VKApiConst.FILTERS, "post")); //Запрос с фильтром pos

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject jsonObject = (JSONObject) response.json.get("response");
                    JSONArray jsonArrayPost = (JSONArray) jsonObject.get("items");


                    for (int i = 0; i < jsonArrayPost.length(); i++) {
                        JSONObject post = (JSONObject) jsonArrayPost.get(i);
                        arrayList.add(post.getString("text"));
                        Log.e("post", "name of group");
                        int sourceId = post.getInt("source_id");

                        String text = post.getString("text");

                        Log.e("post", ""+i+sourceId+" "+ text);
                    }
                    liveDataArrayList.postValue(arrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveDataArrayList;
    }


}
