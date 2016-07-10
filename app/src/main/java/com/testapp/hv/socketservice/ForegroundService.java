package com.testapp.hv.socketservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.google.android.gms.internal.zzir.runOnUiThread;


public class ForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    public static boolean IS_SERVICE_RUNNING = false;
    MediaPlayer md = null;
    static JSONArray arr = null;
    static String server = "http://192.168.0.118:3000/";
    String name = "";
    public static io.socket.client.Socket mSocket;
    {
        try {
            // mSocket = IO.socket("http://chat.socket.io");
            mSocket = IO.socket(server);
        } catch (URISyntaxException e) {

        }
    }


    private void init() {
        md = MediaPlayer.create(this, R.raw.beep02);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            showNotification();
            Toast.makeText(this, "Service Started!", Toast.LENGTH_SHORT).show();
            init();
            mSocket.on("new message", onNewMessage);
            mSocket.on(Socket.EVENT_CONNECT, onConnection);
            mSocket.on("disconnect", onDisconnect);
            mSocket.connect();
            md.setLooping(true);
            md.start();
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i(LOG_TAG, "Clicked Previous");
            Toast.makeText(this, "Clicked Previous!", Toast.LENGTH_SHORT)
                    .show();
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i(LOG_TAG, "Clicked Play");
            Toast.makeText(this, "Clicked Play!", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i(LOG_TAG, "Clicked Next");
            Toast.makeText(this, "Clicked Next!", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            Toast.makeText(this, "Stopping", Toast.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Intent previousIntent = new Intent(this, ForegroundService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, ForegroundService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, ForegroundService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("TutorialsFace Music Player")
                .setTicker("TutorialsFace Music Player")
                .setContentText("My song")
/*
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
*/

                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous, "Previous",
                        ppreviousIntent)
                .addAction(android.R.drawable.ic_media_play, "Play",
                        pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next",
                        pnextIntent).build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
        Toast.makeText(this, "Service Detroyed!", Toast.LENGTH_SHORT).show();
mSocket.disconnect();
        md.stop();
        md.release();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String username = args[0].toString();
                    String message = "|| HASSAAN ||" ;
//                    mInputMessageView.setText("");
/*
                    try {
                        JSONObject dat=new JSONObject(args[0].toString());
                        username = dat.getString("username");
                        message = dat.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
*/
                    try {
                        arr = new JSONArray(args[0].toString());
                        for (int k = 0; k < arr.length(); k++) {
                            username = arr.get(k).toString();
                            JSONObject dat = new JSONObject(username.toString());
                            username = dat.getString("username");
                            message = dat.getString("message");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
         Toast.makeText(getApplicationContext(),username+":"+message,Toast.LENGTH_LONG).show();

                    // add the message to view
    //                addMessage(username, message);
                }
            });
        }
    };
    private Emitter.Listener onConnection = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
      //              mInputMessageView.setText("");
                    try {
                        Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                        //                   tv.append("\n\n\n\n\n Connected");
                    } catch (Exception e) {
                    }
                }
            });
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
       //           mInputMessageView.setText("");
                    try {
                        Toast.makeText(getApplicationContext(),"Disconnected",Toast.LENGTH_LONG).show();
     //                   tv.append("\n\n\n\n\n DisConnect");
                    } catch (Exception e) {
                    }
                }
            });
        }
    };
}