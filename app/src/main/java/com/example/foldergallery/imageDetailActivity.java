package com.example.foldergallery;

import static androidx.core.view.ViewCompat.setTransitionName;

import static com.example.foldergallery.ImageDisplay.allimagesPath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foldergallery.utils.pictureFacer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class imageDetailActivity extends AppCompatActivity {
    private ArrayList<pictureFacer> allImages = new ArrayList<>();
    private int position;
    String path;
    private ImageView image, delete_img;
    private ImageButton share_btn;
    private ViewPager imagePager;
    private int viewVisibilityController;
    private int viewVisibilitylooper;
    ImagesPagerAdapter pagingImages;
    Context context;
    ArrayList<Uri> uris =  new ArrayList<>();
    private int previousSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        context = imageDetailActivity.this;

        viewVisibilityController = 0;
        viewVisibilitylooper = 0;
        imagePager = findViewById(R.id.imagePager);
        share_btn = findViewById(R.id.share_btn);
        delete_img = findViewById(R.id.delete_img);
        position = getIntent().getIntExtra("position", 0);

        pagingImages = new ImagesPagerAdapter();
        imagePager.setAdapter(pagingImages);
//        imagePager.setOffscreenPageLimit(3);
        imagePager.setCurrentItem(position);//displaying the image at the current position passed by the ImageDisplay Activity

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(allimagesPath.get(position));
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));

            }
        });
        delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMyFile(allimagesPath.get(position));

            }
        });
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imageDetailActivity.this.position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
            Uri Uri_one = ContentUris.withAppendedId(MediaStore.Images.Media.getContentUri("external"), mediaID);
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


    private class ImagesPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return allimagesPath.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup containerCollection, int position) {
            LayoutInflater layoutinflater = (LayoutInflater) containerCollection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutinflater.inflate(R.layout.picture_browser_pager, null);
            image = view.findViewById(R.id.image);


            //setTransitionName(image, String.valueOf(position) + "picture");
//            pictureFacer pic = allImages.get(position);
            Glide.with(imageDetailActivity.this)
                    .load(allimagesPath.get(position))
                    .apply(new RequestOptions().fitCenter())
                    .into(image);
            ((ViewPager) containerCollection).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup containerCollection, int position, Object view) {
            ((ViewPager) containerCollection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View) object);

        }
    }
}