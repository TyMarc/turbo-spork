package com.mirego.rebelchat.controllers;

import android.content.Context;

import com.mirego.rebelchat.models.User;

import java.util.ArrayList;

public interface UsersController {

    interface UsersCallback {
        void onUsersSuccess(ArrayList<User> users);
        void onUsersFailed();
    }

    interface RegisterCallback {
        void onRegisterSuccess(String userId);
        void onRegisterFail();
    }

    void getUsers(Context context, String username, UsersCallback usersCallback);

    void register(Context context, String username, String email, RegisterCallback registerCallback);
}
