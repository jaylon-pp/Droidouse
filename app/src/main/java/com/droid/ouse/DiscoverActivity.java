package com.droid.ouse;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.droid.ouse.utils.LogUtils;
import com.droid.ouse.widgets.SimpleBDView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscoverActivity extends AppCompatActivity {
    
    private BluetoothAdapter bluetoothAdapter;
    private Button btnScan;
    private ListView bondedDeviceList;
    private DeviceAdapter bondDeviceAdapter;

    private ListView discoveredDeviceList;
    private DeviceAdapter discoveredDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        btnScan = findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d( "isDiscovering: " + bluetoothAdapter.isDiscovering());
                if(!bluetoothAdapter.isDiscovering()) {
                    discoveredDeviceAdapter.clear();
                    bluetoothAdapter.startDiscovery();
                }
            }
        });
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bondedDeviceList = findViewById(R.id.bonded_devices);
        bondDeviceAdapter = new DeviceAdapter(DiscoverActivity.this, R.layout.layout_device_item, new ArrayList<BluetoothDevice>(bluetoothAdapter.getBondedDevices()));
        bondedDeviceList.setAdapter(bondDeviceAdapter);
        bondedDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DiscoverActivity.this, ConnectActivity.class);
                LogUtils.d( "position: " + position);
                intent.putExtra("bluetoothDevice", bondDeviceAdapter.getItem(position));
                startActivity(intent);
            }
        });

        discoveredDeviceList = findViewById(R.id.discovered_devices);
        discoveredDeviceAdapter = new DeviceAdapter(DiscoverActivity.this, R.layout.layout_device_item);
        discoveredDeviceList.setAdapter(discoveredDeviceAdapter);;
        discoveredDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DiscoverActivity.this, ConnectActivity.class);
                LogUtils.d( "position: " + position);
                intent.putExtra("bluetoothDevice", discoveredDeviceAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtils.d( "requestCode: " + requestCode);
        if(requestCode == 1245) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PackageManager pm = getPackageManager();
        if(pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 6.0) {
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        1245);
            }
        }

        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(searchDevices, intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(searchDevices);
        bluetoothAdapter.cancelDiscovery();
    }


    private BroadcastReceiver searchDevices = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d( "action: " + action);
            if (action.equals(BluetoothDevice.ACTION_FOUND)) { //found device
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str = device.getName() + "|" + device.getAddress();
                LogUtils.d( "scanned device:"+str);
                discoveredDeviceAdapter.add(device);

                LogUtils.d("getMajorDeviceClass: %d deviceClass: %d type: %d", device.getBluetoothClass().getMajorDeviceClass(), device.getBluetoothClass().getDeviceClass(), device.getType());
                LogUtils.d("deviceType: %s", BlueToothUtils.getTypeName(device.getType()));
//                List list = new ArrayList();
//                //如果List中没有str元素则返回-1
//                if (list.indexOf(str) == -1)// 防止重复添加
//                    list.add(str); // 获取设备名称和mac地址

            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
//                Toast.makeText(getBaseContext(), "正在扫描", Toast.LENGTH_SHORT).show();
                LogUtils.d( "start scan");
            } else if (action
                    .equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
//                Toast.makeText(getBaseContext(), "扫描完成，点击列表中的设备来尝试连接", Toast.LENGTH_SHORT).show();
                LogUtils.d( "scan done");
            }
        }
    };

    class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {

        private List<BluetoothDevice> items;

        public DeviceAdapter(@NonNull Context context, int resource, @NonNull List<BluetoothDevice> objects) {
            super(context, resource, objects);
            items = objects;
            if(null == items) {
                items = new ArrayList<>();
            }
        }

        public DeviceAdapter(@NonNull Context context, int resource) {
            this(context, resource, null);
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
//            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_device_item, parent, false);
//            TextView name  = view.findViewById(R.id.device_name);
//            TextView mac = view.findViewById(R.id.device_mac);
//            name.setText(items.get(position).getName());
//            mac.setText(items.get(position).getAddress());
//
//            TextView state = view.findViewById(R.id.device_state);
//            switch (items.get(position).getBondState()) {
//                case BluetoothDevice.BOND_BONDED:
//                    state.setText("BONDED");
//                    break;
//                case BluetoothDevice.BOND_BONDING:
//                    state.setText("BONDING");
//                    break;
//                case BluetoothDevice.BOND_NONE:
//                    state.setText("NONE");
//                    break;
//                default:
//                    state.setText("UNKNOWN");
//                    break;
//            }
//
//            return view;
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
}