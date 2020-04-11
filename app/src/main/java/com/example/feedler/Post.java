package com.example.feedler;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.text.DateFormat;

@Entity(tableName = "post_table")
public class Post {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "group_name")
    private String groupName;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "data")
    private DateFormat date;

    @PrimaryKey
    @ColumnInfo(name = "text")
    private String postText;


    public Post(@NonNull String groupName, @NonNull DateFormat date, String postText, int id ) {
        this.groupName = groupName;
        this.date = date;
        this.postText = postText;
        this.id = id;
    }

    public String getGroupName(){
        return this.groupName;
    }

    public DateFormat getDate(){
        return this.date;
    }

    public String getPostText(){
        return this.postText;
    }

    public int getId(){
        return this.id;
    }
}
