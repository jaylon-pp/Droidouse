package com.droid.ouse;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.droid.ouse.widgets.SimpleBDView;

import java.util.ArrayList;
import java.util.List;

class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private List<BluetoothDevice> items;

    public DeviceAdapter(@NonNull Context context, int resource, @NonNull List<BluetoothDevice> objects) {
        super(context, resource, objects);
        items = objects;
        if(null == items) {
            items = new ArrayList<>();
        }
    }

    @Override
    public void add(@Nullable BluetoothDevice object) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getAddress().equals(object.getAddress())) {
                return;
            }
        }
        items.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SimpleBDView view = new SimpleBDView(getContext());
        view.setBluetoothDevice(items.get(position));
        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public BluetoothDevice getItem(int position) {
//            return super.getItem(position);
        return items.get(position);
    }
}