package com.example.tatthood.ModelData;

public class MatchedCategory {
    private String postid;
    private String imageurl;


    public MatchedCategory() {
    }

    public MatchedCategory(String postid, String imageurl) {
        this.postid = postid;
        this.imageurl = imageurl;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
