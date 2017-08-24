package enfei.com.testfirebase.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import enfei.com.testfirebase.Constants;
import enfei.com.testfirebase.MyApplication;
import enfei.com.testfirebase.R;
import enfei.com.testfirebase.adapters.FoodAdapter;
import enfei.com.testfirebase.models.Food;
import enfei.com.testfirebase.models.Restaurant;
import enfei.com.testfirebase.services.FirebaseService;
import enfei.com.testfirebase.services.ImageResultListener;
import enfei.com.testfirebase.services.ResultListener;

/**
 * Created by king on 17/08/2017.
 */

public class RestaurantActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnBack;
    private ImageView btnInfo;
    private ImageView ivImage;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private TextView tvAddress;
    private RecyclerView mRecyclerViewStarters;
    private RecyclerView mRecyclerViewDrinks;
    private RecyclerView mRecyclerViewDishes;
    private RecyclerView mRecyclerViewDesserts;
    private ProgressBar progressBar;

    private List<Food> mStarters;
    private List<Food> mDrinks;
    private List<Food> mDishes;
    private List<Food> mDesserts;

    private ProgressDialog progressDialog;

    private Restaurant restaurant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Bundle bundle = getIntent().getExtras();
        restaurant = bundle.getParcelable("value");

        btnBack = (ImageView) findViewById(R.id.iv_back);
        btnInfo = (ImageView) findViewById(R.id.iv_info);
        ivImage = (ImageView) findViewById(R.id.imageView);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSubTitle = (TextView) findViewById(R.id.tv_subTitle);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        mRecyclerViewStarters = (RecyclerView) findViewById(R.id.listview_starter);
        mRecyclerViewDrinks = (RecyclerView) findViewById(R.id.listview_drinks);
        mRecyclerViewDishes = (RecyclerView) findViewById(R.id.listview_dishes);
        mRecyclerViewDesserts = (RecyclerView) findViewById(R.id.listview_desserts);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnBack.setOnClickListener(this);
        btnInfo.setOnClickListener(this);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewStarters.setLayoutManager(layoutManager1);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewDrinks.setLayoutManager(layoutManager2);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewDishes.setLayoutManager(layoutManager3);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewDesserts.setLayoutManager(layoutManager4);

        initData();
        loadData();
    }

    @Override
    public void onPause() {
        if (progressDialog != null)
            progressDialog.dismiss();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (progressDialog != null)
            progressDialog.dismiss();
        super.onDestroy();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private void initData() {
        if (restaurant != null) {
            tvTitle.setText(restaurant.name);
            tvSubTitle.setText(restaurant.subTitle);
            tvAddress.setText(restaurant.address);

            if (restaurant.photos == null || restaurant.photos.isEmpty()) {

            } else {
                String photoValue = restaurant.photos.get(0);
                String photoUrl = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=%s", photoValue, getString(R.string.google_server_api_key));
                MyApplication.mImageLoader.displayImage(photoUrl, ivImage, new ImageLoadingListener() {
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
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    private void loadData() {
        mStarters = new ArrayList<>();
        mDrinks = new ArrayList<>();
        mDishes = new ArrayList<>();
        mDesserts = new ArrayList<>();

        progressDialog = ProgressDialog.show(this, "Loading datas...", "");
        FirebaseService.shared.getFoods(new ResultListener() {
            @Override
            public void onResult(boolean isSuccess, String error, List data) {

                if (isSuccess) {
                    for (int i = 0; i < data.size(); i ++) {
                        Food food = (Food) data.get(i);
                        if (food.type.equals(Constants.TYPE_STARTERS)) {
                            mStarters.add(food);
                        } else if (food.type.equals(Constants.TYPE_DRINKS)) {
                            mDrinks.add(food);
                        } else if (food.type.equals(Constants.TYPE_DISHES)){
                            mDishes.add(food);
                        } else {
                            mDesserts.add(food);
                        }
                    }

                    FoodAdapter startersAdapter = new FoodAdapter(RestaurantActivity.this, mStarters);
                    mRecyclerViewStarters.setAdapter(startersAdapter);

                    FoodAdapter drinksAdapter = new FoodAdapter(RestaurantActivity.this, mDrinks);
                    mRecyclerViewDrinks.setAdapter(drinksAdapter);

                    FoodAdapter dishesAdapter = new FoodAdapter(RestaurantActivity.this, mDishes);
                    mRecyclerViewDishes.setAdapter(dishesAdapter);

                    FoodAdapter dessertsAdapter = new FoodAdapter(RestaurantActivity.this, mDesserts);
                    mRecyclerViewDesserts.setAdapter(dessertsAdapter);

                }

                progressDialog.dismiss();
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_info:

                break;
        }
    }
}
