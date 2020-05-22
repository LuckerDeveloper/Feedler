package com.example.feedler.Images;

public class Image {

    int id;

    public String smallSizeURL;

    public int postId;

    public int width;

    public int height;

    public Image(String smallSizeURL, int width, int height){
        this.smallSizeURL=smallSizeURL;
        this.width=width;
        this.height=height;
    }

}
