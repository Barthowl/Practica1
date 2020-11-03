package com.example.practica1;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.content.BroadcastReceiver;

public class IncomingCallsReceiver extends BroadcastReceiver {
    public static final String TAG = MainActivity.class.getName() + "zxcvb";

    @Override
    public void onReceive(Context context, Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.v(TAG, "on receive de Incomming Calls Receiver");
    }
}
