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
import com.example.android_mobile_app.domain.Wearable;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends ArrayAdapter<Wearable> {

    private Context context;
    private List<Wearable> wearableList = new ArrayList<>();

    public DeviceAdapter(@NonNull Context context, ArrayList<Wearable> list) {
        super(context, 0, list);
        this.context = context;
        this.wearableList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        Wearable wearable = wearableList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.textView_name);
        name.setText(wearable.getName());

        TextView address = (TextView) listItem.findViewById(R.id.textView_mac);
        address.setText("MAC-adres: " + wearable.getAddress());

        return listItem;
    }
}
