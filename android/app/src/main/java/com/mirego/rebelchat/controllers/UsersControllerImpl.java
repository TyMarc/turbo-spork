package com.mirego.rebelchat.controllers;

import android.content.Context;

import com.mirego.rebelchat.R;
import com.mirego.rebelchat.models.Message;
import com.mirego.rebelchat.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UsersControllerImpl implements UsersController {

    private final String USERS_PATH = "users";

    private final String PARAMETER_USER_ID = "_id";
    private final String PARAMETER_USERNAME = "name";
    private final String PARAMETER_EMAIL = "email";

    private final String PARAMETER_M_USER_ID = "userId";
    private final String PARAMETER_M_TEXT = "text";
    private final String PARAMETER_M_IMAGE = "image";

    private OkHttpClient client = new OkHttpClient();

    @Override
    public void getUsers(Context context, final String username, final UsersCallback usersCallback) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(context.getString(R.string.service_host))
                .port(context.getResources().getInteger(R.integer.service_port))
                .addPathSegment(USERS_PATH)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (usersCallback != null) {
                    ArrayList<User> users = getUserIdFromResponseArray(response);
                    if (users.size() > 0) {
                        usersCallback.onUsersSuccess(users);
                    } else {
                        usersCallback.onUsersFailed();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (usersCallback != null) {
                    usersCallback.onUsersFailed();
                }
            }
        });

    }

    @Override
    public void getMessagesFromUser(Context context, final String userId, final MessagesCallback messagesCallback) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(context.getString(R.string.service_host))
                .port(context.getResources().getInteger(R.integer.service_port))
                .addPathSegments("users/" + userId + "/messages")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (messagesCallback != null) {
                    ArrayList<Message> messages = getMessagesFromResponseArray(response);
                    if (messages.size() > 0) {
                        messagesCallback.onMessagesSuccess(messages);
                    } else {
                        messagesCallback.onMessagesFailed();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (messagesCallback != null) {
                    messagesCallback.onMessagesFailed();
                }
            }
        });

    }

    private ArrayList<Message> getMessagesFromResponseArray(Response response) {
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            JSONArray userList = new JSONArray(response.body().string());
            for(int i = 0; i < userList.length(); i++) {
                JSONObject message = (JSONObject) userList.get(i);
                messages.add(new Message(message.getString(PARAMETER_M_USER_ID), message.getString(PARAMETER_M_TEXT), message.getString(PARAMETER_M_IMAGE)));
            }
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
            return messages;
        }
    }

    private ArrayList<User> getUserIdFromResponseArray(Response response) {
        try {
            ArrayList<User> users = new ArrayList<User>();
            JSONArray userList = new JSONArray(response.body().string());
            for(int i = 0; i < userList.length(); i++) {
                JSONObject user = (JSONObject) userList.get(i);
                users.add(new User(user.getString(PARAMETER_USERNAME), user.getString(PARAMETER_USER_ID), user.getString(PARAMETER_EMAIL)));
            }
            return users;
        } catch (Exception e) {
            return null;
        }
    }
}
