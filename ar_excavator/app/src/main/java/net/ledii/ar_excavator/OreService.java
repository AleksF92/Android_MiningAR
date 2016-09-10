package net.ledii.ar_excavator;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class OreService extends IntentService {
    private String data = "Coca Cola";

    public OreService() {
        super("OreService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Broadcast data
        Intent callbackIntent = new Intent("SERVICE_BROADCAST");
        callbackIntent.putExtra("SERVICE_DATA", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(callbackIntent);
    }
}