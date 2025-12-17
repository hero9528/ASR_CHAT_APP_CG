
package com.funo.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class AvatarPicker extends BaseAdapter {

    private final Context mContext;
    private final String[] mAvatars;

    public AvatarPicker(Context context, String[] avatars) {
        mContext = context;
        mAvatars = avatars;
    }

    @Override
    public int getCount() {
        return mAvatars.length;
    }

    @Override
    public Object getItem(int position) {
        return mAvatars[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        int resId = mContext.getResources().getIdentifier(mAvatars[position], "drawable", mContext.getPackageName());
        imageView.setImageResource(resId);
        return imageView;
    }
}