package com.mirego.rebelchat.models;

import android.graphics.Bitmap;

import com.mirego.rebelchat.utilities.AvatarGenerator;

/**
 * Created by marc-antoinehinse on 2016-03-12.
 */
public class User {
    public String username;
    public String userId;
    public Bitmap avatar;
    public String email;
    public boolean isChecked;

    public User(String username, String userId, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        avatar = AvatarGenerator.generate(150, 150);
        isChecked = false;
    }
}
