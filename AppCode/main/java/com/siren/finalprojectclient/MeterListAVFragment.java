/**
 * available meter list fragment, belong to the meter monitor activity
 * Contain a listview show all available the meter
 */

package com.siren.finalprojectclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import MyAdapter.AllMeterAdapter;
import ParkingMeterSource.ParkingMeter.Display;
import ParkingMeterSource.ParkingMeter.ParkingMeter;
import ParkingMeterSource.ParkingMeter.ParkingMeterManager;


public class MeterListAVFragment extends Fragment {
    ArrayList<ParkingMeter> parkingMeters;
    AllMeterAdapter adapter;

    /**
     * set up the handler for updating the UI thread
     */
    private Handler myHandler = new Handler() {

        public void handleMessage (Message message) {
            switch (message.what) {
                case 1:
                    // get the new available meter list
                    parkingMeters = ParkingMeterManager.getInstance().getAvailableArrayList();
                    adapter.clear();
                    adapter.addAll(parkingMeters);
                    break;
            }
        }
    };

    public MeterListAVFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // register the handler
        Display.registerhandler(myHandler);

        // get the available meter list
        parkingMeters = ParkingMeterManager.getInstance().getAvailableArrayList();

        // load the view for available meter list
        View view =  inflater.inflate(R.layout.fragment_meter_list_av, container, false);

        // get the listview
        ListView listView = (ListView) view.findViewById(R.id.av_list_view);
        // set up the new listview
        adapter = new AllMeterAdapter(getActivity(), R.layout.meter_list_card_view, parkingMeters);
        listView.setAdapter(adapter);


        return view;
    }
}
