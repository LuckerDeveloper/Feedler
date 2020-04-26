package com.example.feedler;

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


//    public Post(@NonNull String groupName, @NonNull String date, String postText) {
//        this.groupName = groupName;
//        this.date=date;
//        this.postText=postText;
//    }

//    @Ignore
    public Post(@NonNull String groupName, @NonNull String date, String postText, int id) {
        this.groupName = groupName;
        this.date=date;
        this.postText=postText;
        this.id=id;
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
