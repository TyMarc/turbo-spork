package com.mirego.rebelchat.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mirego.rebelchat.R;
import com.mirego.rebelchat.adapters.ContactPickerAdapter;
import com.mirego.rebelchat.adapters.RoommateAdapter;
import com.mirego.rebelchat.controllers.MessageController;
import com.mirego.rebelchat.controllers.MessageControllerImpl;
import com.mirego.rebelchat.controllers.UsersController;
import com.mirego.rebelchat.controllers.UsersControllerImpl;
import com.mirego.rebelchat.models.User;


public class ContactPickerFragment extends Fragment implements OnItemClickListener, OnClickListener, UsersController.UsersCallback {
	public static final int MODE_GROUP = 1;
	private int mode = -1;
	private ListView listView;
	private ContactPickerAdapter adapter;
	private SparseBooleanArray checkedUsers;
	private UsersController usersController;
	private MessageController messageController;
	private String currentUserId;
	private String text;
	private String base64;


	public static ContactPickerFragment newInstance(final String currentUserId, final String text, final String base64image){
		ContactPickerFragment fragment = new ContactPickerFragment();
		Bundle b = new Bundle();
		b.putString("currentUserId", currentUserId);
		b.putString("text", text);
		b.putString("base64", base64image);
		fragment.setArguments(b);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.contact_picker, container, false);

		currentUserId = getArguments().getString("currentUserId");
		text = getArguments().getString("text");
		base64 = getArguments().getString("base64");
		usersController = new UsersControllerImpl();
		messageController = new MessageControllerImpl();
		listView = (ListView) v.findViewById(R.id.list);
		listView.setOnItemClickListener(this);

		listView.setEmptyView(v.findViewById(android.R.id.empty));

		v.findViewById(R.id.close).setOnClickListener(this);
		v.findViewById(R.id.done).setOnClickListener(this);

		return v;
	}

	@Override
	public void onStart(){
		super.onStart();
		slideIn();
	}

	private void slideIn(){
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
		getView().findViewById(R.id.container).startAnimation(anim);
	}

	public void slideOut(){
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
		anim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				getActivity().getFragmentManager().beginTransaction().remove(ContactPickerFragment.this).commit();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationStart(Animation arg0) {

			}});
		getView().findViewById(R.id.container).startAnimation(anim);
	}

	@Override
	public void onResume(){
		super.onResume();

		refreshList();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Log.i("ContactPickerFragment", "itemClicked");
		checkedUsers = listView.getCheckedItemPositions();
		adapter.checkContact(position);
		adapter.notifyDataSetChanged();
	}

	private void finish(){
		if(checkedUsers != null){

		} else{
			slideOut();
		}
	}


	public void refreshList() {
		usersController.getUsers(getActivity(), "", this);
	}

	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.close) {
			slideOut();
		} else if(arg0.getId() == R.id.done) {
			if(adapter != null) {
				for(User user : adapter.getCheckedUsers()) {
					messageController.sendMessage(getActivity(), currentUserId, text, base64, user.userId, false, new MessageController.SendMessageCallback() {
						@Override
						public void onSendMessageSuccess() {
							if(getActivity() != null) {
								//Toast.makeText(getActivity(), R.string.message_send_success, Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onSendMessageFail() {
							if(getActivity() != null) {
								Toast.makeText(getActivity(), R.string.message_send_error, Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
				slideOut();
			}
		}
	}

	@Override
	public void onUsersSuccess(final ArrayList<User> users) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter = new ContactPickerAdapter(getActivity(), users);
				listView.setAdapter(adapter);
			}
		});

	}

	@Override
	public void onUsersFailed() {

	}
}
