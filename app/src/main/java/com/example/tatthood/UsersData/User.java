package com.example.tatthood.UsersData;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String id;
    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User( String userId, String email, String username) {
        this.username = username;
        this.email = email;
        this.id = userId ;

    }

}
