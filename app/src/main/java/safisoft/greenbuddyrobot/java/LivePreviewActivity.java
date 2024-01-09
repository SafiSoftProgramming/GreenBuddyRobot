/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package safisoft.greenbuddyrobot.java;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import safisoft.greenbuddyrobot.CameraSource;
import safisoft.greenbuddyrobot.CameraSourcePreview;
import safisoft.greenbuddyrobot.Choose_Control_Options_Activity;
import safisoft.greenbuddyrobot.DbConnction;
import safisoft.greenbuddyrobot.GraphicOverlay;
import safisoft.greenbuddyrobot.ManualControlDialog;
import safisoft.greenbuddyrobot.R;
import safisoft.greenbuddyrobot.SettingsAndCommandsActivity;
import safisoft.greenbuddyrobot.SnackBarInfoControl;
import safisoft.greenbuddyrobot.java.objectdetector.ObjectDetectorProcessor;
import safisoft.greenbuddyrobot.preference.PreferenceUtils;


/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnItemSelectedListener, CompoundButton.OnCheckedChangeListener  {

  ObjectDetectorProcessor objectDetectorProcessor ;
  private static final String OBJECT_DETECTION_CUSTOM = "Custom Object Detection";
  private static final String TAG = "LivePreviewActivity";
  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = OBJECT_DETECTION_CUSTOM;

  String MODULE_MAC ;
  public final static int REQUEST_ENABLE_BT = 1;
  private Handler Send_Handler;
  DbConnction db ;
  Cursor c = null;
  TextView txtv_command_history ;
  TextView txtv_connecting_lable ;
  ImageView imgv_connect_led ;
  ImageView imgv_count_down ;

  ToggleButton btn_camera_switch ;
  ImageButton btn_settings_and_commands ;
  ImageButton btn_scroll_txtv ;
  ImageButton btn_clean_txtv ;
  ImageButton btn_start ;
  ImageButton btn_pause ;
  ImageButton btn_stop ;



  ImageButton btn_manual_control_dialog ;

  boolean show_infobar = true ;

  public Handler mHandler;
  private final int STATUS_CHECK_INTERVAL = 500;
  private final Handler handlerStatusCheck = new Handler();
  boolean State_Zero = true;
  boolean State_One = true;
  boolean State_Tow = true;
  boolean First_lunch_Zero = true;
  boolean First_lunch_One = true;
  boolean First_lunch_Tow = true;

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

  ManualControlDialog ManualControlDialog;
  String[] Selected_Motor_Manual_Control ;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
    setContentView(R.layout.activity_vision_live_preview);



     ObjectDetectorOptions objectDetectorOptions = PreferenceUtils.getObjectDetectorOptionsForLivePreview(this);
     objectDetectorProcessor = new ObjectDetectorProcessor(LivePreviewActivity.this,objectDetectorOptions);



    MODULE_MAC = getIntent().getStringExtra("BLUETOOTH_MAC");


    db = new DbConnction(LivePreviewActivity.this);
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


    preview = findViewById(R.id.preview_view);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    Spinner spinner = findViewById(R.id.spinner);
    List<String> options = new ArrayList<>();

    options.add(OBJECT_DETECTION_CUSTOM);
    options.add(OBJECT_DETECTION_CUSTOM);

    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(dataAdapter);
    spinner.setOnItemSelectedListener(this);



    txtv_command_history = findViewById(R.id.txtv_command_history);
    txtv_connecting_lable = findViewById(R.id.txtv_connecting_lable);
    imgv_connect_led =findViewById(R.id.imgv_connect_led);
    btn_settings_and_commands = findViewById(R.id.btn_settings_and_commands);
    btn_camera_switch = findViewById(R.id.btn_camera_switch);
    btn_scroll_txtv =findViewById(R.id.btn_scroll_txtv);
    btn_clean_txtv = findViewById(R.id.btn_clean_txtv);
    btn_stop = findViewById(R.id.btn_stop);
    btn_pause = findViewById(R.id.btn_pause);
    btn_start = findViewById(R.id.btn_start);
    imgv_count_down =findViewById(R.id.imgv_count_down);
    btn_manual_control_dialog =findViewById(R.id.btn_manual_control_dialog);





    btn_camera_switch.setOnCheckedChangeListener(this);

    btn_settings_and_commands.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(LivePreviewActivity.this, SettingsAndCommandsActivity.class);
        startActivity(intent);
        finish();
      }
    });

      btn_stop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        EndConnection();
        Intent intent = new Intent(LivePreviewActivity.this, Choose_Control_Options_Activity.class);
        startActivity(intent);
        finish();
        }
      });

    btn_clean_txtv.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
         txtv_command_history.setText("");
       }
    });

    btn_scroll_txtv.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
         String txtv = txtv_command_history.getText().toString();
         txtv_command_history.setText("");
         txtv_command_history.append(txtv);
       }
     });

    btn_start.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        new CountDownTimer(3500, 1000) {
          public void onTick(long millisUntilFinished) {
            if(millisUntilFinished / 1000 == 3){imgv_count_down.setBackgroundResource(R.drawable.ic_num_three);}
            if(millisUntilFinished / 1000 == 2){imgv_count_down.setBackgroundResource(R.drawable.ic_num_two);}
            if(millisUntilFinished / 1000 == 1){imgv_count_down.setBackgroundResource(R.drawable.ic_num_one);}
          }
          public void onFinish() {
            imgv_count_down.setBackgroundResource(0);

            SEARCH_COMMANDS();

          }
        }.start();
      }
    });

    btn_manual_control_dialog.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ManualControlDialog = new ManualControlDialog(LivePreviewActivity.this);
        ManualControlDialog.show();
        ManualControlDialog.setCanceledOnTouchOutside(false);
        ManualControlDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Selected_Motor_Manual_Control = Motors[6];
        ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_cut);
        ManualControlDialog.txtv_motor_name.setText("Cutter");
        ManualControlDialog.txtv_motor_description.setText("It is used to cut the tree branch holding the fruit");



        ManualControlDialog.btn_manual_control_arm.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Selected_Motor_Manual_Control = Motors[2];
            ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_arm);
            ManualControlDialog.txtv_motor_name.setText("Forward and Backward ARM");
            ManualControlDialog.txtv_motor_description.setText("Responsible for extending the robot's head");
          }
        });

        ManualControlDialog.btn_manual_control_base.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Selected_Motor_Manual_Control = Motors[0];
            ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_base);
            ManualControlDialog.txtv_motor_name.setText("Robot Base");
            ManualControlDialog.txtv_motor_description.setText("Responsible for moving the robot base 180 degrees");
          }
        });

        ManualControlDialog.btn_manual_control_lt_wheels.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Selected_Motor_Manual_Control = Motors[7];
            ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_car_lt_wheels);
            ManualControlDialog.txtv_motor_name.setText("Wheels Left Side");
            ManualControlDialog.txtv_motor_description.setText("Moving the robot's wheels on the left side");
          }
        });

        ManualControlDialog.btn_manual_control_rt_wheels.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Selected_Motor_Manual_Control = Motors[8];
            ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_car_rt_wheels);
            ManualControlDialog.txtv_motor_name.setText("Wheels Right Side");
            ManualControlDialog.txtv_motor_description.setText("Move the robot's wheels on the right side");
          }
        });

        ManualControlDialog.btn_manual_control_catch.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Selected_Motor_Manual_Control = Motors[5];
            ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_catch);
            ManualControlDialog.txtv_motor_name.setText("Catcher");
            ManualControlDialog.txtv_motor_description.setText("Its function is to catch the fruits after the searching process");
          }
        });

        ManualControlDialog.btn_manual_control_cut.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Selected_Motor_Manual_Control = Motors[6];
            ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_cut);
            ManualControlDialog.txtv_motor_name.setText("Cutter");
            ManualControlDialog.txtv_motor_description.setText("It is used to cut the tree branch holding the fruit");
          }
        });

        ManualControlDialog.btn_manual_control_head_lr.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Selected_Motor_Manual_Control = Motors[1];
            ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_head_lr);
            ManualControlDialog.txtv_motor_name.setText("Head Right and Left");
            ManualControlDialog.txtv_motor_description.setText("A motor that moves the robot's head to the right and left");
          }
        });

        ManualControlDialog.btn_manual_control_head_ud.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Selected_Motor_Manual_Control = Motors[4];
            ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_head_ud);
            ManualControlDialog.txtv_motor_name.setText("Head Up and Down");
            ManualControlDialog.txtv_motor_description.setText("A motor that moves the robot's head up and down");
          }
        });

        ManualControlDialog.btn_manual_control_updown.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Selected_Motor_Manual_Control = Motors[3];
            ManualControlDialog.imgv_robot_parts.setImageResource(R.drawable.ic_motor_updown);
            ManualControlDialog.txtv_motor_name.setText("Up and Down Arm");
            ManualControlDialog.txtv_motor_description.setText("It is used to raise and lower the main arm of the robot");
          }
        });

        ManualControlDialog.btn_exit_dialog.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            ManualControlDialog.dismiss();
          }
        });

        ManualControlDialog.btn_manual_control_backward.setOnTouchListener(new View.OnTouchListener() {
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

        ManualControlDialog.btn_manual_control_forward.setOnTouchListener(new View.OnTouchListener() {
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





      }
    });



    createCameraSource(selectedModel);
    initiateBluetoothProcess();
    Send_Handler = new Handler();




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





  }




  // Building the Search Movement
  String Search_Side_Command = "F";
  int[] Motor_NUM_To_Move_Search = {        0          ,          1            , 2 , 3 , 4 , 2 , 3 , 4 , 2 , 3 , 4 , 2 , 7 , 8 , 2 , 3 , 4 , 2 , 3 , 4 , 2 , 3 , 4 , 2 };
  String[] Motor_Direction_To_Move_Search = {Search_Side_Command,Search_Side_Command,    "F","F","B","B","F","B","F","F","B","B","F","F","F","B","F","B","B","F","F","B","F","B"};
  int Count_Search = 0 ;

  String Item_Name = "" ;

  boolean Search_Pose_Started = false ;

  CountDownTimer Search_CountDownTimer ;

  public void SEARCH_COMMANDS() {
      String Moving_Direction_Command = Motor_Direction_To_Move_Search[Count_Search];
      String[] Selected_Motor = Motors[ Motor_NUM_To_Move_Search[Count_Search]];
        int Steps = Integer.parseInt(Selected_Motor[3]);
        if (Moving_Direction_Command.equals("F")) {
          Moving_Direction_Command = Selected_Motor[0];
        }
        if (Moving_Direction_Command.equals("B")) {
          Moving_Direction_Command = Selected_Motor[2];
        }
        SEND_COMMAND(Moving_Direction_Command);

    Search_CountDownTimer = new CountDownTimer(Steps, 500) {
          public void onTick(long millisUntilFinished) {

            if(Search_Pose_Started  && Item_Name.equals("Food")){
            //  Toast.makeText(LivePreviewActivity.this, "Start Catch", Toast.LENGTH_SHORT).show();
              Search_CountDownTimer.cancel();
              SEND_COMMAND(Selected_Motor[1]);  //Stop

              //MOTOR_UP_DOWN         MOTOR_ARM              MOTOR_HEAD_LR          MOTOR_BASE             MOTOR_CATCH
              Catch_Steps[0]= 5000 ;  Catch_Steps[1]= 500 ; Catch_Steps[2]= 1000 ; Catch_Steps[3]= 4500 ; Catch_Steps[4]= 5000 ;
              CATCH_COMMANDS();



            }
            db.Update_Command_value(Tables[Motor_NUM_To_Move_Search[Count_Search]], "position", Long.toString(millisUntilFinished));
          }
          public void onFinish() {
            SEND_COMMAND(Selected_Motor[1]);  //Stop
            if(Count_Search == 1){Search_Pose_Started = true;   startRepeatingTask();}
            Count_Search++ ;
            if(Count_Search <= Motor_Direction_To_Move_Search.length -1) {
              SEARCH_COMMANDS();
            }
            else {
              Count_Search = 0 ;
            }
            }
          }.start();
  }

////////////////////////////////////////////////////////////////////////////////////////////////////

  CountDownTimer Catch_CountDownTimer;

                                    //MOTOR_UP_DOWN , MOTOR_ARM , MOTOR_HEAD_LR , MOTOR_BASE , MOTOR_CATCH
  int[] Motor_NUM_To_Move_Catch = {          3 , 2 , 1 , 0 , 5 };
  String[] Motor_Direction_To_Move_Catch = {"F","F","B","F","F"};
  int Count_Catch = 0 ;

  int[] Catch_Steps = new int[5] ;

  public void CATCH_COMMANDS() {
    String Moving_Direction_Command = Motor_Direction_To_Move_Catch[Count_Catch];
    String[] Selected_Motor = Motors[ Motor_NUM_To_Move_Catch[Count_Catch]];
    if (Moving_Direction_Command.equals("F")) {
      Moving_Direction_Command = Selected_Motor[0];
    }
    if (Moving_Direction_Command.equals("B")) {
      Moving_Direction_Command = Selected_Motor[2];
    }
    SEND_COMMAND(Moving_Direction_Command);
    Catch_CountDownTimer = new CountDownTimer(Catch_Steps[Count_Catch], 500) {
      public void onTick(long millisUntilFinished) {
      }
      public void onFinish() {
        SEND_COMMAND(Selected_Motor[1]);  //Stop
        Count_Catch++ ;
        if(Count_Catch <= Motor_Direction_To_Move_Catch.length -1) {
          SEARCH_COMMANDS();
        }
        else {
          Count_Catch = 0 ;
        }
      }
    }.start();
  }
////////////////////////////////////////////////////////////////////////////////////////////////////





    Runnable mStatusChecker = new Runnable() {
      @Override
      public void run() {
        try {

          System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu              " + objectDetectorProcessor.Get_Name());
          System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu              " + objectDetectorProcessor.Get_Top());
          System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu              " + objectDetectorProcessor.Get_Bottom());
          System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu              " + objectDetectorProcessor.Get_Left());
          System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu              " + objectDetectorProcessor.Get_Right());
          System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu              " + objectDetectorProcessor.Get_Width());
          System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu              " + objectDetectorProcessor.Get_Height());
          System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu              " + objectDetectorProcessor.Get_CenterX());
          System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu              " + objectDetectorProcessor.Get_CenterY());

          if(objectDetectorProcessor.Get_Name() != null && Search_Pose_Started ){
            Item_Name = objectDetectorProcessor.Get_Name();
          }

        } finally {
          Send_Handler.postDelayed(mStatusChecker, 250);
        }
      }
    };

    void startRepeatingTask() {
      mStatusChecker.run();
    }

    void stopRepeatingTask() {
      Send_Handler.removeCallbacks(mStatusChecker);
    }


  @Override
  public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent.getItemAtPosition(pos).toString();
    Log.d(TAG, "Selected model: " + selectedModel);
    preview.stop();
    createCameraSource(selectedModel);
    startCameraSource();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Log.d(TAG, "Set facing");
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      }
    }
    preview.stop();
    startCameraSource();
  }

  private void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try {
      switch (model) {
        case OBJECT_DETECTION_CUSTOM:
          Log.i(TAG, "Using Custom Object Detector Processor");
          LocalModel localModel =
                  new LocalModel.Builder()
                          .setAssetFilePath("custom_models/object_labeler.tflite")
                          .build();
          CustomObjectDetectorOptions customObjectDetectorOptions =
                  PreferenceUtils.getCustomObjectDetectorOptionsForLivePreview(this, localModel);
          cameraSource.setMachineLearningFrameProcessor(
                  new ObjectDetectorProcessor(this, customObjectDetectorOptions));
          break;
      }
    } catch (RuntimeException e) {
      Log.e(TAG, "Can not create image processor: " + model, e);
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getMessage(),
              Toast.LENGTH_LONG)
          .show();
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    createCameraSource(selectedModel);
    startCameraSource();
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
 //   stopRepeatingTask();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
      initiateBluetoothProcess();
    }
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
        if(txtv_command_history.getLineCount() > 500){
          txtv_command_history.setText("");
          txtv_command_history.append("> "+"Auto Screen Cleaner"+"\n");
         System.out.println("Auto Screen Cleaner");
        }
        if(((ApplicationEx)getApplication()).mBtEngine.getState() == 0 ){
          if(State_Zero) {
            txtv_connecting_lable.setText("Connection Lost");
            txtv_command_history.append( "> "+"Connection Lost"+"\n");
            imgv_connect_led.setBackgroundResource(R.drawable.ic_red_dot_not_connected);
            if(!First_lunch_Zero) {
              SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
              snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(), findViewById(android.R.id.content).getRootView(), LivePreviewActivity.this, "Connection Lost");
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
            txtv_command_history.append( "> "+"Trying to Connect"+"\n");
            imgv_connect_led.setBackgroundResource(R.drawable.ic_red_dot_not_connected);
            if(!First_lunch_One) {
              SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
              snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(), findViewById(android.R.id.content).getRootView(), LivePreviewActivity.this, "Trying to Connect");
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
            txtv_command_history.append( "> "+"Connected"+"\n");
            imgv_connect_led.setBackgroundResource(R.drawable.ic_green_dot_connected);
            if(!First_lunch_Tow) {
              SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
              snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(), findViewById(android.R.id.content).getRootView(), LivePreviewActivity.this, "Connected");
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


  private void EndConnection() {
    ((ApplicationEx)getApplication()).Start_Stop_Manual_control(1);
    handlerStatusCheck.removeCallbacksAndMessages(null);
    mHandler.removeCallbacksAndMessages(null);
  }


  public void SEND_COMMAND(String Commend){
    ((ApplicationEx)getApplication()).Start_Stop_Manual_control(0);
    if(((ApplicationEx)getApplication()).writeBt(Commend.getBytes(StandardCharsets.UTF_8))){
      txtv_command_history.append( "> "+Commend+"\n");
      show_infobar = true ;
    }
    else if (show_infobar == true) {
      show_infobar = false ;
      SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
      snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(), findViewById(android.R.id.content).getRootView(), LivePreviewActivity.this,"Something Went Wrong");
    }


  }




  @Override
  public void onBackPressed() {
    EndConnection();
   Intent intent = new Intent(LivePreviewActivity.this, Choose_Control_Options_Activity.class);
   startActivity(intent);
   finish();
  }






}
