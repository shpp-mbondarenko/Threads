package ua.mycompany.threadpool;

import android.graphics.Bitmap;

/**
 * Created by Maxim on 08.05.2016.
 */
public class OneBitmap {

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap;

    public OneBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
