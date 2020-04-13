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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class PostRepository {

    private PostDao postDao;
    private MutableLiveData<ArrayList<Post>> liveDataArrayList;


    PostRepository(Application application) {
    }

    public LiveData<ArrayList<Post>> getAllPosts(){
        return getLiveDataArrayListFromInternet();
    }

    //Получение постов с сети
    private MutableLiveData<ArrayList<Post>> getLiveDataArrayListFromInternet(){
        final MutableLiveData<ArrayList<Post>> liveDataArrayList = new MutableLiveData<>();;
        final ArrayList<Post> arrayList= new ArrayList<>();

        VKRequest request = new VKRequest("newsfeed.get", VKParameters.from(VKApiConst.FILTERS, "post")); //Запрос с фильтром pos
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject jsonObject = (JSONObject) response.json.get("response");
                    JSONArray jsonArrayPost = (JSONArray) jsonObject.get("items");
                    JSONArray jsonArrayGroup = (JSONArray) jsonObject.get("groups");
                    JSONArray jsonArrayProfiles = (JSONArray) jsonObject.get("profiles");

                    for (int i = 0; i < jsonArrayPost.length(); i++) {
                        JSONObject jsonObjectPost = (JSONObject) jsonArrayPost.get(i);

                        String text = jsonObjectPost.getString("text");
                        int sourceId = jsonObjectPost.getInt("source_id");
                        String groupName = null;
                        long  dateInMillis=  jsonObjectPost.getLong("date");
                        String date = dateToString(dateInMillis);

                        //получение имени источника
                        if (sourceId>0){
                            for (int j=0 ; j< jsonArrayProfiles.length(); j++){
                                JSONObject profile = (JSONObject) jsonArrayProfiles.get(j);
                                if (sourceId==profile.getInt("id")){
                                    String firstName = profile.getString("first_name");
                                    String lastName= profile.getString("last_name");
                                    groupName = firstName + " "+ lastName;
                                }
                            }
                        } else {
                            for (int j =0; j< jsonArrayGroup.length(); j++ ){
                                JSONObject group = (JSONObject) jsonArrayGroup.get(j);
                                if (sourceId*(-1)==group.getInt("id")){
                                    groupName=group.getString("name");
                                }
                            }
                        }

                        Post post=new Post(groupName, date, text );
                        arrayList.add(post);
                    }
                    liveDataArrayList.postValue(arrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return liveDataArrayList;
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
}
