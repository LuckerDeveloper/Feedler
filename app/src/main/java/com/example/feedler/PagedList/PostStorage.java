package com.example.feedler.PagedList;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;

import com.example.feedler.Post;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostStorage {
    private List<Post> list = new ArrayList<>();
    public PostStorage() {
    }

    public List<Post> getData(final int requestedStartPosition, final int requestedLoadSize){

        final VKRequest request = new VKRequest("newsfeed.get", VKParameters.from(VKApiConst.FILTERS, "post")); //Запрос с фильтром post
          request.executeWithListener(new VKRequest.VKRequestListener() {
                       @Override
                  public void onComplete(VKResponse response) {
                      super.onComplete(response);

                       try {
                           JSONObject jsonObject = (JSONObject) response.json.get("response"); //получаем JSON объект по запросу
                           JSONArray jsonArray = (JSONArray) jsonObject.get("items"); //Получаем конкретно посты
                           for (int i = requestedStartPosition; i < requestedLoadSize; i++) {
                              JSONObject post = (JSONObject) jsonArray.get(i);
                               list.add( new Post("" ,null,post.getString("text"),i));
                               System.out.println(post.getString("text"));//Производим перебор и получаем то, что является телом поста
                           }
                       } catch (JSONException e) {
                          e.printStackTrace();
                      }


                  }
              });
          return list;
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

