package com.mirego.rebelchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirego.rebelchat.R;
import com.mirego.rebelchat.controllers.UsersSingleton;
import com.mirego.rebelchat.models.Message;
import com.mirego.rebelchat.models.User;

import java.util.ArrayList;

/**
 * Created by marc-antoinehinse on 2015-10-10.
 */

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context mContext;
    private LayoutInflater mInflater = null;

    private ArrayList<Message> roommates;

    public MessageAdapter(Context context, ArrayList<Message> roommates) {
        super(context, R.layout.message_item, roommates);
        mContext = context;
        this.roommates = roommates;
    }

    static class ViewHolder {
        public TextView user;
        public ImageView image;
        public TextView message;
    }

    private LayoutInflater getInflater(){
        if(mInflater == null)
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return mInflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        if(convertView == null){ // Only inflating if necessary is great for performance
            rowView = getInflater().inflate(R.layout.message_item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.user = (TextView) rowView.findViewById(R.id.user);
            holder.image = (ImageView) rowView.findViewById(R.id.image);
            holder.message = (TextView) rowView.findViewById(R.id.message);
            rowView.setTag(holder);
        } else{
            rowView = convertView;
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        Message r = getItem(position);

        holder.user.setText(UsersSingleton.getInstance().getUsernameWithId(r.userId));
        holder.message.setText(r.text);


        if(r.image != null) {
            holder.image.setImageBitmap(r.image);
        }

        return rowView;
    }
}
