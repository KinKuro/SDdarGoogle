package studies.kinkuro.sddargoogle.Timer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import studies.kinkuro.sddargoogle.MainActivity;
import studies.kinkuro.sddargoogle.R;

/**
 * Created by alfo6-2 on 2018-05-17.
 */

public class TimerFragment extends Fragment {

    RadioGroup rGroup;
    RadioButton rNormal, rPremium;

    TextView tvMin, tvCol, tvSec;
    ImageView btnStart, btnStop;
    TextView tvCautionContent;

    int minute = 55, second = 0;

    TimerService service;
    TimerReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        rGroup = view.findViewById(R.id.radio_group_timer);
        rNormal = view.findViewById(R.id.radio_normal_timer);
        rPremium = view.findViewById(R.id.radio_premium_timer);

        tvMin = view.findViewById(R.id.tv_minute_timer);
        tvCol = view.findViewById(R.id.tv_colon_timer);
        tvSec = view.findViewById(R.id.tv_second_timer);

        btnStart = view.findViewById(R.id.btn_start_timer);
        btnStop = view.findViewById(R.id.btn_stop_timer);

        tvCautionContent = view.findViewById(R.id.tv_caution_content_timer);

        return view;
    }//onCreateView()...

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rGroup.check(R.id.radio_normal_timer);
        rGroup.setOnCheckedChangeListener(radioListener);

        btnStart.setOnClickListener(btnListener);
        btnStop.setOnClickListener(btnListener);

    }//onActivityCreated()...

    @Override
    public void onResume() {
        super.onResume();
        //TODO:: 서비스 시작 오류 고치기
        /*
        if(service == null){
            Intent intent = new Intent(getContext(), TimerService.class);
            getContext().startService(intent);
            getContext().bindService(intent, conn, 0);
        }
        */
        if(receiver == null){
            receiver = new TimerReceiver();
            IntentFilter filter = new IntentFilter("SDDAR_TIMER");
            getContext().registerReceiver(receiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(receiver != null)        getContext().unregisterReceiver(receiver);
    }

    //////LISTENERs//////////////////
    RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            switch (id){
                case R.id.radio_normal_timer:
                    minute = 55;
                    break;

                case R.id.radio_premium_timer:
                    minute = 115;
                    break;
            }
            second = 0;

            String minStr = String.format("%02d", minute);
            String secStr = String.format("%02d", second);

            tvMin.setText(minStr);
            tvSec.setText(secStr);
        }
    };

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_start_timer:
                    rNormal.setClickable(false);
                    rPremium.setClickable(false);
                    btnStart.setClickable(false);
                    btnStop.setClickable(true);

                    //TODO:: 타이머 시작하기
                    //Toast.makeText(getContext(), "타이머를 시작합니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "죄송합니다.\n타이머 기능은 준비중입니다.", Toast.LENGTH_SHORT).show();

                    /*
                    if(service != null){
                        service.setTimer(minute, second);
                        service.startTimer();
                    }
                    */
                    break;

                case R.id.btn_stop_timer:
                    Toast.makeText(getContext(), "타이머를 종료합니다.", Toast.LENGTH_SHORT).show();
                    rNormal.setClickable(true);
                    rPremium.setClickable(true);
                    btnStart.setClickable(true);
                    btnStop.setClickable(false);

                    if(service != null){
                        service.stopTimer();
                        getContext().unbindService(conn);
                        service = null;
                        Intent intent = new Intent(getContext(), TimerService.class);
                        getContext().stopService(intent);
                    }
                    break;
            }
        }
    };

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
    //////LISTENERs...///////////////

    public class TimerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("SDDAR_TIMER")){
                int minute = intent.getIntExtra("minute", 0);
                int second = intent.getIntExtra("second", 0);
                final boolean isOneSec = intent.getBooleanExtra("isOneSec", false);

                final String minStr = String.format("%02d", minute);
                final String secStr = String.format("%02d", second);
                Log.i("리시버", "리시빙");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMin.setText(minStr);
                        tvSec.setText(secStr);
                        if(isOneSec){
                            tvCol.setVisibility(View.INVISIBLE);
                        }else{
                            tvCol.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    }//TimerReceiver class...
}
