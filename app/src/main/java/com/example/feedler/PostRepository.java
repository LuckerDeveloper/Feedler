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
                        String text = jsonObjectPost.optString("text");
                        int sourceId = jsonObjectPost.getInt("source_id");
                        String groupName = null;
                        String groupImageURL=null;
                        long  dateInMillis=  jsonObjectPost.getLong("date");
                        String attachedLink=null;
                        String linkOfPost="https://vk.com/wall"+jsonObjectPost.getString("source_id")+
                                "_"+jsonObjectPost.getString("post_id");

                        if (jsonObjectPost.has("attachments")){
                            JSONArray jsonAttachments =(JSONArray) jsonObjectPost.get("attachments");
                            for( int j=0 ; j<jsonAttachments.length(); j++){
                                JSONObject jsonAttachment = (JSONObject) jsonAttachments.get(j);
                                if (jsonAttachment.getString("type").equals("photo")){
                                    JSONObject jsonPhoto= (JSONObject)  jsonAttachment.get("photo");
                                    imageList.add(getImageFromJson(jsonPhoto));
                                } else if(jsonAttachment.getString("type").equals("link")){
                                    JSONObject jsonLink= (JSONObject)  jsonAttachment.get("link");
                                    if(jsonLink.has("url")){
                                        attachedLink=jsonLink.getString("url");
                                    }
                                }
                            }
                        }

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
                        Post post=new Post(groupName, dateInMillis, text, linkOfPost);
                        post.imageList=imageList;
                        post.attachedLink=attachedLink;
                        post.groupImageURL=groupImageURL;
                        postList.add(post);
                    }
                    AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (startFrom==null) {
                                //сохранение первой партии постов в базу данных
                                List<Post> checkedOnFavoritePosts = savePost(postList);
                                final String nextFrom = jsonObject.optString("next_from");
                                callback.onResult(nextFrom, checkedOnFavoritePosts, null);
                            } else {
                                List<Post> checkedOnFavoritePosts = checkOnFavorite(postList);
                                final String nextFrom = jsonObject.optString("next_from");
                                callback.onResult(nextFrom, checkedOnFavoritePosts, null);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onResult(startFrom, null , e);
                }
            }
            @Override
            public void onError(VKError error) {
                super.onError(error);
                if (startFrom==null){
                    getSavedPostFromDatabase(context);
                }
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
                }
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
        AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                List<Post> posts= postDao.getFavoriteSearching("%"+search+"%");
                favoriteSearchCallBack.onSuccess(posts);
            }
        });
    }


    public void insertFavorite(Post post){
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
                Log.e("repo insertFavorite", ""+ post.imageList.size() );
            }
        });
    }

    public void deleteFavorite(Post post){
        AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                Post postFromDb=postDao.getByParams(post.getGroupName() , post.getDate(), post.getPostText());
                if (postFromDb!=null){
                    int postIdFromDB=postDao.deletePost(postFromDb);
                    post.favorite=false;
                    long newPostId = postDao.insert(post);
                    imageRoomDatabase.imageDao().deleteImagesByPostId(postIdFromDB);
                    List<Image> imageList=post.imageList;
                    if(imageList!=null){
                        for(Image image:imageList){
                            image.postId=newPostId;
                        }
                        imageRoomDatabase.imageDao().insertImages(imageList);
                    }
                }
            }
        });
    }

    public void deleteFavoriteAll(){
        AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
            @Override
            public void run() {
                PostDao postDao = postRoomDatabase.postDao();
                ImageDao imageDao = imageRoomDatabase.imageDao();
                List<Post> favoritePostList =postDao.getFavorite();
                for(Post post:favoritePostList){
                    long deletedPostId=postDao.deletePost(post);
//                    imageDao.deleteImagesByPostId(deletedPostId);

                    post.favorite=false;
                    long postId=postDao.insert(post);
                    List<Image> imageList=post.imageList;
                    if(imageList!=null){
                        for(Image image:imageList){
                            image.postId=postId;
                        }
                        imageDao.insertImages(imageList);
                    }

                }
            }
        });
    }

    private List<Image> getImageList(long postId){
        ImageDao imageDao = imageRoomDatabase.imageDao();
        List<Image> imageList = imageDao.getByPostId(postId);
        return imageList;
    }

    private Image getImageFromJson(JSONObject jsonPhoto) throws JSONException {
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
        return new Image(smallSizeURL,width,height);
    }

    private  List<Post> savePost (List<Post> postList){
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
        return postList;
    }

    private List<Post> checkOnFavorite(List<Post> postList){
        for (Post post : postList) {
            Post postFromDB = postRoomDatabase.postDao().getByParams(post.getGroupName(), post.getDate(), post.getPostText());
            if (postFromDB != null && postFromDB.favorite) {
                post.favorite = true;
            }
        }
        return postList;
    }

    public void getSavedPostFromDatabase(Context context){
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

    public void cleanDB(){
        AppExecutors.getInstance().postDatabaseExecutor().execute(new Runnable() {
            @Override
            public void run() {
                postRoomDatabase.postDao().deleteAll();
                imageRoomDatabase.imageDao().deleteAll();
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

