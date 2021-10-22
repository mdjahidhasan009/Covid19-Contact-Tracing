package com.example.contactTracing.ble.callbacks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * BleScanCallbackv18 has callbacks to support not only onLeScan when a new Peripheral is found,
 * but also onScanComplete when BLE scanning has stopped
 */
public abstract class BleScanCallbackv18 implements BluetoothAdapter.LeScanCallback {

    /**
     * New Perpheral found.
     *
     * @param bluetoothDevice The Peripheral Device
     * @param rssi The Peripheral's RSSI indicating how strong the radio signal is
     * @param scanRecord Other information about the scan result
     */
    //@Override
    public abstract void onLeScan(final BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord);

    /**
     * BLE Scan complete
     */
    public abstract void onScanComplete();


}
