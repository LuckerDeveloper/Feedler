package com.example.feedler;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.DiffUtil;


import com.example.feedler.Images.Image;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PostRepository  {

    public interface CallbackWithListPost<T> {

        void onSuccess(T result);

        void onFail();
    }

    public interface Callback<R> {
        void onResult(String nextFrom, R result, Throwable error);
    }

    private PostRoomDatabase postRoomDatabase;
    private List<Post> savedList;

    PostRepository(android.app.Application application) {
        postRoomDatabase= PostRoomDatabase.getDatabase(application);
    }

    //Получение постов с сети
    public void getData(final String startFrom, final int count, final Callback<List<Post>> callback, Context context){
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

                        List<Image> imageList = new ArrayList<>();

                        if (jsonObjectPost.has("attachments")){
                            JSONArray jsonAttachments =(JSONArray) jsonObjectPost.get("attachments");
                            for( int j=0 ; j<jsonAttachments.length(); j++){
                                JSONObject jsonAttachment = (JSONObject) jsonAttachments.get(j);
                                if (jsonAttachment.getString("type").equals("photo")){
                                    JSONObject jsonPhoto= (JSONObject)  jsonAttachment.get("photo");
                                    String smallSizeURL=null;
                                    int width=jsonPhoto.getInt("width");
                                    int height=jsonPhoto.getInt("height");
                                    if(jsonPhoto.has("photo_604")){
                                        if((width>604 || height>604)){
                                            if (width>height){
                                                height=height*604/width;
                                                width=604;
                                            } else {
                                                width=width*604/height;
                                                height=604;
                                            }
                                        }
                                        smallSizeURL=jsonPhoto.getString("photo_604");
                                    } else if(jsonPhoto.has("photo_130")) {
                                        smallSizeURL=jsonPhoto.getString("photo_130");
                                    } else if(jsonPhoto.has("photo_75")){
                                        smallSizeURL=jsonPhoto.getString("photo_75");
                                    }
                                    Image image = new Image(smallSizeURL,width,height);
                                    imageList.add(image);


//
//
//                                    String smallPhoto = null;
//                                    if(jsonPhoto.has("photo_807")) {
//                                        smallPhoto=jsonPhoto.getString("photo_807");
//                                    } else if(jsonPhoto.has("photo_604")){
//                                        smallPhoto=jsonPhoto.getString("photo_604");
//                                    } else if(jsonPhoto.has("photo_1280")){
//                                        smallPhoto=jsonPhoto.getString("photo_1280");
//                                    }else if(jsonPhoto.has("photo_130")){
//                                        smallPhoto=jsonPhoto.getString("photo_130");
//                                    }else if(jsonPhoto.has("photo_2560")){
//                                        smallPhoto=jsonPhoto.getString("photo_2560");
//                                    }else if(jsonPhoto.has("photo_75")) {
//                                        smallPhoto = jsonPhoto.getString("photo_75");
//                                    }
//                                    if(smallPhoto!=null){
//                                        photoList.add(smallPhoto);
//                                    }

                                }
                            }
                        }

                        String text = jsonObjectPost.optString("text");
                        int sourceId = jsonObjectPost.getInt("source_id");
                        String groupName = null;
                        long  dateInMillis=  jsonObjectPost.getLong("date");

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
                        Post post=new Post(groupName, dateInMillis, text);
                        post.imageList=imageList;


                        list.add(post);
                    }

                    //сохранение первой партии постов в базу данных
                    if (startFrom==null){
                        AppExecutors.getInstance().postDatabaseExrcutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<Post> postList=new ArrayList<>();
                                for (int i=0; i<list.size(); i++){
                                    Post post=list.get(i);
                                    Post postFromDB=postRoomDatabase.postDao().getByParams(post.getGroupName(), post.getDate(), post.getPostText());
                                    if(postFromDB!=null&& postFromDB.favorite){
                                        post.favorite=true;
                                        list.set(i , post);
                                    } else {
                                        postList.add(post);
                                    }
                                }
                                postRoomDatabase.postDao().deleteSaved();
                                postRoomDatabase.postDao().insertAll(postList);
                            }
                        });
                    }
                    final String nextFrom = jsonObject.optString("next_from");
                    callback.onResult(nextFrom, list, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onResult(startFrom, null , e);
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                if (startFrom==null){
                    CallbackWithListPost callbackWithListPost = (CallbackWithListPost) context;
                    AppExecutors.getInstance().postDatabaseExrcutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            savedList=postRoomDatabase.postDao().getSaved();
                            if (savedList==null) {
                                callbackWithListPost.onFail();
                            } else {
                                callbackWithListPost.onSuccess(savedList);
                            }
                        }
                    });
                }
            }
        });
    }

    void getFavoritePost(Context context){
        CallbackWithListPost favoriteCallBack= (CallbackWithListPost) context;
        AppExecutors.getInstance().favoriteDatabaseExrcutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                List<Post> posts= postDao.getFavorite();
                if (posts.size()>0){
                    favoriteCallBack.onSuccess(posts);
                } else{
                    favoriteCallBack.onFail();
                }
            }
        });
    }

    void getSearchFavoritePost(Context context, String search){
        CallbackWithListPost favoriteSearchCallBack= (CallbackWithListPost) context;
        AppExecutors.getInstance().favoriteDatabaseExrcutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                List<Post> posts= postDao.getFavoriteSearching("%"+search+"%");
                if (posts.size()>0){

                    favoriteSearchCallBack.onSuccess(posts);
                } else{
                    favoriteSearchCallBack.onFail();
                }
            }
        });
    }


    public void insertFavorite(Post post){
        AppExecutors.getInstance().favoriteDatabaseExrcutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                Post postFromDb=postDao.getByParams(post.getGroupName() , post.getDate(), post.getPostText());
                if (postFromDb!=null){
                    Log.e("insertFavorite" ,""+postFromDb.getGroupName());
                    Log.e("insertFavorite" ,""+postFromDb.getDate());
                    Log.e("insertFavorite" ,""+postFromDb.getPostText());
                    int flag=postDao.deletePost(postFromDb);
                    Log.e("insertFavorite" ,""+flag);
                }
                postDao.insertFavorite(post);
                Log.e("insertFavorite" ,"size="+postDao.getAll().size());
            }
        });
    }

    public void deleteFavorite(Post post){
        AppExecutors.getInstance().favoriteDatabaseExrcutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                Post postFromDb=postDao.getByParams(post.getGroupName() , post.getDate(), post.getPostText());
                if (postFromDb!=null){
                    int flag=postDao.deletePost(postFromDb);
                    Log.e("deleteFavorite" ,"deleted="+flag);
                }
                post.favorite=false;
                postDao.insertFavorite(post);
                Log.e("deleteFavorite" ,"size="+postDao.getAll().size());
            }
        });
    }

    public void deleteFavoriteAll(){
        AppExecutors.getInstance().favoriteDatabaseExrcutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                postDao.deleteFavorites();
            }
        });
    }


    public static class PostDiffUtilCallback extends DiffUtil.ItemCallback<Post> {

        public PostDiffUtilCallback() {
        }

        @Override
        public boolean areItemsTheSame(@NonNull Post oldPost, @NonNull Post newPost) {
            return oldPost.id == newPost.id;
        }

        @Override
        public boolean areContentsTheSame(Post oldPost, Post newPost) {
            return oldPost.isEquals(newPost) ;
        }

    }


}

