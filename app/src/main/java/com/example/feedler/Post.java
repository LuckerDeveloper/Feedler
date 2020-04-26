package com.example.feedler;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.text.DateFormat;

@Entity(tableName = "post_table")
public class Post {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "group_name")
    private String groupName;

    @ColumnInfo(name = "data")
    private String date;

    @ColumnInfo(name = "text")
    private String postText;

    public boolean favorite;

    public Post(@NonNull String groupName, @NonNull String date, String postText, int id) {
        this.groupName = groupName;
        this.date=date;
        this.postText=postText;
        this.id=id;
        this.favorite=false;
    }

    public String getGroupName(){
        return this.groupName;
    }

    public String getDate(){
        return this.date;
    }

    public String getPostText(){
        return this.postText;
    }

    public boolean isEquals(Post post){
        if (post.getGroupName().equals(this.getGroupName())){
            if (post.getDate().equals(post.getDate())){
                if(post.getPostText().equals(post.getPostText())){
                    return true;
                }
            }
        }
        return false;
    }

}
