package edu.ewubd.lost_it;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StationCustomAdapter extends ArrayAdapter<PoliceInfoHelperClass> {

    private Activity context;
    private List<PoliceInfoHelperClass> stationList;


    public StationCustomAdapter(Activity context, List<PoliceInfoHelperClass> stationList) {
        super(context, R.layout.layout_police_info_details_row, stationList);
        this.context = context;
        this.stationList = stationList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_police_info_details_row, null, true);

        PoliceInfoHelperClass stations = stationList.get(position);

        TextView t1 = view.findViewById(R.id.pStationNameView);
        TextView t2 = view.findViewById(R.id.pStationLocationView);
        TextView t3 = view.findViewById(R.id.pStationNumberView);


        t1.setText(stations.getStationName());
        t2.setText(stations.getStationLocation());
        t3.setText(stations.getStationNumber());

        return view;
    }
}
