package studies.kinkuro.sddargoogle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import studies.kinkuro.sddargoogle.Map.MapFragment;
import studies.kinkuro.sddargoogle.Talk.TalkFragment;
import studies.kinkuro.sddargoogle.Timer.TimerFragment;

/**
 * Created by alfo6-2 on 2018-05-17.
 */

public class SddarPagerAdapater extends FragmentPagerAdapter {

    Fragment[] frags = new Fragment[3];

    public SddarPagerAdapater(FragmentManager fm) {
        super(fm);

        frags[0] = new MapFragment();
        frags[1] = new TimerFragment();
        frags[2] = new TalkFragment();
    }

    @Override
    public Fragment getItem(int position) {
        return frags[position];
    }

    @Override
    public int getCount() {
        return 3;
    }
}
