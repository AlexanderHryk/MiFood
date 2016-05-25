package com.ahryk94gmail.mifood.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ahryk94gmail.mifood.R;
import com.ahryk94gmail.mifood.presenter.IMainPresenter;
import com.ahryk94gmail.mifood.presenter.MainPresenterImpl;

public class MainActivity extends AppCompatActivity implements IMainView, NavigationView.OnNavigationItemSelectedListener {

    private static final int BIND_DEVICE_REQUEST_CODE = 0x1000;

    private StepsFragment mStepsFragment;
    private HeartRateFragment mHeartRateFragment;
    private SleepActivityFragment mSleepActivityFragment;

    private IMainPresenter mMainPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.mMainPresenter = new MainPresenterImpl(this);
    }

    @Override
    protected void onDestroy() {
        this.mMainPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_bind_device) {
            startActivityForResult(new Intent(this, ScanActivity.class), BIND_DEVICE_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_steps) {
            this.mStepsFragment = new StepsFragment();
            ft.replace(R.id.fragment_container, this.mStepsFragment);
        } else if (id == R.id.nav_heart_rate) {
            this.mHeartRateFragment = new HeartRateFragment();
            ft.replace(R.id.fragment_container, this.mHeartRateFragment);
        } else if (id == R.id.nav_sleep) {
            this.mSleepActivityFragment = new SleepActivityFragment();
            ft.replace(R.id.fragment_container, this.mSleepActivityFragment);
        }

        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BIND_DEVICE_REQUEST_CODE) {
            this.mMainPresenter.setUserProfile(resultCode, data);
        }
    }
}
