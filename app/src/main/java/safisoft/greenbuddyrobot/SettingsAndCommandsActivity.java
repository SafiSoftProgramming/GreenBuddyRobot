package safisoft.greenbuddyrobot;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import java.io.IOException;

import safisoft.greenbuddyrobot.java.LivePreviewActivity;
import safisoft.greenbuddyrobot.preference.SettingsActivity;

public class SettingsAndCommandsActivity extends AppCompatActivity {


    DbConnction db ;
    Cursor c = null;


    TextView txtv_motor_name , txtv_motor_description ;
    ImageView imgv_robot_parts ;
    EditText edtxt_forward_command , edtxt_stop_command , edtxt_backward_command , edtxt_steps ;
    ImageButton btn_back_part , btn_next_part , btn_more_settings , btn_ok ;
    EditText edtxt_bluetooth_mac, edtxt_catch_command;

    int Table_cont = 0 ;


    boolean Edit_Mac = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_and_commands);

        txtv_motor_name = findViewById(R.id.txtv_motor_name);
        txtv_motor_description = findViewById(R.id.txtv_motor_description);
        imgv_robot_parts = findViewById(R.id.imgv_robot_parts);
        edtxt_forward_command = findViewById(R.id.edtxt_forward_command);
        edtxt_stop_command = findViewById(R.id.edtxt_stop_command);
        edtxt_backward_command = findViewById(R.id.edtxt_backward_command);
        btn_back_part = findViewById(R.id.btn_back_part);
        btn_next_part = findViewById(R.id.btn_next_part);
        btn_more_settings = findViewById(R.id.btn_more_settings);
        btn_ok = findViewById(R.id.btn_ok);
        edtxt_bluetooth_mac = findViewById(R.id.edtxt_bluetooth_mac);
        edtxt_catch_command = findViewById(R.id.edtxt_catch_command);
        edtxt_steps = findViewById(R.id.edtxt_steps);


        db = new DbConnction(SettingsAndCommandsActivity.this);
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
        String MAC = c.getString(c.getColumnIndexOrThrow("mac")) ;
        if(MAC.equals("")){
            edtxt_bluetooth_mac.setText("Empty");
        }else {edtxt_bluetooth_mac.setText(MAC);}


        // Motor catch sensor
        c = db.Row_Query("sensor_catch", "id_", "1");
        c.moveToFirst();
        String Sensor_Command = c.getString(c.getColumnIndexOrThrow("sensor_command")) ;
        if(Sensor_Command.equals("")){
            edtxt_catch_command.setText("Empty");
        }else {edtxt_catch_command.setText(Sensor_Command);}



        String[] Tables = {"motor_arm","motor_base","motor_car_lt_wheels","motor_car_rt_wheels","motor_catch","motor_cut","motor_head_lr","motor_head_ud","motor_updown"};

        // Load one of the Motors Command for the start
        HANDLE_COMMAND_DATA(Tables[Table_cont]);

        btn_next_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Table_cont ++ ;
                if(Table_cont > 8){Table_cont = 0; }
                HANDLE_COMMAND_DATA(Tables[Table_cont]);
            }
        });

        btn_back_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Table_cont -- ;
                if(Table_cont < 0){Table_cont = 8; }
                HANDLE_COMMAND_DATA(Tables[Table_cont]);
            }
        });


        edtxt_forward_command.setEnabled(true);
        edtxt_stop_command.setEnabled(true);
        edtxt_backward_command.setEnabled(true);
        edtxt_bluetooth_mac.setEnabled(true);
        edtxt_catch_command.setEnabled(true);
        edtxt_steps.setEnabled(true);


        edtxt_forward_command.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

            db.Update_Command_value(Tables[Table_cont],"forward",edtxt_forward_command.getText().toString());

            }
        });

        edtxt_stop_command.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

                db.Update_Command_value(Tables[Table_cont],"stop",edtxt_stop_command.getText().toString());

            }
        });

        edtxt_backward_command.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

                db.Update_Command_value(Tables[Table_cont],"backward",edtxt_backward_command.getText().toString());

            }
        });

        edtxt_steps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

                db.Update_Command_value(Tables[Table_cont],"steps",edtxt_steps.getText().toString());

            }
        });

        edtxt_bluetooth_mac.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

                db.Update_Command_value("bluetooth","mac",edtxt_bluetooth_mac.getText().toString());
                Edit_Mac = true ;

            }
        });

        edtxt_catch_command.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {


                db.Update_Command_value("sensor_catch","sensor_command",edtxt_catch_command.getText().toString());
        
            }
        });


        btn_more_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
               intent.putExtra(
                       SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW);
               startActivity(intent);
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Edit_Mac) {
                    Intent intent = new Intent(SettingsAndCommandsActivity.this, FindBluetoothActivity.class);
                    intent.putExtra("CONTROL_MODE", "AUTO");
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(SettingsAndCommandsActivity.this, LivePreviewActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });






    }


    private void HANDLE_COMMAND_DATA(String Motor_Name_DB){

        c = db.Row_Query(Motor_Name_DB, "id_", "1");
        c.moveToFirst();

        String Forward , Stop , Backward , Steps ;
        Forward = c.getString(c.getColumnIndexOrThrow("forward")) ;
        Stop = c.getString(c.getColumnIndexOrThrow("stop")) ;
        Backward = c.getString(c.getColumnIndexOrThrow("backward")) ;
        Steps = c.getString(c.getColumnIndexOrThrow("steps")) ;


        if(Forward.equals("")){
            edtxt_forward_command.setText("Empty");
        }else {edtxt_forward_command.setText(Forward);}

        if(Stop.equals("")){
            edtxt_stop_command.setText("Empty");
        }else {edtxt_stop_command.setText(Stop);}

        if(Backward.equals("")){
            edtxt_backward_command.setText("Empty");
        }else {edtxt_backward_command.setText(Backward);}

       if(Steps.equals("")){
            edtxt_steps.setText("Empty");
       }else {edtxt_steps.setText(Steps);}




        switch(Motor_Name_DB) {
            case "motor_arm":

                imgv_robot_parts.setImageResource(R.drawable.ic_motor_arm);
                txtv_motor_name.setText("Forward and Backward ARM");
                txtv_motor_description.setText("Responsible for extending the robot's head");

                break;

            case "motor_base":

                imgv_robot_parts.setImageResource(R.drawable.ic_motor_base);
                txtv_motor_name.setText("Robot Base");
                txtv_motor_description.setText("Responsible for moving the robot base 180 degrees");

                break;

            case "motor_car_lt_wheels":

                imgv_robot_parts.setImageResource(R.drawable.ic_motor_car_lt_wheels);
                txtv_motor_name.setText("Wheels Left Side");
                txtv_motor_description.setText("Moving the robot's wheels on the left side");

                break;

            case "motor_car_rt_wheels":

                imgv_robot_parts.setImageResource(R.drawable.ic_motor_car_rt_wheels);
                txtv_motor_name.setText("Wheels Right Side");
                txtv_motor_description.setText("Move the robot's wheels on the right side");

                break;

            case "motor_catch":

                imgv_robot_parts.setImageResource(R.drawable.ic_motor_catch);
                txtv_motor_name.setText("Catcher");
                txtv_motor_description.setText("Its function is to catch the fruits after the searching process");

                break;

            case "motor_cut":

                imgv_robot_parts.setImageResource(R.drawable.ic_motor_cut);
                txtv_motor_name.setText("Cutter");
                txtv_motor_description.setText("It is used to cut the tree branch holding the fruit");

                break;

            case "motor_head_lr":

                imgv_robot_parts.setImageResource(R.drawable.ic_motor_head_lr);
                txtv_motor_name.setText("Head Right and Left");
                txtv_motor_description.setText("A motor that moves the robot's head to the right and left");

                break;

            case "motor_head_ud":

                imgv_robot_parts.setImageResource(R.drawable.ic_motor_head_ud);
                txtv_motor_name.setText("Head Up and Down");
                txtv_motor_description.setText("A motor that moves the robot's head up and down");

                break;

            case "motor_updown":

                imgv_robot_parts.setImageResource(R.drawable.ic_motor_updown);
                txtv_motor_name.setText("Up and Down Arm");
                txtv_motor_description.setText("It is used to raise and lower the main arm of the robot");

                break;

            default:
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsAndCommandsActivity.this, LivePreviewActivity.class);
        startActivity(intent);
        finish();
    }


}