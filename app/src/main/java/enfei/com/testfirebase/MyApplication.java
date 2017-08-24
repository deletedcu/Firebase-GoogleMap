package enfei.com.testfirebase;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import enfei.com.testfirebase.services.GoogleApiHelper;

/**
 * Created by king on 17/08/2017.
 */

public class MyApplication extends Application {

    public static ImageLoader mImageLoader;
    private GoogleApiHelper googleApiHelper;
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        mInstance = this;
        googleApiHelper = new GoogleApiHelper(mInstance);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }
    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }

    private static void initImageLoader(Context context) {
        mImageLoader = ImageLoader.getInstance();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        mImageLoader.init(configuration);
    }

}
