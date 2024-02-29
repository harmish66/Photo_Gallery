package com.example.foldergallery.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.foldergallery.Adapter.itemClickListener;
import com.example.foldergallery.Adapter.pictureFolderAdapter;
import com.example.foldergallery.ImageDisplay;
import com.example.foldergallery.R;
import com.example.foldergallery.utils.PicHolder;
import com.example.foldergallery.utils.imageFolder;
import com.example.foldergallery.utils.pictureFacer;

import java.util.ArrayList;

public class ImagesFragment extends Fragment implements itemClickListener {


    private Context folderContx;

    RecyclerView folderRecycler;
    TextView empty;
    Context imageContext;

    itemClickListener itemClickListenerimg;

    ProgressDialog progressDialog;
    public ImagesFragment(Context context) {
        // Required empty public constructor
        this.imageContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_images, container, false);


    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(imageContext);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("ProgressDialog");

        folderRecycler = view.findViewById(R.id.folderRecycler);
        folderRecycler.hasFixedSize();


    }
    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            requireActivity().runOnUiThread(() -> progressDialog.show());

            ArrayList<imageFolder> folds = getPicturePaths();

            requireActivity().runOnUiThread(() -> {
                if (folds.isEmpty()) {
                    //  empty.setVisibility(View.VISIBLE);
                } else {
                    folderRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                    folderRecycler.setHasFixedSize(true);
                    RecyclerView.Adapter folderAdapter = new pictureFolderAdapter(folds, imageContext, ImagesFragment.this);
                    folderRecycler.setAdapter(folderAdapter);
                }
                progressDialog.dismiss();
            });
        }).start();

        //Log.d("lifecycle","onResume invoked");
    }
    private ArrayList<imageFolder> getPicturePaths() {
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = getActivity().getContentResolver().query(allImagesuri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do {
                imageFolder folds = new imageFolder();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));


                //String folderpaths =  datapath.replace(name,"");
                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder + "/"));
                folderpaths = folderpaths + folder + "/";
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths);

                    folds.setPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics();
                    picFolders.add(folds);
                } else {
                    for (int i = 0; i < picFolders.size(); i++) {
                        if (picFolders.get(i).getPath().equals(folderpaths)) {
                            picFolders.get(i).setFirstPic(datapath);
                            picFolders.get(i).addpics();
                        }
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < picFolders.size(); i++) {
             //Log.d("picfolders", picFolders.get(i).getFolderName() + " and path = " + picFolders.get(i).getPath() + " " + picFolders.get(i).getNumberOfPics());

        }

        return picFolders;
    }

    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

    }

    public void onPicClicked(String pictureFolderPath, String folderName) {

        Intent i = new Intent(getContext(), ImageDisplay.class);
        i.putExtra("folderPath", pictureFolderPath);
        i.putExtra("folderName", folderName);
        startActivity(i);
    }


}

