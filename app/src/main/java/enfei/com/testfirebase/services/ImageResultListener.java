package enfei.com.testfirebase.services;

import android.graphics.Bitmap;

/**
 * Created by king on 18/08/2017.
 */

public interface ImageResultListener {
    public void onResult(boolean isSuccess, String error, Bitmap bitmap);
}
