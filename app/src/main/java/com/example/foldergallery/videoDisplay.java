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
import android.view.View;
import android.widget.TextView;

import com.example.foldergallery.Adapter.itemClickListener;
import com.example.foldergallery.Adapter.picture_Adapter;
import com.example.foldergallery.utils.PicHolder;
import com.example.foldergallery.utils.pictureFacer;

import java.util.ArrayList;

public class videoDisplay extends AppCompatActivity implements itemClickListener {

    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;

    public static ArrayList<String> allVideoPath = new ArrayList<>();

    String foldePath;
    TextView folderName;
    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);
        context = videoDisplay.this;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loding...");
        progressDialog.setTitle("Video Load...");
        folderName = findViewById(R.id.foldername);
        folderName.setText(getIntent().getStringExtra("folderName"));
        foldePath = getIntent().getStringExtra("folderPath");

        new Thread(() -> {

            runOnUiThread(()-> progressDialog.show());

            allpictures = new ArrayList<>();
            runOnUiThread(()->{

                imageRecycler = findViewById(R.id.recycler);
        imageRecycler.setLayoutManager(new GridLayoutManager(videoDisplay.this, 4));
        imageRecycler.hasFixedSize();
        allpictures = getAllImagesByFolder(foldePath);
        imageRecycler.setAdapter(new picture_Adapter(allpictures, (Context) videoDisplay.this, this));
                progressDialog.dismiss();
            });

        }).start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {

            runOnUiThread(()-> progressDialog.show());

            allpictures = new ArrayList<>();
            runOnUiThread(()->{

                imageRecycler = findViewById(R.id.recycler);
                imageRecycler.setLayoutManager(new GridLayoutManager(videoDisplay.this, 4));
                imageRecycler.hasFixedSize();
                allpictures = getAllImagesByFolder(foldePath);
                imageRecycler.setAdapter(new picture_Adapter(allpictures, (Context) videoDisplay.this, this));
                progressDialog.dismiss();
            });

        }).start();

    }
    public ArrayList<pictureFacer> getAllImagesByFolder(String path) {
        ArrayList<pictureFacer> images = new ArrayList<>();

        allVideoPath.clear();

        Uri allVideosuri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE};
        Cursor cursor = videoDisplay.this.getContentResolver().query(allVideosuri, projection, MediaStore.Video.Media.DATA + " like ? ", new String[]{"%" + path + "%"}, null);
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
            allVideoPath.add(images.get(i).getPicturePath());
        }
        return images;
    }


    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {
       Intent i = new Intent(context, videoDetailActivity.class);
        i.putExtra("position", position);
        context.startActivity(i);
    }
    @Override
    public void onPicClicked(String pictureFolderPath, String folderName) {

    }
}