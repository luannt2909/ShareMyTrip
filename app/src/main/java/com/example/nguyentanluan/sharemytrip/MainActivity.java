package com.example.nguyentanluan.sharemytrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity implements Fragment_Drawer.FragmentDrawerListener {

    private Toolbar toolbar;
    private Fragment_Drawer fragment_drawer;
    private Fragment frag_home,frag_friend,frag_setuptrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        fragment_drawer=(Fragment_Drawer)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        setSupportActionBar(toolbar);
        fragment_drawer.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        fragment_drawer.setDrawerListener(this);
        frag_home=new Fragment_Home();
        frag_friend=new Fragment_Friends();
        frag_setuptrip=new Fragment_SetupTrip();
        displayView(0);
        //initView();
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }
    public  void displayView(int position){
        Fragment fragment=null;
        switch (position){
            case 0:
                fragment=frag_home;
                break;
            case 1:
                fragment=frag_friend;
                break;
            case 2:
                break;
            case 3:
                fragment=frag_setuptrip;
                break;
        }
        if(fragment!=null){
            FragmentManager manager=getSupportFragmentManager();
            FragmentTransaction transaction=manager.beginTransaction();
            transaction.replace(R.id.container_body,fragment);
            transaction.commit();
        }
    }
    public void showFragment( int resId, Fragment fragment, String tag, String lastTag, boolean addToBackStack ) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if ( lastTag != null ) {
            Fragment lastFragment = fragmentManager.findFragmentByTag( lastTag );
            if ( lastFragment != null ) {
                transaction.hide( lastFragment );
            }
        }

        if ( fragment.isAdded() ) {
            transaction.show( fragment );
        }
        else {
            transaction.add( resId, fragment, tag ).setBreadCrumbShortTitle( tag );
        }

        if ( addToBackStack ) {
            transaction.addToBackStack( tag );
        }

        transaction.commit();
    }
}
