package com.example.feedler;

import android.app.Application;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostRepository {
    public interface Callback<R> {
        public void onResult(String nextFrom, R result, Throwable error);
    }
    private PostDao postDao;


    PostRepository() {
    }


    //Получение постов с сети
    public void getData(final String startFrom, final int count, final Callback<List<Post>> callback){
        final Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(VKApiConst.FILTERS, "post");
        paramsMap.put("count", count);

        if (startFrom != null) {
            paramsMap.put("start_from", startFrom);
        }

        final VKParameters params = new VKParameters(paramsMap);
        VKRequest request = new VKRequest("newsfeed.get", params); //Запрос с фильтром params
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    final List<Post> list = new ArrayList<>();
                    JSONObject jsonObject = (JSONObject) response.json.get("response");
                    JSONArray jsonArrayPost = (JSONArray) jsonObject.get("items");
                    JSONArray jsonArrayGroup = (JSONArray) jsonObject.get("groups");
                    JSONArray jsonArrayProfiles = (JSONArray) jsonObject.get("profiles");

                    for (int i = 0; i < jsonArrayPost.length(); i++) {
                        JSONObject jsonObjectPost = (JSONObject) jsonArrayPost.get(i);

                        String text = jsonObjectPost.optString("text");
                        int sourceId = jsonObjectPost.getInt("source_id");
                        String groupName = null;
                        long  dateInMillis=  jsonObjectPost.getLong("date");
                        String date = dateToString(dateInMillis);

                        //получение имени источника
                        if (sourceId>0){
                            for (int j = 0 ; j< jsonArrayProfiles.length(); j++){
                                JSONObject profile = (JSONObject) jsonArrayProfiles.get(j);
                                if (sourceId==profile.getInt("id")){
                                    String firstName = profile.getString("first_name");
                                    String lastName= profile.getString("last_name");
                                    groupName = firstName + " "+ lastName;
                                }
                            }
                        } else {
                            for (int j = 0; j< jsonArrayGroup.length(); j++ ){
                                JSONObject group = (JSONObject) jsonArrayGroup.get(j);
                                if (sourceId*(-1)==group.getInt("id")){
                                    groupName=group.getString("name");
                                }
                            }
                        }

                        Post post=new Post(groupName, date, text, i );
                        list.add(post);
                    }
                    final String nextFrom = jsonObject.optString("next_from");
                    callback.onResult(nextFrom, list, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onResult(startFrom, null, e);
                }
            }
        });

    }

    private String dateToString(long dateInMillis){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(1000*dateInMillis);
        String day = getRightStringNum(calendar.get(Calendar.DAY_OF_MONTH));
        String month = getRightStringNum (calendar.get(Calendar.MONTH)+1);     //не понятно, почему ставит не тот месяц
        String year = getRightStringNum( calendar.get(Calendar.YEAR));
        String hour = getRightStringNum( calendar.get(Calendar.HOUR_OF_DAY));
        String minute = getRightStringNum(calendar.get(Calendar.MINUTE));
        String dateString= day+"."+month+"."+ year+ " "+ hour+":"+minute;
        return dateString;
    }

    private String getRightStringNum(int num){
        if (num<10){
            return "0"+num;
        } else return ""+num;
    }

    public static class PostDiffUtilCallback extends DiffUtil.ItemCallback<Post> {

        public PostDiffUtilCallback() {
        }

        @Override
        public boolean areItemsTheSame(@NonNull Post oldPost, @NonNull Post newPost) {
            return oldPost.getId() == newPost.getId();
        }

        @Override
        public boolean areContentsTheSame(Post oldPost, Post newPost) {
            return oldPost.getPostText().equals(newPost.getPostText());
        }

    }
}

