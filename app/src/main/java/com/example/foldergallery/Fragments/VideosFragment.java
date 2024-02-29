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
import android.widget.Toast;

import com.example.foldergallery.Adapter.itemClickListener;

import com.example.foldergallery.Adapter.videoFolderAdapter;
import com.example.foldergallery.R;
import com.example.foldergallery.utils.PicHolder;

import com.example.foldergallery.utils.pictureFacer;
import com.example.foldergallery.utils.videoFolder;
import com.example.foldergallery.videoDisplay;

import java.util.ArrayList;

public class VideosFragment extends Fragment implements itemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    RecyclerView folderRecycler;
    Context videoContext;
    TextView empty;
    ProgressDialog progressDialog;
    public VideosFragment(Context context) {
        // Required empty public constructor
        this.videoContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(videoContext);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("ProgressDialog");

        folderRecycler = view.findViewById(R.id.folderRecyclervid);

    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(()->{
            requireActivity().runOnUiThread(()-> progressDialog.show());
            ArrayList<videoFolder> folds = getPicturePaths();

            requireActivity().runOnUiThread(()->{
                if (folds.size() <= 0) {
                    //empty.setVisibility(View.VISIBLE);
                    //Toast.makeText(videoContext, "Empty", Toast.LENGTH_SHORT).show();
                } else {
                    folderRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                    folderRecycler.setHasFixedSize(true);
                    RecyclerView.Adapter folderAdapter = new videoFolderAdapter(folds, videoContext, VideosFragment.this);
                    folderRecycler.setAdapter(folderAdapter);
                }
                progressDialog.dismiss();

            });
        }).start();

        //Log.d("lifecycle","onResume invoked");
    }
    private ArrayList<videoFolder> getPicturePaths() {
        ArrayList<videoFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();

        Uri allvideosuri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.BUCKET_ID};

        Cursor cursor = requireActivity().getContentResolver().query(allvideosuri, projection, null, null, null);
        while (cursor.moveToNext()) {
            try {
                videoFolder folds = new videoFolder();
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
                    folds.setFirstvid(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addvid();
                    picFolders.add(folds);
                } else {
                    for (int i = 0; i < picFolders.size(); i++) {
                        if (picFolders.get(i).getPath().equals(folderpaths)) {
//                            picFolders.get(i).setFirstvid(datapath);
                            picFolders.get(i).addvid();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < picFolders.size(); i++) {
//             Log.d("VideoSfolders", picFolders.get(i).getFolderName() + " and path = " + picFolders.get(i).getPath() + " " + picFolders.get(i).getNumberOfvids());
        }

        // Toast.makeText(videoContext, "" + picFolders.size(), Toast.LENGTH_SHORT).show();

        return picFolders;
    }

    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

    }


    @Override
    public void onPicClicked(String pictureFolderPath, String folderName) {
        //Intent move = new Intent(Context, ImageDisplay.class);
        Intent i = new Intent(getContext(), videoDisplay.class);
        i.putExtra("folderPath", pictureFolderPath);
        i.putExtra("folderName", folderName);
        startActivity(i);
    }
}