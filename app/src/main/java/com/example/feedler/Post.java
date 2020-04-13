package com.example.feedler;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.text.DateFormat;

@Entity(tableName = "post_table")
public class Post {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "group_name")
    private String groupName;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "data")
    private String date;

    @PrimaryKey
    @ColumnInfo(name = "text")
    private String postText;


    public Post(@NonNull String groupName, @NonNull String date, String postText ) {
        this.groupName = groupName;
        this.date=date;
        this.postText=postText;
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
}
