package enfei.com.testfirebase.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import enfei.com.testfirebase.MyApplication;
import enfei.com.testfirebase.R;

/**
 * Created by king on 18/08/2017.
 */

public class ImageFragment extends Fragment {

    private String photoValue;
    private ImageView imageView;
    private ProgressBar progressBar;

    public ImageFragment() {
        super();
    }

    public static ImageFragment newInstance(String photoValue) {
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("photoValue", photoValue);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        photoValue = bundle.getString("photoValue");
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        View view = inflator.inflate(R.layout.item_image, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        imageView = (ImageView) view.findViewById(R.id.imageView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        String photoUrl = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=%s", photoValue, getString(R.string.google_server_api_key));
        MyApplication.mImageLoader.displayImage(photoUrl, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progressBar.setVisibility(View.GONE);
                if (bitmap == null) {
                    imageView.setImageResource(R.drawable.bg_place_bottom);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
