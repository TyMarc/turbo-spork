package com.mirego.rebelchat.controllers;

import android.content.Context;

import com.mirego.rebelchat.R;
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
    public void register(Context context, String username, String email, final RegisterCallback registerCallback) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(context.getString(R.string.service_host))
                .port(context.getResources().getInteger(R.integer.service_port))
                .addPathSegment(USERS_PATH)
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(PARAMETER_USERNAME, username);
            jsonObject.put(PARAMETER_EMAIL, email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), jsonObject.toString()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (registerCallback != null) {
                    String userId = getUserIdFromResponseObject(response);
                    if (userId != null) {
                        registerCallback.onRegisterSuccess(userId);
                    } else {
                        registerCallback.onRegisterFail();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if(registerCallback != null){
                    registerCallback.onRegisterFail();
                }
            }
        });
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

    private String getUserIdFromResponseObject(Response response) {
        try {
            JSONObject user = new JSONObject(response.body().string());
            return user.getString(PARAMETER_USER_ID);
        } catch (Exception e) {
            return null;
        }
    }
}
