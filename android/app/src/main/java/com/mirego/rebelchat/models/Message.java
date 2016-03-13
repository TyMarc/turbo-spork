package com.mirego.rebelchat.models;

import android.graphics.Bitmap;

import com.mirego.rebelchat.utilities.Encoding;

/**
 * Created by marc-antoinehinse on 2016-03-12.
 */
public class Message {
    public String userId;
    public String text;
    public Bitmap image;

    public Message(final String userId, final String text, final String base64Image) {
        this.userId= userId;
        this.text = text;
        image = Encoding.decodeBase64(base64Image);
    }
}
