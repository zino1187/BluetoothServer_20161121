package com.sds.study.bluetoothserver;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    /*블루투스 기기를 제어할 수 있는 클래스*/
    BluetoothAdapter bluetoothAdapter;
    static final int REQUEST_ENABLE_BLUETOOTH=1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkSupportBluetooth();
        requestActive();
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
            }
        }
    }
}






