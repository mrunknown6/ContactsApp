package com.example.contactsapp.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.contactsapp.BaseActivity;
import com.example.contactsapp.R;
import com.example.contactsapp.ui.dashboard.adapters.ViewPagerAdapter;
import com.example.contactsapp.ui.dashboard.fragments.ContactsListFragment;
import com.example.contactsapp.ui.dashboard.fragments.ContactsRequestsFragment;
import com.example.contactsapp.ui.dashboard.fragments.SendRequestFragment;
import com.example.contactsapp.ui.dashboard.viewmodel.DashboardViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class DashboardActivity extends BaseActivity {

    public static int REQUEST_CODE = 1001;

    BottomNavigationView bnvDashboard;
    ViewPager vpDashboardContainer;
    ViewPagerAdapter pagerAdapter;

    public static ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setupToolbar();
        checkPermission();
        setupSession();
        setupBottomNavigation();
        addFragments();
    }

    private void addFragments() {
        fragments.add(new ContactsListFragment());
        fragments.add(new ContactsRequestsFragment());
        fragments.add(new SendRequestFragment());
    }

    private void setupViewPager() {
        vpDashboardContainer = findViewById(R.id.vpDashboardContainer);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpDashboardContainer.setAdapter(pagerAdapter);
    }

    private void setupBottomNavigation() {
        bnvDashboard = findViewById(R.id.bnvDashboard);
        bnvDashboard.setOnNavigationItemSelectedListener(item -> {

            if (vpDashboardContainer != null) {
                if (item.getItemId() == R.id.iContacts)
                    vpDashboardContainer.setCurrentItem(0, true);
                else if (item.getItemId() == R.id.iRequests)
                    vpDashboardContainer.setCurrentItem(1, true);
                if (item.getItemId() == R.id.iSend)
                    vpDashboardContainer.setCurrentItem(2, true);
            }

            return true;
        });
    }

    private void setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Dashboard");
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
        } else {
            setupViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                setupViewPager();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}