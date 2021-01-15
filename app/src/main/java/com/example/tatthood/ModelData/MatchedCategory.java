package com.example.tatthood.ModelData;

public class MatchedCategory {
    private String postid;
    private String tattoourl;


    public MatchedCategory() {
    }

    public MatchedCategory(String postid, String tattoourl) {
        this.postid = postid;
        this.tattoourl = tattoourl;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTattoourl() {
        return tattoourl;
    }

    public void setTattoourl(String tattoourl) {
        this.tattoourl = tattoourl;
    }
}
