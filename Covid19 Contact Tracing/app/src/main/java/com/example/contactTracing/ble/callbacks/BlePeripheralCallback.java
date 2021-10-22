package com.example.contactTracing.ble.callbacks;

/**
 * BlePeripheralCallBack has callbacks to support notifications related to advertising and data
 * transmission
 */
public abstract class BlePeripheralCallback {
    /**
     * Advertising Started
     */
    public abstract void onAdvertisingStarted();

    /**
     * Advertising Could not Start
     */
    public abstract void onAdvertisingFailed(int errorCode);

    /**
     * Advertising Stopped
     */
    public abstract void onAdvertisingStopped();
}
