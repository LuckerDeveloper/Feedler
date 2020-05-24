package com.example.feedler.Images;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_table")
public class Image {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String smallSizeURL;

    public long postId;

    public int width;

    public int height;

    public Image(String smallSizeURL, int width, int height){
        this.smallSizeURL=smallSizeURL;
        this.width=width;
        this.height=height;
    }

}
