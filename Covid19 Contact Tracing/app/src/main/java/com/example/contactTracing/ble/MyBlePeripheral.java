package com.example.contactTracing.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.contactTracing.ble.callbacks.BlePeripheralCallback;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class will create a local bluetooth peripheral
 */
public class MyBlePeripheral {
    private static final String TAG = MyBlePeripheral.class.getSimpleName();

    //Peripheral and GATT profile
    public static final String ADVERTISING_NAME = FirebaseAuth.getInstance().getUid();
    /**
     * Advertising settings
     *
     * Advertising modes are ADVERTISE_TX_POWER_HIGH - ADVERTISE_TX_POWER_MEDIUM -
     * ADVERTISE_TX_POWER_LOW - ADVERTISE_TX_POWER_ULTRA_LOW
     *
     * Transmission power modes are - ADVERTISE_TX_POWER_HIGH - ADVERTISE_TX_POWER_MEDIUM -
     * ADVERTISE_TX_POWER_LOW - ADVERTISE_TX_POWER_ULTRA_LOW
     **/
    int mAdvertisingMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY;  //Advertising Mode
    int mTransmissionPower = AdvertiseSettings.ADVERTISE_TX_POWER_HIGH;   //Transmission Power Mode
    public BlePeripheralCallback mBlePeripheralCallback;                  //Callback Handlers
    //Bluetooth Stuff
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;

    /**
     * Construct a new peripheral
     *
     * @param context               The application context
     * @param blePeripheralCallback The callback handler that interfaces with this Peripheral
     * @throws Exception Exception thrown if Bluetooth is not supported
     */
    public MyBlePeripheral(final Context context, BlePeripheralCallback blePeripheralCallback) throws Exception {
        mBlePeripheralCallback = blePeripheralCallback;

        //making sure this device supports bluetooth low energy
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
            throw new Exception("Bluetooth Low Energy is not supported");

        //get a reference to the Bluetooth Manager class, which allows us to talk to talk the BLE radio
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //Waring: this function does not work on some systems
        if (!mBluetoothAdapter.isMultipleAdvertisementSupported())
            throw new Exception("Peripheral mode not supported");
        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothAdvertiser == null) //for extra precaution
            throw new Exception("Peripheral mode not supported");
    }

    /**
     * Get the system bluetooth adapter
     * @return BluetoothAdapter
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    /**
     * Start Advertising
     * @throws Exception Exception thrown if Bluetooth Peripheral mode is not supported
     */
    public void startAdvertising() {
        mBluetoothAdapter.setName(ADVERTISING_NAME);
        //Build Advertise settings with transmission power and advertise speed
        AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(mAdvertisingMode)
                .setTxPowerLevel(mTransmissionPower)
                .setConnectable(false)
                .build();

        AdvertiseData.Builder advertiseBuilder = new AdvertiseData.Builder();
        advertiseBuilder.setIncludeDeviceName(true); //set advertising name
        AdvertiseData advertiseData = advertiseBuilder.build();
        // begin advertising
        try {
            mBluetoothAdvertiser.startAdvertising(advertiseSettings, advertiseData, mAdvertiseCallback);
        } catch (Exception e) {
            Log.e(TAG, "Could not start advertising");
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop Advertising
     */
    public void stopAdvertising() {
        if(mBluetoothAdvertiser != null) {
            mBluetoothAdvertiser.stopAdvertising(mAdvertiseCallback);
            mBlePeripheralCallback.onAdvertisingStopped();
        }
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        /**
         * Advertising started
         * @param settingsInEffect The advertising that worked
         */
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            mBlePeripheralCallback.onAdvertisingStarted();
        }

        /**
         * Advertising failed
          * @param errorCode The reason for failure
         */
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            mBlePeripheralCallback.onAdvertisingFailed(errorCode);
        }
    };
}