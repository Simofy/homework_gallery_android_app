package com.simofy.homework_gallery;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Paprasta mobilioji aplikacija
 *
 * @author Julius Simas Simutis
 * @version 1.0
 * @since 2018-09-26
 */

public class MainActivity extends AppCompatActivity {
    //
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("dogs_urls.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    //Kintamieji skirti dinaminiam nuotraku uzkrovimui
    ArrayList<String> dog_list = new ArrayList<String>();
    Iterator<String> dog_it;
    ImageView lastLoad;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JSONArray m_jArry = new JSONArray();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            m_jArry = obj.getJSONArray("urls");
            //perrasau i lengvesnio containerio forma
            for (int i = 0; i < m_jArry.length(); i++) {
                dog_list.add(m_jArry.getString(i));
            }
            dog_it = dog_list.iterator();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //paprastas mechanizmas kuris populiuoja galerija
        for (int i = 0; i < 10; i++) {
            addImage(i % 2 == 0);
        }
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_main);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                                                                        @Override
                                                                        public void onScrollChanged() {
                                                                            int[] pos = new int[2];
                                                                            lastLoad.getLocationOnScreen(pos);
                                                                            if (MainActivity.this.getScreenHeight() + 200 > pos[1]) {
                                                                                if (dog_it.hasNext())
                                                                                    for (int i = 0; i < 4; i++) {
                                                                                        addImage(i % 2 == 0);
                                                                                    }
                                                                            }
                                                                        }
                                                                    }
        );
    }

    private void addImage(boolean side) {
        if (dog_it.hasNext()) {
            int main_width = MainActivity.this.getScreenWidth();
            int scroll_width = 0;
            if (main_width < 1200) scroll_width = main_width / 2;
            else
                scroll_width = 600;
            final ImageView imageView = new ImageView(MainActivity.this);
            LinearLayout lv_dog = (LinearLayout) findViewById(side ? R.id.lv_dog_right : R.id.lv_dog_left);
            lv_dog.addView(imageView);
            Picasso.get().load(dog_it.next()).networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(scroll_width, 0)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final ImageView imageView_popup = new ImageView(MainActivity.this);
                    imageView_popup.setImageDrawable(imageView.getDrawable());
                    FrameLayout frame = (FrameLayout) findViewById(R.id.view_main);
                    imageView_popup.bringToFront();
                    imageView_popup.setBackgroundColor(Color.argb(100, 150, 100, 100));
                    imageView_popup.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    frame.addView(imageView_popup);
                    imageView_popup.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            FrameLayout parent = (FrameLayout) imageView_popup.getParent();
                            if (parent != null) {
                                parent.removeView(imageView_popup);
                            }
                        }
                    });
                }
            });
            lastLoad = imageView;
        }
    }
}
