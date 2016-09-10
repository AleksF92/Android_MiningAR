package net.ledii.ar_excavator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OreBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("SERVICE_DATA");
        Log.println(Log.ASSERT, "Debug", "Responce: " + name + "!");
    }
}