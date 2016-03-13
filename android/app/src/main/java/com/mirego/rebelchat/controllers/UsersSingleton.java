package com.mirego.rebelchat.controllers;

import android.content.Context;

import com.mirego.rebelchat.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by marc-antoinehinse on 2016-03-12.
 */
public class UsersSingleton implements UsersController.UsersCallback {
    private static UsersSingleton instance;
    private static ArrayList<User> users;
    private static UsersController usersController;

    private UsersSingleton(){
        users = new ArrayList<User>();
        usersController = new UsersControllerImpl();
    };

    public static UsersSingleton getInstance() {
        if(instance == null) {
            instance = new UsersSingleton();
        }

        return instance;
    }

    public void init(final Context context) {
        usersController.getUsers(context, null, this);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public void onUsersSuccess(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public void onUsersFailed() {

    }

    public String getUsernameWithId(String userId) {
        final String username = "Unknown";

        for(User user : users) {
            if(user.userId != null && user.userId.equals(userId)) {
                return user.username;
            }
        }
        return username;
    }
}
