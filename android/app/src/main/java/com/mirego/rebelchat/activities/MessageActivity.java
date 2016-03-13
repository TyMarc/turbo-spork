package com.mirego.rebelchat.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirego.rebelchat.R;
import com.mirego.rebelchat.controllers.MessageController;
import com.mirego.rebelchat.controllers.MessageControllerImpl;
import com.mirego.rebelchat.fragments.ContactPickerFragment;
import com.mirego.rebelchat.transition.ScaleTransition;
import com.mirego.rebelchat.utilities.Encoding;
import com.mirego.rebelchat.utilities.RandomString;
import com.mirego.rebelchat.utilities.Utils;

import java.io.FileDescriptor;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageActivity extends BaseActivity {

    private static String EXTRA_USER_ID = "extra_user_id";
    private static int RESULT_LOAD_IMAGE = 9001;

    private MessageController messageController;
    private String currentUserId;
    private Handler messageHandler;
    private Runnable messageCallback;
    private ContactPickerFragment contactPickerFragment;

    @Bind(R.id.root)
    View root;

    @Bind(R.id.canvas)
    View screenView;

    @Bind(R.id.message_image)
    ImageView messageImage;

    @Bind(R.id.message_text)
    TextView messageText;

    @Bind(R.id.font_reduce)
    Button fontReduce;

    @Bind(R.id.font_grow)
    Button fontGrow;

    @Bind(R.id.color_pick)
    Button colorPick;

    public static Intent newIntent(Activity fromActivity, String userId) {
        Intent intent = new Intent(fromActivity, MessageActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.message_image);

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            imageView.setImageBitmap(Utils.prepareImageFT(screenView.getContext(), bmp));

        }


    }



    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        messageHandler = new Handler();

        messageController = new MessageControllerImpl();
        currentUserId = getIntent().getStringExtra(EXTRA_USER_ID);

        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(true);

        getWindow().setExitTransition(createTransition(true));
        getWindow().setEnterTransition(createTransition(false));
        getWindow().setReturnTransition(createTransition(true));
        getWindow().setReenterTransition(createTransition(false));


        messageCallback = new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);

                Bitmap screenshot = Bitmap.createBitmap(screenView.getWidth(), screenView.getHeight(), Bitmap.Config.ARGB_8888);

                Canvas c = new Canvas(screenshot);
                screenView.layout(0, 0, screenView.getWidth(), screenView.getHeight());
                screenView.draw(c);
                String base64Image = Encoding.encodeImageToBase64(screenshot);

                String text = messageText.getText().toString();


                contactPickerFragment = ContactPickerFragment.newInstance(currentUserId, text, base64Image);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(android.R.id.content, contactPickerFragment).commit();
            }
        };
    }

    private Transition createTransition(boolean goingOut) {
        TransitionSet transitionSet = new TransitionSet();

        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setInterpolator(goingOut ? new AccelerateInterpolator() : new DecelerateInterpolator());
        autoTransition.addTarget(R.id.message_image);

        Slide slideUp = new Slide(Gravity.BOTTOM);
        slideUp.setInterpolator(goingOut ? new AccelerateInterpolator() : new DecelerateInterpolator());
        slideUp.addTarget(R.id.message_text);

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.addTarget(R.id.btn_logout);
        scaleTransition.addTarget(R.id.btn_shuffle);
        scaleTransition.addTarget(R.id.btn_snap);
        scaleTransition.addTarget(R.id.btn_photo);

        transitionSet.addTransition(autoTransition);
        transitionSet.addTransition(slideUp);
        transitionSet.addTransition(scaleTransition);

        transitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        return transitionSet;
    }

    @Override
    public void onBackPressed() {
        startActivity(HomeActivity.newIntent(this), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @OnClick(R.id.btn_logout)
    void onLogoutPressed() {
        startActivity(HomeActivity.newIntent(this), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @OnClick(R.id.btn_shuffle)
    void onShufflePressed() {
        SeeMessagesActivity.show(this, currentUserId);
        //setRandomString();
    }

    @OnClick(R.id.btn_photo)
    void onPhotoPressed() {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
        //setRandomString();
    }

    @OnClick(R.id.btn_snap)
    void onSnapPressed() {
        takeAndSendScreenshot();
    }

    @OnClick(R.id.font_grow)
    void onFontGrowed() {
        this.messageText.setTextSize(this.messageText.getTextSize() + 2);
    }

    @OnClick(R.id.font_reduce)
    void onFontReduced() {
        this.messageText.setTextSize(this.messageText.getTextSize() - 2);
    }

    @OnClick(R.id.color_pick)
    void OnColorPicked() {
    }

    private void setRandomString() {
        String randomString = RandomString.generate(16);
        messageText.setText(randomString);
    }

    public void closePicker(){
        if(contactPickerFragment != null){
            contactPickerFragment.slideOut();
        }
    }

    private void takeAndSendScreenshot() {
        messageHandler.post(messageCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        messageHandler.removeCallbacks(messageCallback);
    }
}
