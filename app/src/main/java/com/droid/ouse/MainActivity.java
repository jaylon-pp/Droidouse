package com.droid.ouse;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.droid.ouse.utils.LogUtils;

public class MainActivity extends AppCompatActivity {

    private Button btnOpen;
    private Button btnClose;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpen = findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
            }
        });


        btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter.disable();
            }
        });

        if(bluetoothAdapter.isEnabled()) {
            btnOpen.setEnabled(false);
            btnClose.setEnabled(true);
        } else {
            btnOpen.setEnabled(true);
            btnClose.setEnabled(false);
        }

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DiscoverActivity.class));
            }
        });

        IntentFilter intent = new IntentFilter();
//        intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
//        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        intent.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(stateChangeBroadcast, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("onActivityResult requestCode: %d", requestCode);
        if(requestCode == Constants.REQUEST_ENABLE_BT) {
//            if(resultCode == RESULT_OK) {
//                btnOpen.setEnabled(false);
//                btnClose.setEnabled(true);
//            } else if (resultCode == RESULT_CANCELED){
//                btnOpen.setEnabled(true);
//                btnClose.setEnabled(false);
//            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateChangeBroadcast);
    }

    private BroadcastReceiver stateChangeBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d(intent.getAction());
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
                if(BluetoothAdapter.STATE_ON == state) {
                    btnOpen.setEnabled(false);
                    btnClose.setEnabled(true);
                } else if(BluetoothAdapter.STATE_OFF == state) {
                    btnOpen.setEnabled(true);
                    btnClose.setEnabled(false);
                }

                LogUtils.d("state: %d --> %d", previousState, state);

            }
        }
    };
}