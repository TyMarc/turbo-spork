package com.mirego.rebelchat.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirego.rebelchat.utilities.Utils;
import com.mirego.rebelchat.views.CheckableImageView;
import com.mirego.rebelchat.R;
import com.mirego.rebelchat.models.User;

public class ContactPickerAdapter extends ArrayAdapter<User>{
	private Context mContext;
	private LayoutInflater mInflater = null;

	private ArrayList<User> contacts;

	public ContactPickerAdapter(Context context, ArrayList<User> contacts) {
		super(context, R.layout.contact_picker_item, contacts);
		mContext = context;
		this.contacts = contacts;     
	}

	public ArrayList<User> getCheckedUsers() {
		ArrayList<User> users = new ArrayList<User>();
		for(User user : contacts) {
			if(user.isChecked) {
				users.add(user);
			}
		}

		return users;
	}

	static class ViewHolder {
	    public TextView name;
		public TextView email;
	    public ImageView avatar;
	    public CheckableImageView checkbox;
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
			rowView = getInflater().inflate(R.layout.contact_picker_item, parent, false);
			
			ViewHolder holder = new ViewHolder();
			holder.name = (TextView) rowView.findViewById(R.id.name);
			holder.email = (TextView) rowView.findViewById(R.id.email);
			holder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
			holder.checkbox = (CheckableImageView) rowView.findViewById(R.id.checkbox);
			rowView.setTag(holder);
		} else{
			rowView = convertView;
		}

		User user = contacts.get(position);
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		holder.name.setText(user.username);
		holder.email.setText(user.email);
		
		if(user.avatar != null){
			holder.avatar.setImageBitmap(Utils.cropToCircle(user.avatar));
		}
		
		
		holder.checkbox.setChecked(contacts.get(position).isChecked);

		return rowView;
	}

	public void unCheckAll() {
		for(User c : contacts){
			c.isChecked = false;
		}
	}

	public void checkContact(int position) {
		contacts.get(position).isChecked = (!contacts.get(position).isChecked);
	}
	
}
