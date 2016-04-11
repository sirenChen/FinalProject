/**
 * use to check the account balance
 */
package com.siren.finalprojectclient;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import ParkingMeterSource.AllHandler.HandlerWrite;
import ParkingMeterSource.ParkingMeter.Display;
import ParkingMeterSource.ParkingMeter.ParkingMeterManager;

public class TimeCheckActivity extends AppCompatActivity {

    private Handler myHandler = new Handler() {

        public void handleMessage (Message message) {
            switch (message.what) {
                case 2:
                    EditText editText = (EditText) findViewById(R.id.check_time_text);
                    editText.setText((CharSequence) message.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_check);

        Display.registerhandler(myHandler);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // go back to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * click submit button,
     * send the query message to the server
     * @param view
     */
    public void submit (View view) {
        EditText editText = (EditText) findViewById(R.id.check_time_text);
        String account = editText.getText().toString();

        new HandlerWrite().send("ct@" + account + "@");
    }
}
