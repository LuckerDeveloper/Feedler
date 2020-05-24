package com.example.feedler;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.DiffUtil;


import com.example.feedler.Images.Image;
import com.example.feedler.Images.ImageDao;
import com.example.feedler.Images.ImageRoomDatabase;
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
    private ImageRoomDatabase imageRoomDatabase;
    private List<Post> postList;

    PostRepository(android.app.Application application) {
        postRoomDatabase= PostRoomDatabase.getDatabase(application);
        imageRoomDatabase= ImageRoomDatabase.getDatabase(application);
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
                    postList = new ArrayList<>();
                    JSONObject jsonObject = (JSONObject) response.json.get("response");
                    JSONArray jsonArrayPost = (JSONArray) jsonObject.get("items");
                    JSONArray jsonArrayGroup = (JSONArray) jsonObject.get("groups");
                    JSONArray jsonArrayProfiles = (JSONArray) jsonObject.get("profiles");

                    for (int i = 0; i < jsonArrayPost.length(); i++) {
                        JSONObject jsonObjectPost = (JSONObject) jsonArrayPost.get(i);

                        List<Image> imageList = new ArrayList<>();
                        String link=null;

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

                                } else if(jsonAttachment.getString("type").equals("link")){
                                    JSONObject jsonLink= (JSONObject)  jsonAttachment.get("link");
                                    if(jsonLink.has("url")){
                                        link=jsonLink.getString("url");
                                    }
                                }
                            }
                        }

                        String text = jsonObjectPost.optString("text");
                        int sourceId = jsonObjectPost.getInt("source_id");
                        String groupName = null;
                        String groupImageURL=null;
                        long  dateInMillis=  jsonObjectPost.getLong("date");

                        //получение имени источника
                        if (sourceId>0){
                            for (int j = 0 ; j< jsonArrayProfiles.length(); j++){
                                JSONObject profile = (JSONObject) jsonArrayProfiles.get(j);
                                if (sourceId==profile.getInt("id")){
                                    String firstName = profile.getString("first_name");
                                    String lastName= profile.getString("last_name");
                                    groupName = firstName + " "+ lastName;
                                    groupImageURL=profile.getString("photo_100");
                                }
                            }
                        } else {
                            for (int j = 0; j< jsonArrayGroup.length(); j++ ){
                                JSONObject group = (JSONObject) jsonArrayGroup.get(j);
                                if (sourceId*(-1)==group.getInt("id")){
                                    groupName=group.getString("name");
                                    groupImageURL=group.getString("photo_100");
                                }
                            }
                        }
                        Post post=new Post(groupName, dateInMillis, text);
                        post.imageList=imageList;
                        post.link=link;
                        post.groupImageURL=groupImageURL;
                        postList.add(post);
                    }

                    //сохранение первой партии постов в базу данных
                    if (startFrom==null){
                        AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                List<Post> savedPosts=postRoomDatabase.postDao().getSaved();
                                for(Post post:savedPosts){
                                    imageRoomDatabase.imageDao().deleteImagesByPostId(post.id);
                                }
                                postRoomDatabase.postDao().deleteSaved();
                                for (int i=0; i<postList.size(); i++){
                                    long postId;
                                    Post post=postList.get(i);
                                    Post postFromDB=postRoomDatabase.postDao().getByParams(post.getGroupName(), post.getDate(), post.getPostText());
                                    if(postFromDB!=null&& postFromDB.favorite){
                                        post.favorite=true;
                                        postId=postFromDB.id;
                                    } else {
                                        postId=postRoomDatabase.postDao().insert(post);
                                    }
                                    for (Image image: post.imageList){
                                        image.postId=postId;
                                    }
                                    imageRoomDatabase.imageDao().insertImages(post.imageList);
                                }
                                final String nextFrom = jsonObject.optString("next_from");
                                callback.onResult(nextFrom, postList, null);
                            }
                        });
                    } else {
                        final String nextFrom = jsonObject.optString("next_from");
                        callback.onResult(nextFrom, postList, null);
                    }

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
                    AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            postList=postRoomDatabase.postDao().getSaved();
                            for (Post post:postList){
                                List<Image> imageList = getImageList(post.id);
                                post.imageList=imageList;
                            }
                            if (postList==null) {
                                callbackWithListPost.onFail();
                            } else {
                                callbackWithListPost.onSuccess(postList);
                            }
                        }
                    });
                }
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    void getFavoritePost(Context context){
        CallbackWithListPost favoriteCallBack= (CallbackWithListPost) context;
        AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                List<Post> posts= postDao.getFavorite();
                for (Post post: posts){
                    List<Image> imageList = imageRoomDatabase.imageDao().getByPostId(post.id);
                    post.imageList=imageList;
                    Log.e("repo getfavorite", ""+imageList.size() );
                }
                if (posts.size()>0){
                    favoriteCallBack.onSuccess(posts);
                } else{
                    favoriteCallBack.onFail();
                }
            }
        });
    }


    public void insertFavorite(Post post){
        Log.e("postRepo", "insert Favorite");
        AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                Post postFromDb=postDao.getByParams(post.getGroupName() , post.getDate(), post.getPostText());
                if (postFromDb!=null){
                    int postIdFromDB=postDao.deletePost(postFromDb);
                    imageRoomDatabase.imageDao().deleteImagesByPostId(postIdFromDB);
                }
                long postId=postDao.insert(post);
                for (Image image: post.imageList){
                    image.postId=postId;
                }
                imageRoomDatabase.imageDao().insertImages(post.imageList);
                Log.e("repo insertFavorite", ""+post.imageList.size() );
            }
        });
    }

    public void deleteFavorite(Post post){
        Log.e("postRepo", "delete Favorite");
        AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                Post postFromDb=postDao.getByParams(post.getGroupName() , post.getDate(), post.getPostText());
                if (postFromDb!=null){
                    int flag=postDao.deletePost(postFromDb);
                }
                post.favorite=false;
                postDao.insert(post);
            }
        });
    }

    public void deleteFavoriteAll(){
        AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                postDao.deleteFavorites();
            }
        });
    }

    private List<Image> getImageList(long postId){
        ImageDao imageDao = imageRoomDatabase.imageDao();
        List<Image> imageList = imageDao.getByPostId(postId);
        Log.e("postRepo", "getImageList size="+imageList.size());
        return imageDao.getByPostId(postId);
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

