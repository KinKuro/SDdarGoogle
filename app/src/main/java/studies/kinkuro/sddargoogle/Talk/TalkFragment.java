package studies.kinkuro.sddargoogle.Talk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import studies.kinkuro.sddargoogle.R;

/**
 * Created by alfo6-2 on 2018-05-17.
 */

public class TalkFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talk, container, false);
        return view;
    }
}