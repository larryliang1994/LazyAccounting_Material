package com.jiubai.jiubaijz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Larry Howell on 2016/9/23.
 *
 * 右上角下拉菜单adapter
 */

public class MenuListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Integer> imageIds;
    private ArrayList<String> titles;

    public MenuListAdapter(Context context, ArrayList<Integer> imageIds, ArrayList<String> titles) {
        this.context = context;
        this.imageIds = imageIds;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return imageIds.size();
    }

    @Override
    public Object getItem(int i) {
        return titles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return imageIds.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.menu_item, null);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.imageView_item);
        TextView textView = (TextView) contentView.findViewById(R.id.textView_item);

        imageView.setImageResource(imageIds.get(i));
        textView.setText(titles.get(i));

        return contentView;
    }
}
