package com.sds.study.bluetoothserver;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by zino on 2016-11-21.
 */

public class ClientThread extends Thread{
    MainActivity mainActivity;
    BluetoothSocket client;
    BufferedReader buffr;
    BufferedWriter buffw;
    boolean flag=true;


    public ClientThread(final MainActivity mainActivity, BluetoothSocket client) {
        this.mainActivity=mainActivity;
        this.client = client;

        try {
            buffr=new BufferedReader(new InputStreamReader(client.getInputStream(),"utf-8"));
            buffw=new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listen(){
        String msg=null;
        try {
            msg=buffr.readLine();

            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("data", msg);
            message.setData(bundle);

            send(msg);

            mainActivity.handler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg){
        try {
            buffw.write(msg);
            buffw.write("\n");
            buffw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(flag){
            listen();
        }
    }

}
