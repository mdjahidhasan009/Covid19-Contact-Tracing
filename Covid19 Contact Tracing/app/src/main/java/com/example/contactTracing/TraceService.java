package com.example.contactTracing;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.example.contactTracing.ble.BleCommManager;
import com.example.contactTracing.ble.MyBlePeripheral;
import com.example.contactTracing.ble.callbacks.BlePeripheralCallback;
import com.example.contactTracing.ble.callbacks.BleScanCallbackv18;
import com.example.contactTracing.ble.callbacks.BleScanCallbackv21;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraceService extends Service {
    /** Constants **/
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;

    /** Bluetooth Stuff **/
    private BleCommManager mBleCommManager;

    private MyBlePeripheral mMyBlePeripheral;

    /** Activity State **/
    private boolean mScanningActive = false;

//    private Map<String, String> map = new HashMap<String, String>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBleBroadcastReceiver);
        stopScan();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Could not initialize bluetooth", Toast.LENGTH_SHORT).show();
        initializeBluetooth();
        startScan();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBleBroadcastReceiver, filter);
    }
    public void initializeBluetooth() {

        try {
            mBleCommManager = new BleCommManager(this);
            mMyBlePeripheral = new MyBlePeripheral(this, mBlePeripheralCallback);
        } catch (Exception e) {
            Toast.makeText(this, "Could not initialize bluetooth", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
        }

        // should prompt user to open settings if Bluetooth is not enabled.
        if (!mBleCommManager.getBluetoothAdapter().isEnabled()) {
            Toast.makeText(getApplicationContext(), "Please turn on bluetooth to use the software", Toast.LENGTH_SHORT).show();
        } else {
            startAdvertising();
        }

    }


    public void startScan() {
        try {
            mScanningActive = true;
            mBleCommManager.scanForPeripherals(mBleScanCallbackv18, mScanCallbackv21);
        } catch (Exception e) {
            Log.e(TAG, "Could not open Ble Device Scanner");
        }

    }
    public void stopScan() {
        mBleCommManager.stopScanning(mBleScanCallbackv18, mScanCallbackv21);
    }
    public void onBlePeripheralDiscovered(BluetoothDevice bluetoothDevice, int rssi) {
        Log.v(TAG, "Found "+bluetoothDevice.getName()+", "+bluetoothDevice.getAddress());
        // only add the peripheral if
        // - it has a name, on
        // - doesn't already exist in our list, or
        // - is transmitting at a higher power (is closer) than an existing peripheral
        boolean addPeripheral = true;
        if (bluetoothDevice.getName() == null) {
            addPeripheral = false;
        }else{
            Map<String, String> map = new HashMap<String, String>();
            map.put("uid", bluetoothDevice.getName());
            FirebaseDatabase.getInstance().getReference("UserCont").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(map);

//            if(!map.containsKey(bluetoothDevice.getName())) {
//                map.put(bluetoothDevice.getName(), bluetoothDevice.getName());
//                FirebaseDatabase.getInstance().getReference("UserCont").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(map);
//            }
        }
    }
    private final BleScanCallbackv21 mScanCallbackv21 = new BleScanCallbackv21() {
        /**
         * New Peripheral discovered
         *
         * @param callbackType int: Determines how this callback was triggered. Could be one of CALLBACK_TYPE_ALL_MATCHES, CALLBACK_TYPE_FIRST_MATCH or CALLBACK_TYPE_MATCH_LOST
         * @param result a Bluetooth Low Energy Scan Result, containing the Bluetooth Device, RSSI, and other information
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice bluetoothDevice = result.getDevice();
            int rssi = result.getRssi();

            onBlePeripheralDiscovered(bluetoothDevice, rssi);
        }

        /**
         * Several peripherals discovered when scanning in low power mode
         *
         * @param results List: List of scan results that are previously scanned.
         */
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                BluetoothDevice bluetoothDevice = result.getDevice();
                int rssi = result.getRssi();

                onBlePeripheralDiscovered(bluetoothDevice, rssi);
            }
        }

        /**
         * Scan failed to initialize
         *
         * @param errorCode	int: Error code (one of SCAN_FAILED_*) for scan failure.
         */
        @Override
        public void onScanFailed(int errorCode) {
            switch (errorCode) {
                case SCAN_FAILED_ALREADY_STARTED:
                    Log.e(TAG, "Fails to start scan as BLE scan with the same settings is already started by the app.");
                    break;
                case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                    Log.e(TAG, "Fails to start scan as app cannot be registered.");
                    break;
                case SCAN_FAILED_FEATURE_UNSUPPORTED:
                    Log.e(TAG, "Fails to start power optimized scan as this feature is not supported.");
                    break;
                default: // SCAN_FAILED_INTERNAL_ERROR
                    Log.e(TAG, "Fails to start scan due an internal error");

            }

            onBleScanStopped();
        }

        /**
         * Scan completed
         */
        public void onScanComplete() {
            onBleScanStopped();
        }
    };
    public final BleScanCallbackv18 mBleScanCallbackv18 = new BleScanCallbackv18() {
        /**
         * New Peripheral discovered
         * @param bluetoothDevice The Peripheral Device
         * @param rssi The Peripheral's RSSI indicating how strong the radio signal is
         * @param scanRecord Other information about the scan result
         */
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            onBlePeripheralDiscovered(bluetoothDevice, rssi);
        }

        /**
         * Scan completed
         */
        @Override
        public void onScanComplete() {
            onBleScanStopped();
        }
    };
    public void startAdvertising() {
        Log.v(TAG, "Starting advertising...");
        try {
            mMyBlePeripheral.startAdvertising();
        } catch (Exception e) {
            Log.e(TAG, "Problem starting advertising");
        }
    }

    /**
     * Event trigger when BLE advertising has stopped
     */
    public void onBleAdvertisingStarted() {
        Toast.makeText(this, "Advertising started", Toast.LENGTH_LONG).show();
    }

    /**
     * Advertising failed to start
     */
    public void onBleAdvertisingFailed() {
        Toast.makeText(this, "Advertising could not started", Toast.LENGTH_LONG).show();
    }

    /**
     * Event trigger when BLE advertising has stopped
     */
    public void onBleAdvertisingStopped() {
        Toast.makeText(this, "Advertising Stopped", Toast.LENGTH_LONG).show();
    }

    /**
     * When the bluetooth radio turns on, initialize the bluetooth connection
     */
    private final BroadcastReceiver mBleBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.v(TAG, "Bluetooth turned off");
                        Toast.makeText(getApplicationContext(), "Please turn on your bluetooth",
                                Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.v(TAG, "Bluetooth turned on");
                        Toast.makeText(getApplicationContext(), "Thx for turning on bluetooth",
                                Toast.LENGTH_LONG).show();
                        startAdvertising();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

    /**
     * Response to changes to the Bluetooth Peripheral state
     */
    private final BlePeripheralCallback mBlePeripheralCallback= new BlePeripheralCallback() {

        public void onAdvertisingStarted() {
            Log.v(TAG, "Advertising stated");
            onBleAdvertisingStarted();
        }

        public void onAdvertisingFailed(int errorCode) {
            onBleAdvertisingFailed();
            switch (errorCode) {
                case AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED:
                    Log.e(TAG, "Failed to start advertising as the advertising is already started.");
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE:
                    Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    Log.e(TAG, "This feature is not supported on this platform.");
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR:
                    Log.e(TAG, "Operation failed due to an internal error.");
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
                    break;
                default:
                    Log.e(TAG, "unknown problem");
            }
        }

        public void onAdvertisingStopped() {
            Log.v(TAG, "Advertising stopped");
            onBleAdvertisingStopped();
        }
    };
    public void onBleScanStopped() {
        // update UI compenents to reflect that a BLE scan has stopped
        // it's possible that this method will be called before the menu has been instantiated
        // Check to see if menu items are initialized, or Activity will crash
        mScanningActive = false;
    }
}