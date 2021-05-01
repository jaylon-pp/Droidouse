package com.droid.ouse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.droid.ouse.utils.LogUtils;
import com.droid.ouse.widgets.SimpleBDView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ConnectBleActivity extends AppCompatActivity {

    private BluetoothDevice bluetoothDevice;

    private Button btnBond;
    private Button btnUnbound;
    private Button btnConnect;
    private Button btnDisconnect;

    private SimpleBDView simpleBDView;
    private ListView deviceProps;
    List<Pair<String, String>> devicePropDatas = new ArrayList<>();
    DevicePropAdapter devicePropAdapter;

    BluetoothGatt bluetoothGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_ble);

        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getExtras();
        bluetoothDevice = (BluetoothDevice) bundle.get("bluetoothDevice");
        simpleBDView = findViewById(R.id.simple_bt_view);
        simpleBDView.setBluetoothDevice(bluetoothDevice);

        try {
            devicePropDatas.add(new Pair("name", bluetoothDevice.getName()));
            devicePropDatas.add(new Pair("mac", bluetoothDevice.getAddress()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                devicePropDatas.add(new Pair("type", String.valueOf(BlueToothUtils.getTypeName(bluetoothDevice.getType()))));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                devicePropDatas.add(new Pair("alias", bluetoothDevice.getAlias()));
            }
            devicePropDatas.add(new Pair("uuidsWithSdp", String.valueOf(bluetoothDevice.fetchUuidsWithSdp())));
            if(null != bluetoothDevice.getBluetoothClass()) {
                devicePropDatas.add(new Pair("btClass", "0x" + bluetoothDevice.getBluetoothClass().toString()));
                BluetoothClass bluetoothClass = bluetoothDevice.getBluetoothClass();
                devicePropDatas.add(new Pair("majorClass", "0x" + Integer.toHexString(bluetoothClass.getMajorDeviceClass())));
                devicePropDatas.add(new Pair("deviceClass", "0x" + Integer.toHexString(bluetoothClass.getDeviceClass())));
            }
            devicePropDatas.add(new Pair("bondState", BlueToothUtils.getBondState(bluetoothDevice.getBondState())));
            devicePropDatas.add(new Pair("describeContents", String.valueOf(bluetoothDevice.describeContents())));
        } catch (Exception e) {
            LogUtils.e("", e);
        }

        deviceProps = findViewById(R.id.device_props);
        devicePropAdapter = new DevicePropAdapter(this, R.layout.layout_device_item, devicePropDatas);
        deviceProps.setAdapter(devicePropAdapter);

        btnConnect = findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    bluetoothGatt = bluetoothDevice.connectGatt(ConnectBleActivity.this, false, bluetoothGattCallback);
                }
            }
        });

        btnDisconnect = findViewById(R.id.btn_disconnect);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
            }
        });
    }

    private MyBluetoothGattCallback bluetoothGattCallback = new MyBluetoothGattCallback();

    private class DevicePropAdapter extends ArrayAdapter<Pair<String, String>> {

        private List<Pair<String, String>> items;


        public DevicePropAdapter(@NonNull Context context, int resource, @NonNull List<Pair<String, String>> objects) {
            super(context, resource, objects);
            items = objects;
            if(null == items) {
                items = new ArrayList<>();
            }
        }

        public void setItems(List<Pair<String, String>> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public void add(@Nullable Pair<String, String> object) {
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
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_key, parent, false);
            TextView key  = view.findViewById(R.id.tv_key);
            TextView value = view.findViewById(R.id.tv_value);
            key.setText(items.get(position).first);
            value.setText(items.get(position).second);
            return view;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Nullable
        @Override
        public Pair<String, String> getItem(int position) {
//            return super.getItem(position);
            return items.get(position);
        }
    }
}