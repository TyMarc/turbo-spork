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
import com.mirego.rebelchat.models.User;
import com.mirego.rebelchat.utilities.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by marc-antoinehinse on 2015-10-10.
 */

public class RoommateAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private LayoutInflater mInflater = null;

    private ArrayList<User> roommates;

    public RoommateAdapter(Context context, ArrayList<User> roommates) {
        super(context, R.layout.roommate_item, roommates);
        mContext = context;
        this.roommates = roommates;
    }

    static class ViewHolder {
        public TextView name;
        public ImageView avatar;
        public CheckBox checkBox;
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
            rowView = getInflater().inflate(R.layout.roommate_item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) rowView.findViewById(R.id.name);
            holder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
            holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
            rowView.setTag(holder);
        } else{
            rowView = convertView;
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        User r = getItem(position);

        holder.name.setText(r.username);


        if(r.avatar != null) {
            //Picasso.with(getContext()).load(r.avatar).transform(new CircleTransform()).into(holder.avatar);
            holder.avatar.setImageBitmap(r.avatar);
        }

        holder.checkBox.setChecked(r.isChecked);

        return rowView;
    }
}
