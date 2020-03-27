package com.example.feedler;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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
    private DateFormat date;

    @PrimaryKey
    @ColumnInfo(name = "text")
    private String postText;


    public Post(@NonNull String groupName, @NonNull DateFormat date, String postText ) {
        this.groupName = groupName;
        this.date=date;
        this.postText=postText;
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
}
