package com.example.feedler;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.text.DateFormat;

@Entity(tableName = "post_table")
public class Post {

    @PrimaryKey
    public int id;


    @NonNull
    @ColumnInfo(name = "group_name")
    public String groupName;


    @NonNull
    @ColumnInfo(name = "date")
    public String date;

    @NonNull
    @ColumnInfo(name = "text")
    public String postText;


    public Post(@NonNull String groupName, @NonNull String date, String postText, int id ) {
        this.groupName = groupName;
        this.date=date;
        this.postText=postText;
        this.id = id;
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

    public int getId(){
        return this.id;
    }
}
