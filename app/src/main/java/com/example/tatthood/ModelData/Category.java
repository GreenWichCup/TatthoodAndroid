package com.example.tatthood.ModelData;

public class Category {
    private String categoryName ;
    private String letter;
    private String imageurl;
    private String postid;
    private String tattoourl;

    public Category() {
    }

    public Category(String categoryName, String letter, String imageurl) {
        this.categoryName = categoryName;
        this.letter = letter;
        this.imageurl = imageurl;
        this.postid = postid;
        this.tattoourl = tattoourl;

    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
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
