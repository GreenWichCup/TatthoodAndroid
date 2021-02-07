package com.example.tatthood.ModelData;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
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

    protected Category(Parcel in) {
        categoryName = in.readString();
        letter = in.readString();
        imageurl = in.readString();
        postid = in.readString();
        tattoourl = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoryName);
        dest.writeString(letter);
        dest.writeString(imageurl);
        dest.writeString(postid);
        dest.writeString(tattoourl);
    }
}
