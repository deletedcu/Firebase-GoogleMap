package enfei.com.testfirebase.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import enfei.com.testfirebase.MyApplication;
import enfei.com.testfirebase.R;
import enfei.com.testfirebase.activities.RestaurantActivity;
import enfei.com.testfirebase.adapters.RestaurantAdapter;
import enfei.com.testfirebase.adapters.ViewPagerAdapter;
import enfei.com.testfirebase.models.Restaurant;
import enfei.com.testfirebase.services.FirebaseService;
import enfei.com.testfirebase.services.ObjectResultListener;
import enfei.com.testfirebase.services.ResultListener;
import enfei.com.testfirebase.utils.PermissionUtils;
import enfei.com.testfirebase.utils.PixelUtils;

import static android.app.Activity.RESULT_CANCELED;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by king on 17/08/2017.
 */

public class PlaceFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1001;
    private final int PERMISSIONS_REQUEST_FINE_LOCATION = 2001;

    private View view;
    private ScrollView mScrollView;
    private ImageView btnFilter;
    private Button btnSearch;
    private Button btnToggle;
    private ListView listView;
    private Button btnEnter;
    private ViewPager mTopViewPager;
    private ViewPager mBottomViewPager;
    private LinearLayout mTopIndicator;
    private LinearLayout mBottomIndicator;
    private ProgressBar progressBar;
    private TextView tvAddress;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private FrameLayout layoutTop;
    private FrameLayout layoutBottom;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private boolean isShowMap = true;

    private List<Restaurant> mRestaurants = new ArrayList<>();
    private Restaurant selectedRestaurant;
    private Drawable mDotEnabled;
    private Drawable mDotDisabled;
    private AsyncHttpClient httpClient;

    private ViewPagerAdapter mTopAdapter;
    private ViewPagerAdapter mBottomAdapter;
    private RestaurantAdapter mListAdapter;

    private WorkaroundMapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpClient = new AsyncHttpClient();
        if (MyApplication.getGoogleApiHelper().isConnected())
            mGoogleApiClient = MyApplication.getGoogleApiHelper().getGoogleApiClient();

    }

    public View onCreateView(final LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_place, container, false);

        initView();
        mTopAdapter = new ViewPagerAdapter(getFragmentManager());
        mBottomAdapter = new ViewPagerAdapter(getFragmentManager());
        mListAdapter = new RestaurantAdapter(getContext(), 0);
        listView.setAdapter(mListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Restaurant item = mListAdapter.getItem(position);
                showUIs(item);
            }
        });

        loadData();
        return view;
    }

    private void initView() {

        mScrollView = (ScrollView) view.findViewById(R.id.layout_scrollView);
        layoutTop = (FrameLayout) view.findViewById(R.id.layout_top);
        layoutBottom = (FrameLayout) view.findViewById(R.id.layout_bottom);
        mTopViewPager = (ViewPager) view.findViewById(R.id.viewPager_top);
        mBottomViewPager = (ViewPager) view.findViewById(R.id.viewPager_bottom);
        mTopIndicator = (LinearLayout) view.findViewById(R.id.indicator_top);
        mBottomIndicator = (LinearLayout) view.findViewById(R.id.indicator_bottom);
        btnFilter = (ImageView) view.findViewById(R.id.iv_filter);
        btnSearch = (Button) view.findViewById(R.id.btn_search);
        btnToggle = (Button) view.findViewById(R.id.btn_toggle);
        btnEnter = (Button) view.findViewById(R.id.btn_enter);
        listView = (ListView) view.findViewById(R.id.listView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tvAddress = (TextView) view.findViewById(R.id.tv_address);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvSubTitle = (TextView) view.findViewById(R.id.tv_subTitle);

        mDotEnabled = getResources().getDrawable(R.drawable.active);
        mDotDisabled = getResources().getDrawable(R.drawable.inactive);

        btnFilter.setOnClickListener(this);
        btnToggle.setOnClickListener(this);
        btnEnter.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        mapFragment = (WorkaroundMapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_mapView);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(getContext(), "Please go to the settings and enable Location permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        mRestaurants.clear();
        mListAdapter.setList(mRestaurants);
        FirebaseService.shared.getRestaurants(new ResultListener() {
            @Override
            public void onResult(boolean isSuccess, String error, List object) {
                progressBar.setVisibility(View.GONE);
                if (object != null) {
                    for (int i = 0; i < object.size(); i++) {
                        Restaurant item = (Restaurant) object.get(i);
                        mRestaurants.add(item);
                        if (i == 0) {
                            updateUIs(item);
                        }
                    }
                    mListAdapter.setList(mRestaurants);
                    showNearbyPlaces(mRestaurants);

                }
            }
        });
    }

    private void updateUIs(Restaurant item) {

        selectedRestaurant = item;
        mTopViewPager.removeAllViews();
        mBottomViewPager.removeAllViews();
        mTopAdapter.removeAll();
        mBottomAdapter.removeAll();

        if (item != null) {
            tvAddress.setText(item.address);
            tvTitle.setText(item.name);
            tvSubTitle.setText(item.subTitle);

            if (item.photos != null && !item.photos.isEmpty()) {

                int i = 0;
                for (String photoValue : item.photos) {
                    if (i > 4)
                        break;
                    mTopAdapter.addFragment(ImageFragment.newInstance(photoValue));
                    mBottomAdapter.addFragment(ImageFragment.newInstance(photoValue));
                    i++;
                }
                setupTopViewPager(mTopAdapter);
                setupBottomViewPager(mBottomAdapter);
            }

        }

    }

    private void setupTopViewPager(ViewPagerAdapter adapter) {
        mTopViewPager.setAdapter(adapter);
        mTopViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                toggleDotsIndicator(mTopIndicator, position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        addDotsIndicator(mTopIndicator, adapter.getCount());
    }

    private void setupBottomViewPager(final ViewPagerAdapter adapter) {
        mBottomViewPager.setAdapter(adapter);
        mBottomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                toggleDotsIndicator(mBottomIndicator, position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        addDotsIndicator(mBottomIndicator, adapter.getCount());
    }

    private void addDotsIndicator(LinearLayout indicator, int count) {
        int margin = PixelUtils.dpToPx(getContext(), 3);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, margin);
        if (indicator.getChildCount() > 0)
            indicator.removeAllViews();

        for (int i = 0; i < count; i++) {
            ImageView dotView = new ImageView(getContext());
            dotView.setTag(i);
            dotView.setImageDrawable(mDotDisabled);
            dotView.setLayoutParams(layoutParams);
            indicator.addView(dotView);
        }

        if (count > 0)
            toggleDotsIndicator(indicator, 0);
    }

    private void toggleDotsIndicator(LinearLayout indicator, int position) {
        int childViewCount = indicator.getChildCount();
        for (int i = 0; i < childViewCount; i++) {
            View childView = indicator.getChildAt(i);
            if (childView instanceof ImageView) {
                ImageView dotView = (ImageView) childView;
                Object positionTag = dotView.getTag();
                if (positionTag != null && positionTag instanceof Integer) {
                    if ((int) positionTag == position && dotView.getDrawable() != mDotEnabled) {
                        dotView.setImageDrawable(mDotEnabled);
                    } else if ((int) positionTag != position && dotView.getDrawable() == mDotEnabled) {
                        dotView.setImageDrawable(mDotDisabled);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_filter:

                break;
            case R.id.btn_toggle:
                toggleUI();
                break;
            case R.id.btn_enter:
                Intent intent = new Intent(getContext(), RestaurantActivity.class);
                intent.putExtra("value", selectedRestaurant);
                startActivity(intent);
                break;
            case R.id.btn_search:
                startPlaceActivity();
                break;
        }
    }

    private void startPlaceActivity() {
        try {
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(filter)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                findNearbyRestaurants(place);
                btnSearch.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    private void findNearbyRestaurants(Place place) {
        progressBar.setVisibility(View.VISIBLE);
        mMap.clear();
        mRestaurants.clear();
        mListAdapter.setList(mRestaurants);
        String latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;

        String urlString = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&rankby=distance&types=restaurant&key=%s", latlng, getString(R.string.google_server_api_key));

        httpClient.get(getContext(), urlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    try {
                        String strResponse = new String(responseBody, "UTF-8");
                        JSONObject jsonObject = new JSONObject(strResponse);
                        mRestaurants = parseData(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                showNearbyPlaces(mRestaurants);
                mListAdapter.setList(mRestaurants);
//                if (!mRestaurants.isEmpty()) {
//                    Restaurant item = mRestaurants.get(0);
//                    showUIs(item);
//                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void showNearbyPlaces(List<Restaurant> list) {

        for (int i = 0; i < list.size(); i++) {
            Restaurant item = list.get(i);
            MarkerOptions options = new MarkerOptions();
            LatLng latLng = new LatLng(item.location.lat, item.location.lng);
            options.position(latLng);
            options.title(item.name);
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));

            Marker marker = mMap.addMarker(options);
            marker.setTag(i);
            if (i == 0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        }

    }

    private void getPlacePhotos(final Restaurant restaurant) {

        progressBar.setVisibility(View.VISIBLE);
        String placeId = restaurant.place_id;
        String urlString = String.format("https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=%s", placeId, getString(R.string.google_server_api_key));
        httpClient.get(getContext(), urlString, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.GONE);
                if (statusCode == 200) {

                    try {
                        String strResponse = new String(responseBody, "UTF-8");
                        JSONObject jsonObject = new JSONObject(strResponse);
                        JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("photos");
                        List<String> photos = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String path = jsonArray.getJSONObject(i).getString("photo_reference");
                            photos.add(path);
                        }
                        restaurant.photos = photos;

                        FirebaseService.shared.createRestaurant(restaurant, new ObjectResultListener() {
                            @Override
                            public void onResult(boolean isSuccess, String error, Object object) {

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                updateUIs(restaurant);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                updateUIs(restaurant);
            }
        });

    }

    private List<Restaurant> parseData(JSONObject jsonObject) {
        List<Restaurant> list = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            Log.d("Json parser: started", jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Restaurant item = new Restaurant(object);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            PermissionUtils.requestPermissions(getActivity(), PERMISSIONS_REQUEST_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void toggleUI() {
        if (isShowMap) {
            isShowMap = false;
            btnToggle.setText(getString(R.string.list_string));
            listView.setVisibility(View.VISIBLE);
            listView.setAlpha(0.0f);
            listView.animate()
                    .alpha(1.0f)
                    .setDuration(600)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });

        } else {
            isShowMap = true;
            btnToggle.setText(getString(R.string.map_string));
            listView.animate()
                    .alpha(0.0f)
                    .setDuration(600)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            listView.setVisibility(View.GONE);
                        }
                    });

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getTag() != null) {
            int position = (int) marker.getTag();
            Restaurant item = mRestaurants.get(position);
            showUIs(item);
        }

        return false;
    }

    private void showUIs(Restaurant item) {
        if (item.photos == null || item.photos.isEmpty()) {
            getPlacePhotos(item);
        } else {
            updateUIs(item);
        }
    }

}
