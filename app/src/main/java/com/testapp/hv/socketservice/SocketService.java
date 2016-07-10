package com.testapp.hv.socketservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by hv on 7/9/16.
 */
public class SocketService extends Service {

    Socket s;
    PrintStream os;
    static String server = "http://192.168.0.118:3000/";

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return myBinder;
    }

    private final IBinder myBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        s = new Socket();
    }

    public void IsBoundable(){
        Toast.makeText(this,"I bind like butter", Toast.LENGTH_LONG).show();
    }

    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        new Thread(connect).start();
    }

    class connectSocket implements Runnable {
        @Override
        public void run() {
            SocketAddress socketAddress = new InetSocketAddress(server, 4505);
            try {
                s.connect(socketAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            s.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        s = null;
    }
}