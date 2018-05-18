package studies.kinkuro.sddargoogle.Timer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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



    RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            switch (id){
                case R.id.radio_normal_timer:
                    tvMin.setText("55");
                    tvSec.setText("00");
                    break;

                case R.id.radio_premium_timer:
                    tvMin.setText("115");
                    tvSec.setText("00");
                    break;
            }
        }
    };

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_start_timer:
                    Toast.makeText(getContext(), "타이머를 시작합니다.", Toast.LENGTH_SHORT).show();
                    rNormal.setClickable(false);
                    rPremium.setClickable(false);
                    btnStart.setClickable(false);
                    btnStop.setClickable(true);
                    break;

                case R.id.btn_stop_timer:
                    Toast.makeText(getContext(), "타이머를 종료합니다.", Toast.LENGTH_SHORT).show();
                    rNormal.setClickable(true);
                    rPremium.setClickable(true);
                    btnStart.setClickable(true);
                    btnStop.setClickable(false);
                    break;
            }
        }
    };
}
