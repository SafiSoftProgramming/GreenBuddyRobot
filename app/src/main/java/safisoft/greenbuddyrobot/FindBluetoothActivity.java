package safisoft.greenbuddyrobot;


import static safisoft.greenbuddyrobot.java.LivePreviewActivity.REQUEST_ENABLE_BT;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.util.ArrayList;

import safisoft.greenbuddyrobot.java.LivePreviewActivity;

public class FindBluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    String PLANT_NAME;
    String PLANT_PLACE;
    String PLANT_PIC;
    String PLANT_CONNECT_STATE;
    String PLANT_WATER_STATE;
    String PLANT_NUTRIENT_STATE;
    String PLANT_LIGHT_STATE;
    String PLANT_TEMP_STATE;
    String BLUETOOTH_NAME;
    String BLUETOOTH_MAC;

    LinearLayout lay_buts_lottie ;

    ImageButton btn_re_connect,btn_test_app;
    ImageView imgv_connection_info ;

    CountDownTimer countDownTimer;

    DbConnction db ;
    Cursor c = null;
    String MODULE_MAC ;

    String MODE ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bluetooth);

        MODE = getIntent().getStringExtra("CONTROL_MODE");


        db = new DbConnction(FindBluetoothActivity.this);
        try {
            db.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            db.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        // Bluetooth MAC
        c = db.Row_Query("bluetooth", "id_", "1");
        c.moveToFirst();
        MODULE_MAC = c.getString(c.getColumnIndexOrThrow("mac")) ;



        PLANT_NAME = getIntent().getStringExtra("PLANT_NAME");
        PLANT_PLACE = getIntent().getStringExtra("PLANT_PLACE");
        PLANT_PIC = getIntent().getStringExtra("PLANT_PIC");
        PLANT_CONNECT_STATE = getIntent().getStringExtra("PLANT_CONNECT_STATE");
        PLANT_WATER_STATE = getIntent().getStringExtra("PLANT_WATER_STATE");
        PLANT_NUTRIENT_STATE = getIntent().getStringExtra("PLANT_NUTRIENT_STATE");
        PLANT_LIGHT_STATE = getIntent().getStringExtra("PLANT_LIGHT_STATE");
        PLANT_TEMP_STATE = getIntent().getStringExtra("PLANT_TEMP_STATE");
        BLUETOOTH_NAME = "";
        BLUETOOTH_MAC = "";



        btn_re_connect = findViewById(R.id.btn_re_connect);
        imgv_connection_info = findViewById(R.id.imgv_connection_info);
        btn_test_app = findViewById(R.id.btn_test_app);
        lay_buts_lottie = findViewById(R.id.lay_buts_lottie);





        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);

        } else {
            try {
                mBluetoothAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);
            } catch (Exception e) {

                 lay_buts_lottie.setVisibility(View.VISIBLE);
                 imgv_connection_info.setBackgroundResource(R.drawable.ic_unable_connect_text);

            }
        }


        countDownTimer = new CountDownTimer(8000, 1000
        ) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                lay_buts_lottie.setVisibility(View.VISIBLE);
                imgv_connection_info.setBackgroundResource(R.drawable.ic_unable_connect_text);

            }
        }.start();


        btn_re_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    countDownTimer.cancel();
                    mBluetoothAdapter.startDiscovery();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter);

                    lay_buts_lottie.setVisibility(View.INVISIBLE);
                    imgv_connection_info.setBackgroundResource(R.drawable.ic_connecting_text);
                    countDownTimer.start();
                } catch (Exception e) {

                    lay_buts_lottie.setVisibility(View.VISIBLE);
                    imgv_connection_info.setBackgroundResource(R.drawable.ic_unable_connect_text);

                }
            }
        });

        btn_test_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_move = new Intent(FindBluetoothActivity.this, LivePreviewActivity.class);
                startActivity(intent_move);
                finish();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

                  if (requestCode == REQUEST_ENABLE_BT) {
                           if (resultCode == Activity.RESULT_OK) {
                               mBluetoothAdapter.startDiscovery();
                               IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                               registerReceiver(mReceiver, filter);
                           }
                           if (resultCode == Activity.RESULT_CANCELED) {

                           }
    }                  }

    @Override
    protected void onDestroy() {
        //   unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                //  if (device.getAddress().equals("98:D3:31:F9:78:0B")) { //
                    if (device.getAddress().equals(MODULE_MAC)) {
                    countDownTimer.cancel();
                    BLUETOOTH_NAME = device.getName();
                    BLUETOOTH_MAC = device.getAddress();

                    lay_buts_lottie.setVisibility(View.INVISIBLE);
                    imgv_connection_info.setBackgroundResource(R.drawable.ic_connected_text);

                    countDownTimer = new CountDownTimer(2000, 1000
                    ) {
                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {

                            if(MODE.equals("AUTO")) {
                                Intent intent_move = new Intent(FindBluetoothActivity.this, LivePreviewActivity.class);
                                intent_move.putExtra("BLUETOOTH_MAC", BLUETOOTH_MAC);
                                startActivity(intent_move);
                                finish();
                            }

                            if(MODE.equals("MANUAL")) {
                                Intent intent_move = new Intent(FindBluetoothActivity.this, Manual_Control_Activity .class);
                                intent_move.putExtra("BLUETOOTH_MAC", BLUETOOTH_MAC);
                                startActivity(intent_move);
                                finish();
                            }




                        }
                    }.start();




                }



            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FindBluetoothActivity.this, Choose_Control_Options_Activity.class);
        startActivity(intent);
        finish();
    }



}