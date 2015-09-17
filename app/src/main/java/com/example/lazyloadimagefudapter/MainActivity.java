package com.example.lazyloadimagefudapter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lvPost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        ImageLoader.getInstance().init(UILConfig());

        ArrayList<Post> posts = SampleData.postData();

        BindDictionary<Post> dict = new BindDictionary<Post>();
        dict.addStringField(R.id.tvDesc,
                new StringExtractor<Post>() {
                    @Override
                    public String getStringValue(Post post, int i) {
                        return post.desc;
                    }
                }
        );
        dict.addStringField(R.id.tvImageUrl,
                new StringExtractor<Post>() {
                    @Override
                    public String getStringValue(Post post, int i) {
                        return post.imageUrl.toString();
                    }
                }
        );
        dict.addStringField(R.id.tvFav,
                new StringExtractor<Post>() {
                    @Override
                    public String getStringValue(Post post, int i) {
                        return post.fav.toString() + " likes";
                    }
                }
        );

        dict.addDynamicImageField(R.id.ivImage,
                new StringExtractor<Post>() {
                    @Override
                    public String getStringValue(Post post, int i) {
                        return post.imageUrl;
                    }
                },
                new DynamicImageLoader() {
                    @Override
                    public void loadImage(String imageUrl, ImageView imageView) {
                        Log.d("imageUrl", imageUrl);

                        ImageLoader.getInstance().displayImage(imageUrl, imageView);
                    }
                }
        );

        FunDapter adapter = new FunDapter(this, posts,
                R.layout.layout_post, dict);
        lvPost = (ListView)findViewById(R.id.lvPost);
        lvPost.setAdapter(adapter);

    }

    private ImageLoaderConfiguration UILConfig(){
        /** To make the image fill the width and keep height ratio.**/
//        <ImageView
//        android:layout_width="fill_parent" //fill_width #1
//        android:layout_height="wrap_content" //fill_width #2
//        android:id="@+id/ivImage"
//        android:src="@android:drawable/gallery_thumb"
//        android:scaleType="fitCenter"   //fill_width #3
//        android:adjustViewBounds="true" //fill_width #4
//                />
        final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)  //cache #1
                .cacheOnDisk(true) //cache #2
                .showImageOnLoading(android.R.drawable.stat_sys_download)
                .showImageForEmptyUri(android.R.drawable.ic_dialog_alert)
                .showImageOnFail(android.R.drawable.stat_notify_error)
                .considerExifParams(true) //cache #3
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) //fill_width #5
                .build();

        ////cache #4
        //add <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> to manifest
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        return config;
    }


    
}
