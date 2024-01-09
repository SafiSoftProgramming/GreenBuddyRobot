package safisoft.greenbuddyrobot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import safisoft.greenbuddyrobot.java.AppPermissionsDialog;

@RequiresApi(api = Build.VERSION_CODES.S)
public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST= 112;

    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
            //  android.Manifest.permission.ACCESS_COARSE_LOCATION,
            //  android.Manifest.permission.CHANGE_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);// hide notification bar

        if (Build.VERSION.SDK_INT >= 23) {

            if (!hasPermissions(this, PERMISSIONS[0]) &&
                    !hasPermissions(this, PERMISSIONS[1]) &&
                    !hasPermissions(this, PERMISSIONS[2]) &&
                    !hasPermissions(this, PERMISSIONS[3]) &&
                    !hasPermissions(this, PERMISSIONS[4]) &&
                    !hasPermissions(this, PERMISSIONS[5])) {


            AppPermissionsDialog appPermissionsDialog = new AppPermissionsDialog(SplashActivity.this);
            appPermissionsDialog.show();
            appPermissionsDialog.setCanceledOnTouchOutside(false);
            appPermissionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

          appPermissionsDialog.btn_ok.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, REQUEST);
                  appPermissionsDialog.dismiss();
              }
          });
            }
            else {
                callActivity();
            }

        }
        else {
            callActivity();
        }



    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {


                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[2] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[3] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[4] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[5] == PackageManager.PERMISSION_GRANTED) {

                    callActivity();
                } else {
                    SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
                    snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(), findViewById(android.R.id.content).getRootView(), SplashActivity.this, "Please allow Bluetooth permission. ROBOBOY would not work.");
                }

            }
        }
    }


    public void callActivity() {
        new CountDownTimer(4500, 1000) {
            public void onTick(long millisUntilFinished) { }
            public void onFinish() {
                Intent intent = new Intent(SplashActivity.this, Choose_Control_Options_Activity.class);
                intent.putExtra("AD_STATE","false" );
                startActivity(intent);
                finish();
            }
        }.start();
    }



}
