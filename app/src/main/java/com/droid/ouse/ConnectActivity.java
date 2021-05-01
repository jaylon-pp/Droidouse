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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class ConnectActivity extends AppCompatActivity {

    private BluetoothDevice bluetoothDevice;

    private Button btnBond;
    private Button btnUnbound;
    private Button btnConnect;
    private Button btnDisconnect;

    private SimpleBDView simpleBDView;
    private ListView deviceProps;
    Set<PropValue> devicePropDatas = Collections.synchronizedSet(new TreeSet<>());
    DevicePropAdapter devicePropAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getExtras();
        bluetoothDevice = (BluetoothDevice) bundle.get("bluetoothDevice");
        simpleBDView = findViewById(R.id.simple_bt_view);
        simpleBDView.setBluetoothDevice(bluetoothDevice);
        
        try {
            devicePropDatas.add(new PropValue("name", bluetoothDevice.getName()));
            devicePropDatas.add(new PropValue("mac", bluetoothDevice.getAddress()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                devicePropDatas.add(new PropValue("type", String.valueOf(BlueToothUtils.getTypeName(bluetoothDevice.getType()))));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                devicePropDatas.add(new PropValue("alias", bluetoothDevice.getAlias()));
            }
            devicePropDatas.add(new PropValue("uuidsWithSdp", String.valueOf(bluetoothDevice.fetchUuidsWithSdp())));
            if(null != bluetoothDevice.getBluetoothClass()) {
                devicePropDatas.add(new PropValue("btClass", "0x" + bluetoothDevice.getBluetoothClass().toString()));
                BluetoothClass bluetoothClass = bluetoothDevice.getBluetoothClass();
                devicePropDatas.add(new PropValue("majorClass", "0x" + Integer.toHexString(bluetoothClass.getMajorDeviceClass())));
                devicePropDatas.add(new PropValue("deviceClass", "0x" + Integer.toHexString(bluetoothClass.getDeviceClass())));
            }
            devicePropDatas.add(new PropValue("bondState", BlueToothUtils.getBondState(bluetoothDevice.getBondState())));
            devicePropDatas.add(new PropValue("describeContents", String.valueOf(bluetoothDevice.describeContents())));
        } catch (Exception e) {
            LogUtils.e("", e);
        }

        deviceProps = findViewById(R.id.device_props);
        devicePropAdapter = new DevicePropAdapter(this, R.layout.layout_device_item, new ArrayList<>(devicePropDatas));
        deviceProps.setAdapter(devicePropAdapter);

        btnBond = findViewById(R.id.btn_bond);
        btnBond.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                boolean result = bluetoothDevice.fetchUuidsWithSdp();
                if(!result) {
                    ToastUtils.showShort("fetchUuidsWithSdp failed");
                }
//                if(!bluetoothDevice.createBond()) {
//                    ToastUtils.showShort("bond failed");
//                }
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
            } else if(BluetoothDevice.ACTION_UUID.equals(intent.getAction())) {
                if(bluetoothDevice.getUuids() != null) {
                    LogUtils.d("uuid length: %d", bluetoothDevice.getUuids().length);
                    if(bluetoothDevice.getUuids() != null) {
                        for(int i = 0; i < bluetoothDevice.getUuids().length; i++) {
                            PropValue propValue = new PropValue(String.format("uuid:%d", i), bluetoothDevice.getUuids()[i].getUuid().toString());
                            boolean re = devicePropDatas.add(propValue);
                            LogUtils.d("re: %b", re);
                        }
                    }
                    devicePropAdapter.setItems(new ArrayList<>(devicePropDatas));
                } else {
                    LogUtils.d("uuid is null");
                }
            }
        }
    };

    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mBluetoothSocket;


    class DevicePropAdapter extends ArrayAdapter<PropValue> {

        private List<PropValue> items;


        public DevicePropAdapter(@NonNull Context context, int resource, @NonNull List<PropValue> objects) {
            super(context, resource, objects);
            items = objects;
            if(null == items) {
                items = new ArrayList<>();
            }
        }

        public void setItems(List<PropValue> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public void add(@Nullable PropValue object) {
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
            key.setText(items.get(position).key);
            value.setText(items.get(position).value);
            return view;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Nullable
        @Override
        public PropValue getItem(int position) {
//            return super.getItem(position);
            return items.get(position);
        }
    }

    class PropValue implements Comparable<PropValue>{
        long timestamp;
        String key;
        String value;

        public PropValue(String key, String value) {
            this.key = key;
            this.value = value;
            timestamp = System.currentTimeMillis();
        }

        @Override
        public int compareTo(PropValue o) {
            int re = key.compareTo(o.key);
            if(re == 0) {
                re = value.compareTo(o.value);
            }
            return re;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("*%s-%s*", key, value);
        }
    }
}