/**
 * All meter list fragment, belong to the meter monitor activity
 * Contain a listview show all the meter
 */

package com.siren.finalprojectclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import MyAdapter.AllMeterAdapter;
import ParkingMeterSource.ParkingMeter.Display;
import ParkingMeterSource.ParkingMeter.ParkingMeter;
import ParkingMeterSource.ParkingMeter.ParkingMeterManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeterListALLFragment extends Fragment {
    ArrayList<ParkingMeter> parkingMeters;
    AllMeterAdapter adapter;

    /**
     * Set up the handler for updating UI thread
     */
    private Handler myHandler = new Handler() {

        public void handleMessage (Message message) {
            switch (message.what) {
                case 1:
                    // get the new meter list for the listview
                    parkingMeters = ParkingMeterManager.getInstance().getArrayList();
                    adapter.clear();
                    adapter.addAll(parkingMeters);
                    break;
            }
        }
    };

    public MeterListALLFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Register the handler
        Display.registerhandler(myHandler);

        parkingMeters = ParkingMeterManager.getInstance().getArrayList();

        // load the fragment view
        View view =  inflater.inflate(R.layout.fragment_meter_list_all, container, false);

        /**
         * get the list view and set a adapter
          */
        // get the listview
        ListView listView = (ListView) view.findViewById(R.id.all_list_view);
        // set a adapter to the listview
        adapter = new AllMeterAdapter(getActivity(), R.layout.meter_list_card_view, parkingMeters);
        listView.setAdapter(adapter);

        // set the a on click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int meterID = adapter.getItem(position).getID();
                Intent intent = new Intent(getActivity(), PaymentAccountActivity.class);
                intent.putExtra("id", meterID);
                startActivity(intent);
            }
        });

        return view;
    }


}
