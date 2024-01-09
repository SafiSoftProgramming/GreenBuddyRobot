package safisoft.greenbuddyrobot.java;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import safisoft.greenbuddyrobot.R;


public class AppPermissionsDialog extends Dialog implements
        View.OnClickListener {

    public ImageButton btn_ok;
    public Activity c;


    public AppPermissionsDialog(@NonNull Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_permissions_dialog);

        btn_ok = findViewById(R.id.btn_ok_info);
        btn_ok.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

    }
}
