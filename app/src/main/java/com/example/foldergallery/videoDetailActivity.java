package com.example.foldergallery;

import static com.example.foldergallery.ImageDisplay.allimagesPath;
import static com.example.foldergallery.videoDisplay.allVideoPath;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class videoDetailActivity extends AppCompatActivity {

    String videoPath;
    SeekBar seek_bar;
    ImageButton previou_btn, play_btn, next_btn;
    Drawable pause_btn;
    ImageView delete_vid, share_vid;
    VideoView videoView;
    int position;
    MediaController mediaControls;
    ProgressDialog progressDialog;
    Context context;
    ArrayList<Uri> uris =  new ArrayList<>();
    private Handler mHandler = new Handler();


    @SuppressLint({"ResourceType", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        context = videoDetailActivity.this;

        seek_bar = findViewById(R.id.seek_bar);
        play_btn = findViewById(R.id.play_btn);
        previou_btn = findViewById(R.id.previou_btn);
        next_btn = findViewById(R.id.next_btn);
        videoView = findViewById(R.id.videoview);
        delete_vid =  findViewById(R.id.delete_vid);
        share_vid =  findViewById(R.id.share_vid);



        //videoPath = getIntent().getStringExtra("list");
        position = getIntent().getIntExtra("position", 0);

        videoView.setVideoPath(allVideoPath.get(position));
        videoView.start();

        updateProgressBar();


        delete_vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMyFile(allVideoPath.get(position));
            }
        });
        share_vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(allVideoPath.get(position));
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));
            }
        });
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (videoView.isPlaying()) {
                    videoView.pause();
                    play_btn.setImageResource(R.drawable.baseline_play_arrow_24);
                } else {
                    play_btn.setImageResource(R.drawable.baseline_pause_24);
                    videoView.start();
                }
            }
        });



        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(updateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(updateTimeTask);
                videoView.seekTo(seek_bar.getProgress());
                updateProgressBar();
            }
        });

        previou_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 0) {
                    return;
                }

                if (position > 0) {
                    videoView.setVideoPath(allVideoPath.get(--position));
                }
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == allVideoPath.size()-1) {
                    return;
                }
                if (position >= 0) {
                    videoView.setVideoPath(allVideoPath.get(++position));
                }
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play_btn.setImageResource(R.drawable.baseline_play_arrow_24);
            }
        });
    }

    public void scanFile(Context context, String path) {
        MediaScannerConnection.scanFile(context,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    void deleteMyFile(String path) {
        scanFile(context, path);

        File file3 = new File(path);

        // If the file is deleted then the path will be scanned first
        if (file3.delete()) {

            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        // After the scan is completed , flow will come to previous activity with updated files
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("TAG", "Finished scanning " + path);
                            setUpBack();
                        }
                    });

        }
        // if the file is not deleted then URI of the given file will be got using mediaID and the delete
        // permission will be requested using requestDeletePermission(uris)
        else {
            long mediaID = getFilePathToMediaID(file3.getAbsolutePath(), getApplicationContext());
            Uri Uri_one = ContentUris.withAppendedId(MediaStore.Video.Media.getContentUri("external"), mediaID);
            uris.clear();
            uris.add(Uri_one);
            requestDeletePermission(uris);
        }
        // At last the path will be scanned again to cross check
        scanFile(context, path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the User denies to delete the file then it will go back to previous activity
        if (requestCode == 1000 && resultCode == -1) {
            setUpBack();
        }
    }

    // For SDKs >= Android 11 , mechanism of PendingIntent will be used to get the confirmation from the user
    private void requestDeletePermission(List<Uri> uriList) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            PendingIntent pi = MediaStore.createDeleteRequest(getContentResolver(), uriList);

            try {
                startIntentSenderForResult(pi.getIntentSender(), 1000, null, 0, 0,
                        0);
            } catch (IntentSender.SendIntentException e) {
            }
        }
    }

    public long getFilePathToMediaID(String songPath, Context context) {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Images.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = {MediaStore.Images.Media._ID};
        String sortOrder = MediaStore.Images.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }
        return id;
    }

    private void setUpBack() {
        finish();
    }

    private void updateProgressBar() {
        mHandler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            seek_bar.setMax(videoView.getDuration());
            seek_bar.setProgress(videoView.getCurrentPosition());
            mHandler.postDelayed(this, 100);
        }
    };

}