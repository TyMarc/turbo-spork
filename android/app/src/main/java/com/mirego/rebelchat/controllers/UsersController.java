package com.mirego.rebelchat.controllers;

import android.content.Context;

import com.mirego.rebelchat.models.Message;
import com.mirego.rebelchat.models.User;

import java.util.ArrayList;

public interface UsersController {

    interface UsersCallback {
        void onUsersSuccess(ArrayList<User> users);
        void onUsersFailed();
    }

    interface MessagesCallback {
        void onMessagesSuccess(ArrayList<Message> userId);
        void onMessagesFailed();
    }

    void getUsers(Context context, String username, UsersCallback usersCallback);

    void getMessagesFromUser(Context context, String userId, MessagesCallback registerCallback);
}
