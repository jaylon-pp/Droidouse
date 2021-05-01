package com.droid.ouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.droid.ouse.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ScanBleActivity extends AppCompatActivity {

    private Button btnStartScan;
    private Button btnStopScan;

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler = new Handler();

    private ListView deviceListView;
    private DeviceAdapter deviceAdapter;
    private List<BluetoothDevice> deviceList = new ArrayList<>();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ble);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        btnStartScan = findViewById(R.id.start_scan);
        btnStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(true);
            }
        });

        btnStopScan = findViewById(R.id.stop_scan);
        btnStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(false);
            }
        });

        deviceListView = findViewById(R.id.device_list);
        deviceAdapter = new DeviceAdapter(this, R.layout.layout_device_item, deviceList);
        deviceListView.setAdapter(deviceAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ScanBleActivity.this, ConnectBleActivity.class);
                LogUtils.d( "position: " + position);
                intent.putExtra("bluetoothDevice", deviceList.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PackageManager pm = getPackageManager();
        if(pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
            LogUtils.d("permission: %d  %b", pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()), ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION));
            if (Build.VERSION.SDK_INT >= 6.0) {
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        1245);
            }

            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                finish();
            } else {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtils.d( "requestCode: " + requestCode);
        if(requestCode == 1245) {

        }
    }

    @SuppressLint("NewApi")
    class MyLeScanCallback implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            LogUtils.d("bluetoothDevice: %s", bluetoothDevice.getAddress());
//                    leDeviceListAdapter.addDevice(device);
//                    leDeviceListAdapter.notifyDataSetChanged();
            deviceAdapter.add(bluetoothDevice);
        }
    }

    private MyLeScanCallback leScanCallback = new MyLeScanCallback();

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        bluetoothAdapter.stopLeScan(leScanCallback);
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bluetoothAdapter.startLeScan(leScanCallback);
            }
        } else {
            mScanning = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bluetoothAdapter.stopLeScan(leScanCallback);
            }
        }
    }
}