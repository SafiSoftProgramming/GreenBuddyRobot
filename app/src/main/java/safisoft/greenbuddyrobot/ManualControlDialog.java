package safisoft.greenbuddyrobot;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ManualControlDialog extends Dialog implements
        View.OnClickListener, View.OnTouchListener {



    public ImageView imgv_robot_parts ;
    public TextView txtv_motor_name , txtv_motor_description ;


    public ImageButton btn_manual_control_arm, btn_manual_control_base ,btn_manual_control_lt_wheels ,btn_manual_control_rt_wheels
            , btn_manual_control_catch,btn_manual_control_cut , btn_manual_control_head_lr,btn_manual_control_head_ud ,btn_manual_control_updown
            , btn_manual_control_backward, btn_manual_control_forward , btn_exit_dialog;

    public Activity c;

    public ManualControlDialog(@NonNull Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.manual_control_dialog);

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


        btn_manual_control_arm.setOnClickListener(this);
        btn_manual_control_base.setOnClickListener(this);
        btn_manual_control_lt_wheels.setOnClickListener(this);
        btn_manual_control_rt_wheels.setOnClickListener(this);
        btn_manual_control_catch.setOnClickListener(this);
        btn_manual_control_cut.setOnClickListener(this);
        btn_manual_control_head_lr.setOnClickListener(this);
        btn_manual_control_head_ud.setOnClickListener(this);
        btn_manual_control_updown.setOnClickListener(this);
        btn_manual_control_backward.setOnTouchListener(this);
        btn_manual_control_forward.setOnTouchListener(this);





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit_dialog:
                c.finish();
                break;
            default:
                break;
        }
        dismiss();

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
