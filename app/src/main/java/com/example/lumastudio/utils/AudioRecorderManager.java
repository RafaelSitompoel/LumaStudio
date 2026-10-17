package com.example.lumastudio.utils;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class AudioRecorderManager {

    private final AudioManager audioManager;

    public AudioRecorderManager(Context context) {
        this.audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    public List<String> getAvailableAudioInputs() {
        List<String> inputs = new ArrayList<>();
        inputs.add("Internal Microphone");

        if (audioManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
            for (AudioDeviceInfo device : devices) {
                int type = device.getType();
                if (type == AudioDeviceInfo.TYPE_USB_DEVICE || type == AudioDeviceInfo.TYPE_USB_HEADSET) {
                    inputs.add("USB External Microphone");
                } else if (type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO || type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
                    inputs.add("Bluetooth Audio Input");
                } else if (type == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                    inputs.add("Wired Headset Microphone");
                }
            }
        }
        return inputs;
    }
}