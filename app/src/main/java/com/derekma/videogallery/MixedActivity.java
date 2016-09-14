package com.derekma.videogallery;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.derekma.videogallery.dummy.DummyContent;
import com.derekma.videogallery.gcm.QuickstartPreferences;
import com.derekma.videogallery.gcm.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

/**
 * This class named MixedActivity extends FragmentActivity.
 * It also implement OnHeadlineSelectedListener interface.
 * This class plays a role like container. Manage fragments of
 * the whole application. Each fragment communicate not directly but
 * via this class.
 */
public class MixedActivity extends FragmentActivity implements VideosFragment.OnHeadlineSelectedListener, View.OnClickListener,NewsFragment.OnFragmentInteractionListener,VideosFragment.OnFragmentInteractionListener, WishListFragment.OnFragmentInteractionListener, RecommendFragment.OnListFragmentInteractionListener {

    /**
     * viewPager is an object to control the fragments.
     * It implements swapping between each fragments.
     */
    private ViewPager viewPager;

    /**
     * fragmentPagerAdapter is an adapter to supervise the data in view.
     */
    private FragmentPagerAdapter fragmentPagerAdapter;

    /**
     * A list to store all fragments.
     */
    private List<Fragment> fragmentList;

    /**
     *
     */
    public static ProgressDialog mDialog;

    public static int uId;

    /** Integrate GCM in this activity */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        uId = i.getIntExtra("uId", 1);

        setContentView(R.layout.activity_mixed);

        initView();

        setSelect(0);

        initNotification();
    }

    private void initNotification(){

        //mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                }

            }
        };
        //mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void initView(){
        viewPager = (ViewPager)findViewById(R.id.id_viewpager);

        viewPager.setOffscreenPageLimit(4);

        fragmentList = new ArrayList<Fragment>();
        Fragment videosFragment = new VideosFragment();
        Fragment newsFragment = new NewsFragment();
        Fragment wishListFragment = new WishListFragment();
        Fragment recommendFragment = new RecommendFragment();

        fragmentList.add(videosFragment);
        fragmentList.add(recommendFragment);
        fragmentList.add(newsFragment);
        fragmentList.add(wishListFragment);

        final ArrayList<String> titleList = new ArrayList<>();
        titleList.add("Videos");
        titleList.add("Recommend");
        titleList.add("News");
        titleList.add("Favorites");

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.PagerTab);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);

        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.DKGRAY);
        pagerTabStrip.setBackgroundColor(Color.CYAN);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentItem = viewPager.getCurrentItem();
                //Toast.makeText(getApplicationContext(), String.valueOf(currentItem), Toast.LENGTH_LONG).show();
                setSelect(currentItem);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {

    }

    private void setSelect(int i)
    {
        viewPager.setCurrentItem(i);
    }

    @Override
    public void onFavoriteSelected(int position, long uid, String vid) {

        //WishListFragment favoriteFrag = (WishListFragment) getSupportFragmentManager().findFragmentById(R.id.wish_list_fragment);

        WishListFragment fragment = (WishListFragment) getSupportFragmentManager().findFragmentByTag(
                        "android:switcher:" + R.id.id_viewpager + ":3");

        if(fragment != null){

            fragment.updateView();
            fragment.adapter.notifyDataSetChanged();

        }else {
            // Otherwise, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
            WishListFragment newFragment = new WishListFragment();

            Bundle args = new Bundle();
            args.putInt(WishListFragment.param, position);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.wish_list_fragment, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

        }

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
