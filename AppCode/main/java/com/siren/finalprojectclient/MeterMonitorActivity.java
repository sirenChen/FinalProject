/**
 * meter monitor, have three tab
 */
package com.siren.finalprojectclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import MyAdapter.ViewPageAdapter;
import ParkingMeterSource.AllHandler.HandlerWrite;

public class MeterMonitorActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPageAdapter viewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_monitor);

        //set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // load the view
        tabLayout = (TabLayout) findViewById(R.id.meter_tab_layout);
        // view page for the tab
        viewPager = (ViewPager) findViewById(R.id.meter_view_pager);

        // set up the adapter for the tab view
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragments(new MeterListALLFragment(), "All Meters");
        viewPageAdapter.addFragments(new MeterListUNFragment(), "Unauthorized Meters");
        viewPageAdapter.addFragments(new MeterListAVFragment(), "Available   Meters");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        sendInit();
    }

    public void sendInit () {
        new HandlerWrite().send("cc");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.check_time) {
            Intent intent = new Intent(getApplicationContext(), TimeCheckActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}


