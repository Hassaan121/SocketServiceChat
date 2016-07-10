package com.testapp.hv.socketservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private SocketService mBoundService;
    private Boolean mIsBound;
    public MainActivity ssc;
    Button start;
    Button stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ssc = this;

        start = (Button)findViewById(R.id.serviceButton);
        stop = (Button)findViewById(R.id.cancelButton);
        start.setOnClickListener(startListener);
        stop.setOnClickListener(stopListener);

    }
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((SocketService.LocalBinder)service).getService();
        }
        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    private void doBindService() {
        bindService(new Intent(MainActivity.this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
     //   mBoundService.IsBoundable();
    }


    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
   //     doUnbindService();
    }


    private View.OnClickListener startListener =new View.OnClickListener(){
        public void onClick(View v) {
    /*        startService(new Intent(MainActivity.this,SocketService.class));
            doBindService();
    */      Intent service = new Intent(MainActivity.this, ForegroundService.class);
                service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                ForegroundService.IS_SERVICE_RUNNING = true;
           //     b1.setText("Stop Service");
            startService(service);

        }
    };

    private View.OnClickListener stopListener = new View.OnClickListener() {
        public void onClick(View v){
      //      stopService(new Intent(MainActivity.this,SocketService.class));
                 Intent service = new Intent(MainActivity.this, ForegroundService.class);
                 service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                ForegroundService.IS_SERVICE_RUNNING = false;
            stopService(new Intent(MainActivity.this,ForegroundService.class));
            //      b1.setText("Start Service");
        }
    };
}