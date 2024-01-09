package safisoft.greenbuddyrobot.java;
import android.app.Application;
import android.os.Handler;

public class ApplicationEx extends Application{
    private final int STATUS_CHECK_INTERVAL = 500;
    public MyBtEngine mBtEngine;
    private final Handler handlerStatusCheck = new Handler();
    int Manual_Control = 0 ;


    @Override
    public void onCreate() {
        super.onCreate();
        mBtEngine = new MyBtEngine();
        handlerStatusCheck.postDelayed(new Runnable() {
            @Override
            public void run() {
                onBtStatusCheckTimer();
                handlerStatusCheck.postDelayed(this, STATUS_CHECK_INTERVAL);
            }
        }, STATUS_CHECK_INTERVAL);
    }

    private void onBtStatusCheckTimer(){
        if(mBtEngine.getState() == MyBtEngine.BT_STATE_NONE && Manual_Control != 1) {
            mBtEngine.start();
        }
    }

    public boolean writeBt(byte[] buffer){
        return mBtEngine.writeBt(buffer);
    }

    public void Start_Stop_Manual_control(int Control_state){
        if(Control_state == 1) {
            mBtEngine.setState(0);
            mBtEngine.stop();
        }
        Manual_Control = Control_state ;
    }



}