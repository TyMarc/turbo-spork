package com.mirego.rebelchat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mirego.rebelchat.R;
import com.mirego.rebelchat.adapters.MessageAdapter;
import com.mirego.rebelchat.controllers.UsersController;
import com.mirego.rebelchat.controllers.UsersControllerImpl;
import com.mirego.rebelchat.models.Message;
import com.wenchao.cardstack.CardStack;

import java.util.ArrayList;

public class SeeMessagesActivity extends AppCompatActivity implements UsersController.MessagesCallback, CardStack.CardEventListener {
    private CardStack mCardStack;
    private MessageAdapter adapter;
    private String userId;
    private UsersController usersController;


    public static void show(final Context context, final String userId) {
        Intent i = new Intent(context, SeeMessagesActivity.class);
        i.putExtra("userId", userId);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_see_messages);

        mCardStack = (CardStack)findViewById(R.id.container);
        mCardStack.setStackMargin(20);
        mCardStack.setListener(this);

        userId = getIntent().getStringExtra("userId");

        usersController = new UsersControllerImpl();
        usersController.getMessagesFromUser(this, userId, this);
    }

    @Override
    public void onMessagesSuccess(final ArrayList<Message> messages) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new MessageAdapter(getApplicationContext(), messages);
                mCardStack.setAdapter(adapter);
            }
        });

    }

    @Override
    public void onMessagesFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SeeMessagesActivity.this, "Not able to fetch messages", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean swipeEnd(int i, float v) {
        return true;
    }

    @Override
    public boolean swipeStart(int i, float v) {
        return true;
    }

    @Override
    public boolean swipeContinue(int i, float v, float v1) {
        return true;
    }

    @Override
    public void discarded(int i, int i1) {
        Log.i("SeeMessagesActivity", "id=" +i + " count=" + adapter.getCount());
        if(i == adapter.getCount()) {
            finish();
        }

    }

    @Override
    public void topCardTapped() {

    }
}
