package com.droid.ouse;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.droid.ouse.br.BlueToothConnectReceiver;
import com.droid.ouse.utils.LogUtils;

public class BondActivity extends AppCompatActivity {

    private TextView deviceName;
    private TextView deviceMac;
    private Button btnBond;
    private Button btnUnbond;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bond);

        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getExtras();
        device = (BluetoothDevice) bundle.get("device");
        deviceName = findViewById(R.id.device_name);
        deviceMac = findViewById(R.id.device_mac);
        btnBond = findViewById(R.id.btn_bond);
        btnUnbond = findViewById(R.id.btn_unbond);

        deviceName.setText(device.getName());
        deviceMac.setText(device.getAddress());

        btnBond.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                device.connectGatt(BondActivity.this, true, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        LogUtils.d("Status:" + status + " " + newState);
                        switch (status) {
                            case BluetoothGatt.GATT_SUCCESS:
                                //连接成功
                                break;
                            case BluetoothProfile.STATE_CONNECTED:
                                //发现蓝牙服务
                                break;
                        }
                        super.onConnectionStateChange(gatt, status, newState);
                    }
                });
            }
        });

        btnUnbond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    private BlueToothConnectReceiver blueToothConnectReceiver;
    @Override
    protected void onResume() {
        super.onResume();

        //蓝牙连接广播
        blueToothConnectReceiver = new BlueToothConnectReceiver();
        IntentFilter filter_connect = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(blueToothConnectReceiver, filter_connect);
        IntentFilter filter_disconnect = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(blueToothConnectReceiver, filter_disconnect);
        blueToothConnectReceiver.setOnBleConnectListener(new BlueToothConnectReceiver.OnBleConnectListener() {
            @Override
            public void onConnect(BluetoothDevice device) {
            }

            @Override
            public void onDisConnect(BluetoothDevice device) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}