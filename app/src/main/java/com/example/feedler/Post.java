package com.example.feedler;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.example.feedler.Images.Image;
import com.vk.sdk.api.model.VKApiCommunityFull;

import java.text.DateFormat;
import java.util.List;

@Entity(tableName = "post_table")
public class Post {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "group_name")
    private String groupName;

    @ColumnInfo(name = "data")
    private long date;

    @ColumnInfo(name = "text")
    private String postText;

    public boolean favorite;

    @Ignore
    public List<Image> imageList;

    public String attachedLink;

    public String groupImageURL;

    public String linkOfPost;

    public Post(@NonNull String groupName, long date, String postText, String linkOfPost) {
        this.groupName = groupName;
        this.date=date;
        this.postText=postText;
        this.favorite=false;
        this.linkOfPost=linkOfPost;
    }

    public String getGroupName(){
        return this.groupName;
    }

    public long getDate(){
        return this.date;
    }

    public String getPostText(){
        return this.postText;
    }

    boolean isEquals(Post post){
        if (post.getGroupName().equals(this.getGroupName())){
            if (post.getDate()==this.getDate()){
                return post.getPostText().equals(this.getPostText());
            }
        }
        return false;
    }

}
