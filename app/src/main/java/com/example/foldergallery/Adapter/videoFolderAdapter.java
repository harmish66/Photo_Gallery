package com.example.foldergallery.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foldergallery.R;
import com.example.foldergallery.utils.videoFolder;

import java.util.ArrayList;

public class videoFolderAdapter extends RecyclerView.Adapter<videoFolderAdapter.FolderHoldervideo> {
    private ArrayList<videoFolder> videoFolders;
    private Context videofolderContx;
    private itemClickListener listenToClick;

    public videoFolderAdapter(ArrayList<videoFolder> videoFolders, Context videofolderContx, itemClickListener listen) {
        this.videoFolders = videoFolders;
        this.videofolderContx = videofolderContx;
        this.listenToClick = listen;
        //Log.d("Size_folders","" + folders.size());
    }




    @NonNull
    public FolderHoldervideo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.videos_folder_item, parent, false);
        return new FolderHoldervideo(cell);
    }


    public void onBindViewHolder(@NonNull videoFolderAdapter.FolderHoldervideo holder, int position) {
        final videoFolder folder = videoFolders.get(position);

        Glide.with(videofolderContx)
                .load(folder.getFirstPic())
                .apply(new RequestOptions().centerCrop())
                .into(holder.folderPic);

        //setting the number of images
        String text = "" + folder.getFolderName();
        String folderSizeString = "" + folder.getNumberOfvids() + " Media";
        holder.folderSize.setText(folderSizeString);
        holder.folderName.setText(text);

        holder.folderPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenToClick.onPicClicked(folder.getPath(), folder.getFolderName());
            }
        });
    }



    public int getItemCount() {
     //   Log.d("Countvideoas", "getItemCount: "+videoFolders.size());
        return videoFolders.size();
    }

    public class FolderHoldervideo extends RecyclerView.ViewHolder {

        ImageView folderPic;
        TextView folderName;
        TextView folderSize;
        CardView folderCard;

        public FolderHoldervideo(@NonNull View itemView) {
            super(itemView);


            folderPic = itemView.findViewById(R.id.folderPic);
            folderName = itemView.findViewById(R.id.folderName);
            folderSize = itemView.findViewById(R.id.folderSize);
            folderCard = itemView.findViewById(R.id.folderCard);
        }
    }

}