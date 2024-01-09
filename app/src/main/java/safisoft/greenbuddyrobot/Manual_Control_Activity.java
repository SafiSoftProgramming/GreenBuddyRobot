package safisoft.greenbuddyrobot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import safisoft.greenbuddyrobot.java.ApplicationEx;
import safisoft.greenbuddyrobot.java.LivePreviewActivity;

public class Manual_Control_Activity extends AppCompatActivity {

    ImageView imgv_robot_parts ;
    TextView txtv_motor_name , txtv_motor_description ;
    TextView txtv_connecting_lable ;

    ImageView imgv_connect_led ;
    ImageButton btn_manual_control_arm, btn_manual_control_base ,btn_manual_control_lt_wheels ,btn_manual_control_rt_wheels
            , btn_manual_control_catch,btn_manual_control_cut , btn_manual_control_head_lr,btn_manual_control_head_ud ,btn_manual_control_updown
            , btn_manual_control_backward, btn_manual_control_forward , btn_exit_dialog;

    String[] Selected_Motor_Manual_Control ;
    String[][] Motors ;
    String[] Tables ;
    String[] MOTOR_ARM = new String[4];
    String[] MOTOR_BASE = new String[4];
    String[] MOTOR_CAR_LT_WHEELS = new String[4];
    String[] MOTOR_CAR_RT_WHEELS = new String[4];
    String[] MOTOR_CATCH = new String[4];
    String[] MOTOR_CUT = new String[4];
    String[] MOTOR_HEAD_LR = new String[4];
    String[] MOTOR_HEAD_UD = new String[4];
    String[] MOTOR_UP_DOWN = new String[4];

    private final Handler handlerStatusCheck = new Handler();
    public Handler mHandler;

    boolean State_Zero = true;
    boolean State_One = true;
    boolean State_Tow = true;
    boolean First_lunch_Zero = true;
    boolean First_lunch_One = true;
    boolean First_lunch_Tow = true;

    String MODULE_MAC ;

    DbConnction db ;
    Cursor c = null;

    private final int STATUS_CHECK_INTERVAL = 500;

    boolean show_infobar = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);

        MODULE_MAC = getIntent().getStringExtra("BLUETOOTH_MAC");


        imgv_robot_parts = findViewById(R.id.imgv_robot_parts);
        txtv_motor_name = findViewById(R.id.txtv_motor_name);
        txtv_motor_description = findViewById(R.id.txtv_motor_description);
        btn_manual_control_arm =findViewById(R.id.btn_manual_control_arm);
        btn_manual_control_base = findViewById(R.id.btn_manual_control_base);
        btn_manual_control_lt_wheels = findViewById(R.id.btn_manual_control_lt_wheels);
        btn_manual_control_rt_wheels = findViewById(R.id.btn_manual_control_rt_wheels);
        btn_manual_control_catch = findViewById(R.id.btn_manual_control_catch);
        btn_manual_control_cut = findViewById(R.id.btn_manual_control_cut);
        btn_manual_control_head_lr = findViewById(R.id.btn_manual_control_head_lr);
        btn_manual_control_head_ud = findViewById(R.id.btn_manual_control_head_ud);
        btn_manual_control_updown = findViewById(R.id.btn_manual_control_updown);
        btn_manual_control_backward = findViewById(R.id.btn_manual_control_backward);
        btn_manual_control_forward = findViewById(R.id.btn_manual_control_forward);
        btn_exit_dialog = findViewById(R.id.btn_exit_dialog);
        txtv_connecting_lable = findViewById(R.id.txtv_connecting_lable);
        imgv_connect_led =findViewById(R.id.imgv_connect_led);


        db = new DbConnction(Manual_Control_Activity.this);
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

        Tables = new String[]{"motor_base" , "motor_head_lr", "motor_arm", "motor_updown", "motor_head_ud", "motor_catch", "motor_cut", "motor_car_lt_wheels", "motor_car_rt_wheels"};
        Motors = new String[][]{MOTOR_BASE , MOTOR_HEAD_LR , MOTOR_ARM , MOTOR_UP_DOWN , MOTOR_HEAD_UD , MOTOR_CATCH , MOTOR_CUT , MOTOR_CAR_LT_WHEELS , MOTOR_CAR_RT_WHEELS};

        for(int Tables_Count = 0 ; Tables_Count < 9 ; Tables_Count ++){

            c = db.Row_Query(Tables[Tables_Count], "id_", "1");
            c.moveToFirst();

            for(int Record_Count = 0 ; Record_Count < 4 ; Record_Count ++){
                Motors[Tables_Count][Record_Count] = c.getString(Record_Count+1) ;

                System.out.println(Motors[Tables_Count][Record_Count]);

            }
        }








        btn_manual_control_arm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_Motor_Manual_Control = Motors[2];
                imgv_robot_parts.setImageResource(R.drawable.ic_motor_arm);
                txtv_motor_name.setText("Forward and Backward ARM");
                txtv_motor_description.setText("Responsible for extending the robot's head");
            }
        });

        btn_manual_control_base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_Motor_Manual_Control = Motors[0];
                imgv_robot_parts.setImageResource(R.drawable.ic_motor_base);
                txtv_motor_name.setText("Robot Base");
                txtv_motor_description.setText("Responsible for moving the robot base 180 degrees");
            }
        });

        btn_manual_control_lt_wheels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_Motor_Manual_Control = Motors[7];
                imgv_robot_parts.setImageResource(R.drawable.ic_motor_car_lt_wheels);
                txtv_motor_name.setText("Wheels Left Side");
                txtv_motor_description.setText("Moving the robot's wheels on the left side");
            }
        });

        btn_manual_control_rt_wheels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_Motor_Manual_Control = Motors[8];
                imgv_robot_parts.setImageResource(R.drawable.ic_motor_car_rt_wheels);
                txtv_motor_name.setText("Wheels Right Side");
                txtv_motor_description.setText("Move the robot's wheels on the right side");
            }
        });

        btn_manual_control_catch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_Motor_Manual_Control = Motors[5];
                imgv_robot_parts.setImageResource(R.drawable.ic_motor_catch);
                txtv_motor_name.setText("Catcher");
                txtv_motor_description.setText("Its function is to catch the fruits after the searching process");
            }
        });

        btn_manual_control_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_Motor_Manual_Control = Motors[6];
                imgv_robot_parts.setImageResource(R.drawable.ic_motor_cut);
                txtv_motor_name.setText("Cutter");
                txtv_motor_description.setText("It is used to cut the tree branch holding the fruit");
            }
        });

        btn_manual_control_head_lr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_Motor_Manual_Control = Motors[1];
                imgv_robot_parts.setImageResource(R.drawable.ic_motor_head_lr);
                txtv_motor_name.setText("Head Right and Left");
                txtv_motor_description.setText("A motor that moves the robot's head to the right and left");
            }
        });

        btn_manual_control_head_ud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_Motor_Manual_Control = Motors[4];
                imgv_robot_parts.setImageResource(R.drawable.ic_motor_head_ud);
                txtv_motor_name.setText("Head Up and Down");
                txtv_motor_description.setText("A motor that moves the robot's head up and down");
            }
        });

        btn_manual_control_updown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Selected_Motor_Manual_Control = Motors[3];
                imgv_robot_parts.setImageResource(R.drawable.ic_motor_updown);
                txtv_motor_name.setText("Up and Down Arm");
                txtv_motor_description.setText("It is used to raise and lower the main arm of the robot");
            }
        });

        btn_exit_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndConnection();
                Intent intent = new Intent(Manual_Control_Activity.this, Choose_Control_Options_Activity.class);
                startActivity(intent);
                finish();

            }
        });

        btn_manual_control_backward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    SEND_COMMAND(Selected_Motor_Manual_Control[0]);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    SEND_COMMAND(Selected_Motor_Manual_Control[1]);
                }

                return false;
            }
        });

        btn_manual_control_forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    SEND_COMMAND(Selected_Motor_Manual_Control[2]);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    SEND_COMMAND(Selected_Motor_Manual_Control[1]);
                }

                return false;
            }
        });

        initiateBluetoothProcess();

    }






    public void SEND_COMMAND(String Commend){
        ((ApplicationEx)getApplication()).Start_Stop_Manual_control(0);
        if(((ApplicationEx)getApplication()).writeBt(Commend.getBytes(StandardCharsets.UTF_8))){
            show_infobar = true ;
        }
        else if (show_infobar == true) {
           // show_infobar = false ;
            SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
            snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(), findViewById(android.R.id.content).getRootView(), Manual_Control_Activity.this,"Something Went Wrong");
        }


    }


    private void EndConnection() {
        ((ApplicationEx)getApplication()).Start_Stop_Manual_control(1);
        handlerStatusCheck.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    public void initiateBluetoothProcess(){
        ((ApplicationEx)getApplication()).mBtEngine.SET_MAC(MODULE_MAC);
        ((ApplicationEx)getApplication()).Start_Stop_Manual_control(0);

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String txt = (String)msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(txt);                                      // append string
                String sbprint = sb.substring(0, sb.length());            // extract string
                sb.delete(0, sb.length());
                final String finalSbprint = sb.append(sbprint).toString();
                System.out.println(finalSbprint);
                // txtv_command_history.append(finalSbprint);

            }
        };
        ((ApplicationEx)getApplication()).mBtEngine.SET_HANDLER(mHandler);


        handlerStatusCheck.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(((ApplicationEx)getApplication()).mBtEngine.getState() == 0 ){
                    if(State_Zero) {
                        txtv_connecting_lable.setText("Connection Lost");
                        imgv_connect_led.setBackgroundResource(R.drawable.ic_red_dot_not_connected);
                        if(!First_lunch_Zero) {
                            SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
                            snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(), findViewById(android.R.id.content).getRootView(), Manual_Control_Activity.this, "Connection Lost");
                        }
                        State_Zero = false ;
                        State_One = true ;
                        State_Tow = true ;
                        First_lunch_Zero = false ;
                    }
                }
                if(((ApplicationEx)getApplication()).mBtEngine.getState() == 1 ){
                    if(State_One) {
                        txtv_connecting_lable.setText("Trying to Connect");
                        imgv_connect_led.setBackgroundResource(R.drawable.ic_red_dot_not_connected);
                        if(!First_lunch_One) {
                            SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
                            snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(), findViewById(android.R.id.content).getRootView(), Manual_Control_Activity.this, "Trying to Connect");
                        }
                        State_Zero = true ;
                        State_One = false ;
                        State_Tow = true ;
                        First_lunch_One = false ;
                    }
                }
                if(((ApplicationEx)getApplication()).mBtEngine.getState() == 2 ){
                    if(State_Tow) {
                        txtv_connecting_lable.setText("Connected");
                        imgv_connect_led.setBackgroundResource(R.drawable.ic_green_dot_connected);
                        if(!First_lunch_Tow) {
                            SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
                            snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(), findViewById(android.R.id.content).getRootView(), Manual_Control_Activity.this, "Connected");
                        }
                        State_Zero = true ;
                        State_One = true ;
                        State_Tow = false ;
                        First_lunch_Tow = false ;
                    }
                }
                handlerStatusCheck.postDelayed(this, STATUS_CHECK_INTERVAL);
            }
        }, STATUS_CHECK_INTERVAL);
    }



}