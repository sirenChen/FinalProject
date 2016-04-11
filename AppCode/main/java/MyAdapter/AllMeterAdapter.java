package MyAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.siren.finalprojectclient.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ParkingMeterSource.ParkingMeter.ParkingMeter;

/**
 * the all meter adapter for the all meter listview
 */
public class AllMeterAdapter extends ArrayAdapter<ParkingMeter> {
    int resource;

    public AllMeterAdapter(Context context, int resource, List<ParkingMeter> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParkingMeter meter = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resource, null);

        // get all the element from the listview item
        TextView meterID = (TextView) view.findViewById(R.id.card_view_park_id);
        TextView occupy = (TextView) view.findViewById(R.id.card_view_occupy);
        TextView authorized = (TextView) view.findViewById(R.id.card_view_authorized);
        TextView time = (TextView) view.findViewById(R.id.card_view_time);

        /**
         * determine what information will display on the each element of the listview
         */
        meterID.setText("Parking Meter ID: " + meter.getID());

        // meter status: available for parking
        if (meter.getOccupy() == 'f') {
            meterID.getBackground().setColorFilter(Color.parseColor("#81C784"), PorterDuff.Mode.LIGHTEN);
            occupy.setText("Available For Parking");
            authorized.setVisibility(View.INVISIBLE);
            time.setVisibility(View.INVISIBLE);

        }
        // meter status: authorized
        else if (meter.getOccupy() == 't' && meter.getValid() == 't') {
            Date date = new Date(meter.getStartTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String timeS = simpleDateFormat.format(date);

            meterID.getBackground().setColorFilter(Color.parseColor("#FFF176"), PorterDuff.Mode.LIGHTEN);
            occupy.setText("Not Available For Parking");
            authorized.setText("Authorized");
            time.setText("" + timeS);

            authorized.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
        }
        // meter status: not authorized
        else if (meter.getOccupy() == 't' && meter.getValid() == 'f') {
            meterID.getBackground().setColorFilter(Color.parseColor("#FF8A65"), PorterDuff.Mode.LIGHTEN);
            occupy.setText("Not Available For Parking");
            authorized.setText("Not Authorized");

            authorized.setVisibility(View.VISIBLE);
            time.setVisibility(View.INVISIBLE);
        }
        // meter status: error
        else {
            meterID.setText("ERROR !!!");
        }
        return view;
    }


}
