package com.example.foldergallery.Adapter;

import com.example.foldergallery.utils.PicHolder;
import com.example.foldergallery.utils.pictureFacer;

import java.util.ArrayList;

public interface itemClickListener {




    void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics);

    void onPicClicked(String pictureFolderPath,String folderName);
}
