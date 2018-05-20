package com.example.administrator.mybluetoothletest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    boolean scanning = false;

    private BluetoothManager bluetoothManager;
    //블루투스 매니저
    //블루트스 기능을 총괄적으로 관리함.
    private BluetoothAdapter bluetoothAdapter;
    //블루투스 연결자
    //블루투스를 스켄하거나, 페어링된장치목록을 읽어들일 수 있습니다.
    //이를 바탕으로 블루투스와의 연결을 시도할 수 있습니다.

    private class BleList extends BaseAdapter{//리스트뷰 어뎁터 선언
        private ArrayList<BluetoothDevice> devices;
        private ArrayList<Integer> RSSIs;
        private LayoutInflater inflater;


        public BleList(){
            super();
            devices = new ArrayList<BluetoothDevice>();
            RSSIs = new ArrayList<Integer>();
            inflater = ((Activity) MainActivity.this).getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device,int rssi){
            if(!devices.contains(device)){
                devices.add(device);
                RSSIs.add(rssi);
            }
            else{
                RSSIs.set(devices.indexOf(device),rssi);
            }
        }

        public void clear(){
            devices.clear();
            RSSIs.clear();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(android.R.layout.two_line_list_item,null);
                viewHolder.deviceName = (TextView) convertView.findViewById(android.R.id.text1);
                viewHolder.deviceRssi = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String deviceName = devices.get(position).getName();
            int rssi = RSSIs.get(position);

            viewHolder.deviceName.setText(deviceName != null && deviceName.length() > 0 ?deviceName:"알 수 없는 장치");
            viewHolder.deviceRssi.setText(String.valueOf(rssi));

            return convertView;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceRssi;
    }


    Button button;//버튼

    ListView listView;//리스트뷰 객체
    BleList bleList = null;//리스트 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH},1);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();


        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()){
            //블루투스를 지원하지 않거나 켜져있지 않으면 장치를끈다.
            Toast.makeText(this, "블루투스를 켜주세요", Toast.LENGTH_SHORT).show();
            finish();
        }

        //리스트뷰 설정
        bleList = new BleList();
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(bleList);

        //버튼설정
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!scanning){
                    bluetoothAdapter.startLeScan(leScanCallback);
                }
                else{
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    bleList.clear();
                    bleList.notifyDataSetChanged();
                }
                scanning = !scanning;

            }
        });
    }

    // 스켄 이후 장치 발견 이벤트
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d("scan",device.getName() + " RSSI :" + rssi + " Record " + scanRecord);
            bleList.addDevice(device,rssi);
            bleList.notifyDataSetChanged();
        }
    };



}
