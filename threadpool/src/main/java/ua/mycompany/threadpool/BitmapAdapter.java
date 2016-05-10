package ua.mycompany.threadpool;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Maxim on 08.05.2016.
 */
public class BitmapAdapter extends BaseAdapter {

    private Context context;
    private OneBitmap[] bitmaps;

    public BitmapAdapter(Context context, OneBitmap[] bitmaps) {
        this.context = context;
        this.bitmaps = bitmaps;
    }

    @Override
    public int getCount() {
        return bitmaps.length;
    }

    @Override
    public Object getItem(int position) {
        return bitmaps[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imgView;

        if (convertView == null) {
           imgView = new ImageView(context);
            imgView.setLayoutParams(new GridView.LayoutParams(300,300));
//            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgView.setPadding(1,1,1,1);

        } else {
            imgView = (ImageView) convertView;
        }
            imgView.setImageBitmap(bitmaps[position].getBitmap());

        return imgView;
    }
}
