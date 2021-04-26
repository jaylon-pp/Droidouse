package com.droid.ouse.br;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.droid.ouse.utils.LogUtils;

public class BlueToothConnectReceiver extends BroadcastReceiver {
    private OnBleConnectListener onBleConnectListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                if (onBleConnectListener != null) {
                    onBleConnectListener.onConnect(device);
                }
                LogUtils.d("蓝牙已连接：" + device.getName());
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                if (onBleConnectListener != null) {
                    onBleConnectListener.onDisConnect(device);
                }
                LogUtils.d("蓝牙已断开：" + device.getName());
                break;
        }
    }

    public interface OnBleConnectListener {
        void onConnect(BluetoothDevice device);

        void onDisConnect(BluetoothDevice device);
    }

    public void setOnBleConnectListener(OnBleConnectListener onBleConnectListener) {
        this.onBleConnectListener = onBleConnectListener;
    }
}
