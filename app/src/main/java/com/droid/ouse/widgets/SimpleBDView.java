package com.droid.ouse.widgets;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.droid.ouse.BlueToothUtils;
import com.droid.ouse.R;

public class SimpleBDView extends RelativeLayout {
    public SimpleBDView(Context context) {
        super(context);
        init(context);
    }

    public SimpleBDView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimpleBDView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SimpleBDView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private TextView deviceName;
    private TextView deviceMac;
    private TextView deviceState;
    private TextView deviceType;
    private BluetoothDevice bluetoothDevice;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_device_item, this);
        deviceName = findViewById(R.id.device_name);
        deviceMac = findViewById(R.id.device_mac);
        deviceState = findViewById(R.id.device_state);
        deviceType = findViewById(R.id.device_type);
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        if(null != bluetoothDevice) {
            deviceName.setText(bluetoothDevice.getName());
            deviceMac.setText(bluetoothDevice.getAddress());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                deviceState.setText(BlueToothUtils.getTypeName(bluetoothDevice.getType()));
                deviceType.setText(BlueToothUtils.getTypeName(bluetoothDevice.getType()));
            }
        }
    }
}
