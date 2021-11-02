package com.example.android_mobile_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android_mobile_app.R;
import com.example.android_mobile_app.domain.Measurement;

import java.util.ArrayList;
import java.util.List;

public class MeasurementAdapter extends ArrayAdapter<Measurement> {
    private Context context;
    private List<Measurement> measurements = new ArrayList<>();

    public MeasurementAdapter(@NonNull Context context, List<Measurement> list) {
        super(context, 0, list);
        this.context = context;
        this.measurements = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        Measurement measurement = measurements.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.textView_name);
        name.setText(measurement.getName());

        TextView address = (TextView) listItem.findViewById(R.id.textView_mac);
        address.setText(measurement.getID());

        return listItem;
    }
}
