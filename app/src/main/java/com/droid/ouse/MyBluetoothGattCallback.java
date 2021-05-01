package com.droid.ouse;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.droid.ouse.utils.LogUtils;

import java.util.List;
import java.util.UUID;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MyBluetoothGattCallback extends BluetoothGattCallback {

    final static String UUID_DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    final static String UUID_GENERIC_ACCESS = "00001800-0000-1000-8000-00805f9b34fb";
    final static String UUID_DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    final static String UUID_BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
//    final static String UUID_MANUFACManufacturer Name String = "00002a29-0000-1000-8000-00805f9b34fb";

    public MyBluetoothGattCallback() {
        super();
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        LogUtils.d("onPhyUpdate");
    }

    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyRead(gatt, txPhy, rxPhy, status);
        LogUtils.d("onPhyRead");
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        LogUtils.d("onConnectionStateChange status: %d newState: %d", status, newState);

        String intentAction;
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            LogUtils.d("Connected to GATT server. discoverServices:%b", gatt.discoverServices());

        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            LogUtils.i("Disconnected from GATT server.");
        }

        ParcelUuid[] uuids = gatt.getDevice().getUuids();
        if(null != uuids) {
            for(int i = 0; i < uuids.length; i++) {
                LogUtils.d("onConnectionStateChange uuids: %s", uuids[i].getUuid().toString());
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        LogUtils.d("onServicesDiscovered");
        displayGattServices(gatt.getServices());

        List<BluetoothGattService> services = gatt.getServices();
        if(null != services) {
            for(BluetoothGattService service: services) {
                if(UUID_DEVICE_INFORMATION_SERVICE.equals(service.getUuid().toString())) {
//                    service.getCharacteristics();
//                    displayGattCharacteristics(service.getCharacteristics());
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for(BluetoothGattCharacteristic characteristic: characteristics) {
//                        if(gatt.setCharacteristicNotification(characteristic, true)) {
//                            LogUtils.e("setCharacteristicNotification failed");
//                        }
//                        byte[] values = characteristic.getValue();
//                        LogUtils.d("values: %s", new String(values));
                        LogUtils.d("readUuid: %s", characteristic.getUuid().toString());
                        gatt.readCharacteristic(characteristic);
                    }
                } else if(UUID_GENERIC_ACCESS.equals(service.getUuid().toString())) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for(BluetoothGattCharacteristic characteristic: characteristics) {
//                        if(gatt.setCharacteristicNotification(characteristic, true)) {
//                            LogUtils.e("setCharacteristicNotification failed");
//                        }
//                        byte[] values = characteristic.getValue();
                        LogUtils.d("readUuid*: %s", characteristic.getUuid().toString());
                        gatt.readCharacteristic(characteristic);
                    }
                } else {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for(BluetoothGattCharacteristic characteristic: characteristics) {
//                        if(gatt.setCharacteristicNotification(characteristic, true)) {
//                            LogUtils.e("setCharacteristicNotification failed");
//                        }
//                        byte[] values = characteristic.getValue();
                        LogUtils.d("readUuid*: %s", characteristic.getUuid().toString());
                        gatt.readCharacteristic(characteristic);
                        if(null != characteristic.getDescriptors()) {
                            for(BluetoothGattDescriptor descriptor:characteristic.getDescriptors()) {
                                LogUtils.d("readUuid*: %s", characteristic.getUuid().toString());
                                gatt.readDescriptor(descriptor);
                            }
                        }
                        boolean r = gatt.setCharacteristicNotification(characteristic, true);
                        LogUtils.e("setCharacteristicNotification*: %b", r);
                    }
                }
            }
        }

//        if(null != services) {
//            for(BluetoothGattService service: services) {
//                String uuid = service.getUuid().toString();
//                if(UUID_DEVICE_INFORMATION_SERVICE.equals(uuid)) {
//                    LogUtils.d("onServicesDiscovered uuids: %s", uuid);
//                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
//                    if(null != characteristics) {
//                        for(BluetoothGattCharacteristic characteristic: characteristics) {
////                            displayCharacteristic(characteristic);
////                            boolean re = gatt.setCharacteristicNotification(characteristic, true);
////                            LogUtils.d("re: %b ", re);
////                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
////                                    characteristic.getUuid());
////                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
////                            re = gatt.writeDescriptor(descriptor);
////                            LogUtils.d("re: %b ", re);
//                        }
//                    }
//                }
//            }
//        }
    }

    private void displayGattServices(List<BluetoothGattService> services) {
        if(null == services) {
            return;
        }
        for(BluetoothGattService service: services) {
            UUID uuid = service.getUuid();
            LogUtils.d("    service uuids: %s", uuid);
            displayGattCharacteristics(service.getCharacteristics());
        }
    }

    private void displayGattCharacteristics(List<BluetoothGattCharacteristic> characteristics) {
        if(null == characteristics) {
            return;
        }
        for(BluetoothGattCharacteristic characteristic:characteristics) {
            UUID uuid = characteristic.getUuid();
            LogUtils.d("        character uuid: %s", uuid);
            displayGattDescriptors(characteristic.getDescriptors());
        }
    }

    private void displayGattDescriptors(List<BluetoothGattDescriptor> descriptors) {
        if(null == descriptors) {
            return;
        }
        for(BluetoothGattDescriptor descriptor:descriptors) {
            UUID uuid = descriptor.getUuid();
            LogUtils.d("            descriptor uuid: %s", uuid);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        LogUtils.d("onCharacteristicRead. status: %d", status);
        LogUtils.d("onCharacteristicRead. uuid: %s value: %s", characteristic.getUuid().toString(), new String(characteristic.getValue()));
//        LogUtils.d("onCharacteristicRead. uuid: %s", characteristic.getUuid().toString());
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        LogUtils.d("onCharacteristicWrite");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        LogUtils.d("onCharacteristicChanged");
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
        LogUtils.d("onDescriptorRead");
        LogUtils.d("onDescriptorRead. status: %d", status);
        LogUtils.d("onDescriptorRead. uuid: %s value: %s", descriptor.getUuid().toString(), new String(descriptor.getValue()));
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        LogUtils.d("onDescriptorWrite");
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
        LogUtils.d("onReliableWriteCompleted");
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        LogUtils.d("onReadRemoteRssi");
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        LogUtils.d("onMtuChanged");
    }
}
