package com.example.tatthood.ModelData;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {

    public String id;
    public String username;
    public String email;
    public String imageUrl;
    public String hood;
    public String status;
    public String address;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String address, String id, String email, String username, String status, String hood, String imageUrl) {
        this.username = username;
        this.email = email;
        this.id = id ;
        this.hood = hood;
        this.status = status;
        this.imageUrl = imageUrl;
        this.address = address;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getimageUrl() { return imageUrl;}

    public void setimageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHood() {
        return hood;
    }

    public void setHood(String hood) {
        this.hood = hood;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }
}
