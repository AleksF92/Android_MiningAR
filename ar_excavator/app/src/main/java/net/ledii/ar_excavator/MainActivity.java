package net.ledii.ar_excavator;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private static final int ID_SERVICE_RUNNING = 1;
    private Intent oreService;
    private OreBroadcast oreBroadcast;
    private IntentFilter oreIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize global variables
        Global.initialize(this);

        //Initialize background service
        oreService = new Intent(this, OreService.class);
        oreIntentFilter = new IntentFilter("SERVICE_BROADCAST");
        oreBroadcast = new OreBroadcast();

        //Start game
        startGame();

        //Start service
        setServiceEnabled(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //Enter fullscreen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            enterTrueFullscreen();
        }
        else {
            //Dummy
        }
    }

    @Override
    public void onStop() {
        //Stop service
        setServiceEnabled(false);
    }

    private void enterTrueFullscreen() {
        if (Build.VERSION.SDK_INT >= 19) {
            //Hide nav and status bar
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
        if (Build.VERSION.SDK_INT < 16) {
            int fullscreen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setFlags(fullscreen, fullscreen);
        }
    }

    private void setServiceEnabled(boolean enabled) {
        if (enabled) {
            //Start service
            startService(oreService);
            LocalBroadcastManager.getInstance(this).registerReceiver(oreBroadcast, oreIntentFilter);

            //Show notification
            String title = "Excavator";
            String desc = "The background service is currently running!";
            showNotification(ID_SERVICE_RUNNING, title, desc);
        }
        else {
            //Stop service
            stopService(oreService);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(oreBroadcast);

            //Hide notification
            hideNotification(ID_SERVICE_RUNNING);
        }
    }

    private void showNotification(int id, String title, String desc) {
        //Create notification
        int smallIcon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(desc)
                    .setSmallIcon(smallIcon)
                    .setWhen(when)
                    .build();
        }
        else {
            notification = new Notification(smallIcon, desc, when);
        }
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        //Add notification
        NotificationManager notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifier.notify(id, notification);
    }

    private void hideNotification(int id) {
        //Cancel notification
        NotificationManager notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifier.cancel(id);
    }

    private void startGame() {
        //Open mining minigame
        MiningGame miningGame = new MiningGame(this);
        String rockType = "Copper";
        if (Global.randomInt(0, 1) > 0) { rockType = "Adamant"; }
        miningGame.newGame(rockType);
    }
}