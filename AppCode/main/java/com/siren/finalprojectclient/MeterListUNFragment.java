/**
 * Unauthorized meter list fragment, belong to the meter monitor activity
 * Contain a listview show unauthorized meter
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


/**
 * A simple {@link Fragment} subclass.
 */
public class MeterListUNFragment extends Fragment {
    ArrayList<ParkingMeter> parkingMeters;
    AllMeterAdapter adapter;


    /**
     * Set up the handler for updating the UI thread
     */
    private Handler myHandler = new Handler() {

        public void handleMessage (Message message) {
            switch (message.what) {
                case 1:
                    // get the not authorized meter list
                    parkingMeters = ParkingMeterManager.getInstance().getNotAuthArrayList();
                    adapter.clear();
                    adapter.addAll(parkingMeters);

                    break;
            }
        }
    };

    public MeterListUNFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // register the handler for display
        Display.registerhandler(myHandler);

        // get the list
        parkingMeters = ParkingMeterManager.getInstance().getNotAuthArrayList();

        // load the view
        View view =  inflater.inflate(R.layout.fragment_meter_list_un, container, false);

        // set up the listview
        ListView listView = (ListView) view.findViewById(R.id.un_list_view);
        adapter = new AllMeterAdapter(getActivity(), R.layout.meter_list_card_view, parkingMeters);
        listView.setAdapter(adapter);

        return view;
    }

}
