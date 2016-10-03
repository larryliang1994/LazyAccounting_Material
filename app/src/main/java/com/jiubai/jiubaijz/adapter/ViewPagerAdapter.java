package com.jiubai.jiubaijz.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jiubai.jiubaijz.R;
import com.jiubai.jiubaijz.common.UtilBox;

import java.util.ArrayList;

/**
 * Created by Larry Howell on 2016/10/2.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private Callback mCallback;
    private View[] views;

    public ViewPagerAdapter(Context context, Callback callback) {
        this.mContext = context;
        this.mCallback = callback;

        views = new View[3];
    }

    /**
     * 获得当前界面数
     */
    @Override
    public int getCount() {
        return 3;
    }

    /**
     * 判断是否由对象生成界面
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;

        if (position == 2) {
            view = LayoutInflater.from(mContext).inflate(R.layout.view_guide_last, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            imageView.setImageBitmap(UtilBox.readBitMap(mContext, R.drawable.guide3));

            Button button = (Button) view.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onStart();
                }
            });
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.view_guide_normal, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

            imageView.setImageBitmap(UtilBox.readBitMap(mContext,
                    position == 0 ? R.drawable.guide1 : R.drawable.guide2));
        }

        views[position] = view;

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views[position]);
    }

    public interface Callback {
        void onStart();
    }
}