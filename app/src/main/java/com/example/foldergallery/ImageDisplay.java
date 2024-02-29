package com.example.foldergallery;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import com.example.foldergallery.Adapter.itemClickListener;
import com.example.foldergallery.Adapter.picture_Adapter;
import com.example.foldergallery.utils.PicHolder;
import com.example.foldergallery.utils.pictureFacer;

import java.util.ArrayList;

public class ImageDisplay extends AppCompatActivity implements itemClickListener {
    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;

    public static ArrayList<String> allimagesPath = new ArrayList<>();

    String foldePath;
    TextView folderName;

    Context context;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        folderName = findViewById(R.id.foldername);
        folderName.setText(getIntent().getStringExtra("folderName"));
        foldePath = getIntent().getStringExtra("folderPath");

        mProgressDialog = new ProgressDialog(ImageDisplay.this);
        mProgressDialog.setMessage("Loding...");
        mProgressDialog.setTitle("Image Load...");
        mProgressDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(() -> {

            runOnUiThread(()-> mProgressDialog.show());
            allpictures = new ArrayList<>();
            runOnUiThread(()->{
                imageRecycler = findViewById(R.id.recycler);
                imageRecycler.setLayoutManager(new GridLayoutManager(ImageDisplay.this, 4));
                allpictures = getAllImagesByFolder(foldePath);
                imageRecycler.setAdapter(new picture_Adapter(allpictures, (Context) ImageDisplay.this, ImageDisplay.this));
                mProgressDialog.dismiss();
            });
        }).start();

    }
    public ArrayList<pictureFacer> getAllImagesByFolder(String path) {
        ArrayList<pictureFacer> images = new ArrayList<>();
        allimagesPath.clear();
        Uri allVideosuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = ImageDisplay.this.getContentResolver().query(allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[]{"%" + path + "%"}, null);
        try {
            cursor.moveToFirst();
            do {
                pictureFacer pic = new pictureFacer();

                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                images.add(pic);
            } while (cursor.moveToNext());
            cursor.close();
            ArrayList<pictureFacer> reSelection = new ArrayList<>();
            for (int i = images.size() - 1; i > -1; i--) {
                reSelection.add(images.get(i));
            }
            images = reSelection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < images.size(); i++) {
            allimagesPath.add(images.get(i).getPicturePath());
        }

        return images;
    }
    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

//        holder.videoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
        Intent i = new Intent(ImageDisplay.this, imageDetailActivity.class);
        i.putExtra("position", position);
        ImageDisplay.this.startActivity(i);


    }

/*

    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

        pictureBrowserFragment browser = pictureBrowserFragment.newInstance(pics, position, ImageDisplay.this);




        // Note that we need the API version check here because the actual transition classes (e.g. Fade)
        // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
        // ARE available in the support library (though they don't do anything on API < 21)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //browser.setEnterTransition(new Slide());
            //browser.setExitTransition(new Slide()); uncomment this to use slide transition and comment the two lines below
            browser.setEnterTransition(new Fade());
            browser.setExitTransition(new Fade());
        }
       */
/* getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.picture, position + "picture")
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();*//*

    }
*/


    @Override
    public void onPicClicked(String pictureFolderPath, String folderName) {

    }
}