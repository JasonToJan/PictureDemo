package picture.read.picturedemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    TextView photoPathsTv;
    /**
     * 本地图片
     */
    ArrayList<String> photos;
    /**
     * 是否正在轮播
     */
    boolean isRecycling;
    
    private ViewPager viewPager;
    boolean isRunning = false;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoPathsTv=findViewById(R.id.am_tv);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOnPageChangeListener(this);
        imageView=new ImageView(this);
    }

    public void selectPicture(View view){
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    public void beginRecycle(View view){
        if(!isRecycling&&photos.size()>0){
            isRecycling=true;
            viewPager.setAdapter(new MyAdapter(photos,this));
            new Thread() {
                public void run() {
                    isRunning = true;
                    while (isRunning) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            }

                        });
                    }

                }
            }.start();
        }else{
            isRecycling=false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                StringBuilder stringBuilder=new StringBuilder();
                stringBuilder.append("选择的图片路径为：\n");
                for(int i=0;i<photos.size();i++){
                    stringBuilder.append((i+1)+":");
                    stringBuilder.append(photos.get(i));
                    stringBuilder.append("\n");
                }
                photoPathsTv.setText(stringBuilder.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    class MyAdapter extends PagerAdapter {

        private ArrayList<String> images;
        private Context context;

        public MyAdapter(ArrayList<String> images,Context context){
            this.images=images;
            this.context=context;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            int newPosition = position % images.size();
            Bitmap bitmap=BitmapFactory.decodeFile(images.get(newPosition));

            ImageView imageView=new ImageView(context);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        int newPosition = position % photos.size();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
