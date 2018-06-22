package studies.kinkuro.sddargoogle.Timer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.PolylineOptions;

import studies.kinkuro.sddargoogle.R;

public class TimerActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton rNormal, rPreminum;

    TextView tvMin, tvSec, tvCol;
    ImageView btnStart, btnStop;
    TextView tvCautionContent;

    int min = 55, sec = 0;

    TimerService service;
    TimerReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        findViews();
        setRadio();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(service == null){
            Intent intent = new Intent(this, TimerService.class);
            startService(intent);
            bindService(intent, conn, 0);
        }
        if(receiver == null){
            receiver = new TimerReceiver();
            IntentFilter filter = new IntentFilter("SDDAR_TIMER");
            registerReceiver(receiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(receiver != null) unregisterReceiver(receiver);
    }



    public void clickStart(View v){
        rNormal.setClickable(false);
        rPreminum.setClickable(false);
        btnStart.setClickable(false);
        btnStop.setClickable(true);
        if(service != null){
            service.setTimer(min, sec);
            service.startTimer();
        }
    }

    public void clickStop(View v){
        rNormal.setClickable(true);
        rPreminum.setClickable(true);
        btnStart.setClickable(true);
        btnStop.setClickable(false);
        if(service != null){
            service.stopTimer();
            unbindService(conn);
            service = null;
            stopService(new Intent(this, TimerService.class));
        }
    }

    public void findViews(){

        radioGroup = findViewById(R.id.radio_group_timer_activity);
        rNormal = findViewById(R.id.radio_normal_timer_activity);
        rPreminum = findViewById(R.id.radio_premium_timer_activity);

        tvMin = findViewById(R.id.tv_minute_timer_activity);
        tvSec = findViewById(R.id.tv_second_timer_activity);
        tvCol = findViewById(R.id.tv_colon_timer_activity);

        btnStart = findViewById(R.id.btn_start_timer_activity);
        btnStop = findViewById(R.id.btn_stop_timer_activity);

        tvCautionContent = findViewById(R.id.tv_caution_content_timer_activity);

    }

    public void setRadio(){
        radioGroup.check(R.id.radio_normal_timer_activity);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id){
                    case R.id.radio_normal_timer_activity:
                        min = 55;
                        break;
                    case R.id.radio_premium_timer_activity:
                        min = 115;
                        break;
                }

                sec = 0;

                String minStr = String.format("%02d", min);
                String secStr = String.format("%02d", sec);

                tvMin.setText(minStr);
                tvSec.setText(secStr);
            }
        });
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TimerService.ServiceBinder binder = (TimerService.ServiceBinder)iBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void setTextViews(String minStr, String secStr, boolean isOneSec){
        tvMin.setText(minStr);
        tvSec.setText(secStr);
        if(isOneSec) tvCol.setVisibility(View.VISIBLE);
        else    tvCol.setVisibility(View.INVISIBLE);
    }

    public class TimerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("SDDAR_TIMER")){
                int min = intent.getIntExtra("minute", 0);
                int sec = intent.getIntExtra("second", 0);
                boolean isOneSec = intent.getBooleanExtra("isOneSec", false);

                String minStr = String.format("%02d", min);
                String secStr = String.format("%02d", sec);
                Log.i("리시버_액티비티", "리시빙");

                setTextViews(minStr, secStr, isOneSec);
            }
        }
    }//TimerReceiver class...
}
