package com.example.foldergallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;

import com.example.foldergallery.Fragments.ImagesFragment;
import com.example.foldergallery.Fragments.VideosFragment;
import com.example.foldergallery.Adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        viewPager = findViewById(R.id.viewpager);


        viewPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new ImagesFragment(context),"Images");
        viewPagerAdapter.add(new VideosFragment(context),"Videos");
        //viewPagerAdapter.add(new Temp(context),"Temp");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout =  findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


    }

}