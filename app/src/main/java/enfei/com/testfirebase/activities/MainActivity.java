package enfei.com.testfirebase.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import enfei.com.testfirebase.R;
import enfei.com.testfirebase.adapters.ViewPagerAdapter;
import enfei.com.testfirebase.fragments.PlaceFragment;
import enfei.com.testfirebase.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        initViewPager();

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        initTabItems();
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PlaceFragment());
        adapter.addFragment(new ProfileFragment());
        mViewPager.setAdapter(adapter);
    }

    private void initTabItems() {
        mTabLayout.getTabAt(0).setCustomView(R.layout.tab_place);
        mTabLayout.getTabAt(1).setCustomView(R.layout.tab_profile);
    }


    DatabaseReference mRestaurantRef;
    private void test() {
        mRestaurantRef = FirebaseDatabase.getInstance().getReference("restaurants");
    }

}
