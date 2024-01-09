package safisoft.greenbuddyrobot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import safisoft.greenbuddyrobot.java.LivePreviewActivity;

public class Choose_Control_Options_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_control_options);

        ImageButton btn_discover ,btn_automatic,btn_manual ;
        ImageButton btn_settings_and_commands ;

        btn_discover = findViewById(R.id.btn_discover);
        btn_automatic = findViewById(R.id.btn_automatic);
        btn_manual = findViewById(R.id.btn_manual);
        btn_settings_and_commands = findViewById(R.id.btn_settings_and_commands);

        btn_settings_and_commands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Choose_Control_Options_Activity.this, SettingsAndCommandsActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btn_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_move = new Intent(Choose_Control_Options_Activity.this, LivePreviewActivity.class);
                startActivity(intent_move);
                finish();
            }
        });

        btn_automatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_move = new Intent(Choose_Control_Options_Activity.this, FindBluetoothActivity.class);
                intent_move.putExtra("CONTROL_MODE", "AUTO");
                startActivity(intent_move);
                finish();

            }
        });

        btn_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_move = new Intent(Choose_Control_Options_Activity.this, FindBluetoothActivity.class);
                intent_move.putExtra("CONTROL_MODE", "MANUAL");
                startActivity(intent_move);
                finish();

            }
        });



    }
}