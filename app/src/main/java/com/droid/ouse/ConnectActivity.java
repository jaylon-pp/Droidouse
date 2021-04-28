package com.droid.ouse;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.droid.ouse.utils.LogUtils;
import com.droid.ouse.widgets.SimpleBDView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectActivity extends AppCompatActivity {

    private BluetoothDevice bluetoothDevice;
//    private TextView deviceName;
//    private TextView deviceMac;
//    private TextView deviceState;

    private Button btnBond;
    private Button btnUnbound;
    private Button btnConnect;
    private Button btnDisconnect;

    private SimpleBDView simpleBDView;
    private ListView deviceProps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getExtras();
        bluetoothDevice = (BluetoothDevice) bundle.get("bluetoothDevice");

//        deviceName = findViewById(R.id.device_name);
//        deviceMac = findViewById(R.id.device_mac);
//        deviceState = findViewById(R.id.device_state);
//        deviceState.setTextColor(Color.RED);
//
        simpleBDView = findViewById(R.id.simple_bt_view);
        simpleBDView.setBluetoothDevice(bluetoothDevice);
//
//        if(null != bluetoothDevice) {
//            deviceName.setText(bluetoothDevice.getName());
//            deviceMac.setText(bluetoothDevice.getAddress());
//            switch (bluetoothDevice.getBondState()) {
//                case BluetoothDevice.BOND_BONDED:
//                    deviceState.setText("Bonded");
//                    break;
//                case BluetoothDevice.BOND_BONDING:
//                    deviceState.setText("Bonding");
//                    break;
//                case BluetoothDevice.BOND_NONE:
//                    deviceState.setText("None");
//                    break;
//                default:
//                    deviceState.setText("Unknown");
//            }
//        }
//        deviceName = findViewById(R.id.device_name);
//        deviceMac = findViewById(R.id.device_mac);
//        btnBond = findViewById(R.id.btn_bond);
//        btnUnbond = findViewById(R.id.btn_unbond);
//
//        deviceName.setText(device.getName());
//        deviceMac.setText(device.getAddress());

        List<Pair<String, String>> datas = new ArrayList<>();
        try {
            datas.add(new Pair<>("name", bluetoothDevice.getName()));
            datas.add(new Pair<>("mac", bluetoothDevice.getAddress()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                datas.add(new Pair<>("type", String.valueOf(BlueToothUtils.getTypeName(bluetoothDevice.getType()))));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                datas.add(new Pair<>("alias", bluetoothDevice.getAlias()));
            }
            datas.add(new Pair<>("uuidsWithSdp", String.valueOf(bluetoothDevice.fetchUuidsWithSdp())));
            datas.add(new Pair<>("btClass", bluetoothDevice.getBluetoothClass().toString()));
            datas.add(new Pair<>("bondState", String.valueOf(bluetoothDevice.getBondState())));
            datas.add(new Pair<>("uuid", bluetoothDevice.getUuids().toString()));
            datas.add(new Pair<>("describeContents", String.valueOf(bluetoothDevice.describeContents())));
        } catch (Exception e) {
            LogUtils.e("", e);
        }

        deviceProps = findViewById(R.id.device_props);
        DevicePropAdapter devicePropAdapter = new DevicePropAdapter(this, R.layout.layout_device_item, datas);
        deviceProps.setAdapter(devicePropAdapter);

        btnBond = findViewById(R.id.btn_bond);
        btnBond.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(!bluetoothDevice.createBond()) {
                    ToastUtils.showShort("bond failed");
                }
            }
        });

        btnUnbound = findViewById(R.id.btn_unbound);
        btnUnbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class btDeviceCls = BluetoothDevice.class;
                Method removeBond = null;
                try {
                    removeBond = btDeviceCls.getMethod("removeBond");
                    removeBond.setAccessible(true);
                    removeBond.invoke(bluetoothDevice);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        btnConnect = findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// Establish connection to the proxy.
                if(bluetoothDevice.getBluetoothClass().getMajorDeviceClass() != BluetoothClass.Device.Major.AUDIO_VIDEO) {
                    ToastUtils.showShort("it is not HEADSET");
                    return;
                }

                boolean result = bluetoothAdapter.getProfileProxy(ConnectActivity.this, profileListener, BluetoothProfile.A2DP);
                if(result) {
                    ToastUtils.showShort("getProfileProxy successfully");
                } else {
                    ToastUtils.showShort("getProfileProxy failed");
                }
            }
        });

        btnDisconnect = findViewById(R.id.btn_disconnect);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                // ... call functions on bluetoothHeadset

                // Close proxy connection after use.
                bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset);
            }
        });
    }

    BluetoothHeadset bluetoothHeadset;

    // Get the default adapter
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private BluetoothProfile.ServiceListener profileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            LogUtils.d("onServiceConnected profile: %d proxy.hashCode: %d ", profile, proxy.hashCode());
//            if (profile == BluetoothProfile.HEADSET) {
            bluetoothHeadset = (BluetoothHeadset) proxy;
            Class btHeadsetCls = BluetoothA2dp.class;
            try {
                Method connect = btHeadsetCls.getMethod("connect", BluetoothDevice.class);
                connect.setAccessible(true);
                connect.invoke(bluetoothHeadset, bluetoothDevice);
                ToastUtils.showShort("connect done.");
            } catch (Exception e) {
                LogUtils.e(e.toString(), e);
            }
//            }
        }
        public void onServiceDisconnected(int profile) {
            LogUtils.d("onServiceDisconnected: %d", profile);
//            if (profile == BluetoothProfile.HEADSET) {
            bluetoothHeadset = null;
//            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT);
        intent.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        intent.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothDevice.ACTION_UUID);
        registerReceiver(connectBroadcastReceiver, intent);
    }

    private BroadcastReceiver connectBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("onReceive: %s", intent.getAction());
            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                if(state == BluetoothDevice.BOND_BONDED) {

//                    try {
//                        mBluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//                        mBluetoothSocket.connect();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
    };

    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mBluetoothSocket;


    class DevicePropAdapter extends ArrayAdapter<Pair<String, String>> {

        private List<Pair<String, String>> items;


        public DevicePropAdapter(@NonNull Context context, int resource, @NonNull List<Pair<String, String>> objects) {
            super(context, resource, objects);
            items = objects;
            if(null == items) {
                items = new ArrayList<>();
            }
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