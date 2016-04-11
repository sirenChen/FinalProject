/**
 * Create By Siren and XueRong
 *
 * This is the main activity,
 * Display welcome page, init the System
 * Start the networking and message processing
 */

package com.siren.finalprojectclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import ParkingMeterSource.MessagePool.DoProcess;
import ParkingMeterSource.NetWork.NetWorking;
import ParkingMeterSource.ParkingMeter.ParkingMeterManager;

public class MainActivity extends AppCompatActivity {
    //Init the thread pool for the networking and message process thread
    private static ExecutorService netWorkingThread = Executors.newFixedThreadPool(2);
    private static ExecutorService messageThread = Executors.newFixedThreadPool(2);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cancel the status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create  the meter instance
        ParkingMeterManager.getInstance().create();

        // start two thread
        try {
            netWorkingThread.submit(new NetWorking());
            messageThread.submit(new DoProcess());

        } catch (IOException e) {
            e.printStackTrace();
        }

        // wait for 6s and then jump to meter monitor
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MeterMonitorActivity.class);
                startActivity(intent);
                finish();
            }
        };

        // start timer
        timer.schedule(task, 1000 * 6);
    }
}
