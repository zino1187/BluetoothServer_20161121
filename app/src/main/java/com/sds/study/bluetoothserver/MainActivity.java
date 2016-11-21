package com.sds.study.bluetoothserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    /*블루투스 기기를 제어할 수 있는 클래스*/
    BluetoothAdapter bluetoothAdapter;
    static final int REQUEST_ENABLE_BLUETOOTH=1;
    static final int REQUEST_DISCOVERABLE=2;
    BluetoothServerSocket server;
    String UUID="e2909684-38c2-46fe-b819-1d19204ad4a3";
    TextView txt_msg;
    BluetoothSocket client;
    Thread acceptThread;
    MainActivity mainActivity;
    Handler handler;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=this;
        setContentView(R.layout.activity_main);
        txt_msg=(TextView)findViewById(R.id.txt_msg);

        handler = new Handler(){
            public void handleMessage(Message message) {
                Bundle bundle=message.getData();
                String data=bundle.getString("data");
                txt_msg.append(data+"\n");
            }
        };

        checkSupportBluetooth();
        requestActive();
        setDiscoverable();
        listenClient();
    }

    /*----------------------------------------------
      이 스마트폰이 블루투스 장비가 장착되었나부터 체크..
     ----------------------------------------------*/
    public void checkSupportBluetooth(){
        /*모든 앱들이 공유하는 장치에 대한 레퍼런스를 얻는다!!*/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter==null){
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setTitle("안내").setMessage("이 디바이스는 블루투스를 지원하지 않습니다.").show();
        }
    }

     /*----------------------------------------------
      블루투스가 활성화되어 있는지(즉 켜져 있는지) 체크한다
     ----------------------------------------------*/
    public void requestActive(){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
    }

     /*----------------------------------------------
      요청 처리 결과 제어하기
     ----------------------------------------------*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_ENABLE_BLUETOOTH:
                if(resultCode == RESULT_CANCELED){
                    AlertDialog.Builder alert=new AlertDialog.Builder(this);
                    alert.setTitle("안내").setMessage("저희 서비스를 이용하기 위해서는 \n반드시 블루투스를 활성화하셔야 합니다.").show();
                }break;
            case REQUEST_DISCOVERABLE:
                if(resultCode==RESULT_CANCELED){
                    AlertDialog.Builder alert=new AlertDialog.Builder(this);
                    alert.setTitle("안내").setMessage("현재 디바이스를 다른 기기가 검색할 수 있도록 해주세요ㅜㅜ").show();
                }
        }
    }

     /*----------------------------------------------
      다른 기기가 나를 검색할 수 있도록 허용한다.
     ----------------------------------------------*/
    public void setDiscoverable(){
        Intent intent = new Intent();
        intent.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60*3); //3분
        startActivityForResult(intent, REQUEST_DISCOVERABLE);
    }


     /*----------------------------------------------
      클라이언트의 접속을 준비한다.
     ----------------------------------------------*/
    public void listenClient(){
        String name=this.getPackageName();

        try {
            server=bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, java.util.UUID.fromString(UUID));
        } catch (IOException e) {
            e.printStackTrace();
        }

        acceptThread = new Thread(){
            public void run() {
                Message message = new Message();
                Bundle bundle =new Bundle();
                bundle.putString("data","클라이언트 대기중...");
                message.setData(bundle);
                handler.sendMessage(message);

                try {
                    client=server.accept();
                    Message message2 = new Message();
                    Bundle bundle2 =new Bundle();
                    bundle2.putString("data","클라이언트 접속 감지");
                    message2.setData(bundle2);
                    handler.sendMessage(message2);

                    ClientThread clientThread = new ClientThread(mainActivity, client);
                    clientThread.start();
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        acceptThread.start();
    }

}






