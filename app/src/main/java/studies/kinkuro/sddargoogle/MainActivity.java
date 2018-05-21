package studies.kinkuro.sddargoogle;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import studies.kinkuro.sddargoogle.Map.LocationItem;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    Toolbar toolbar;
    TextView tvTitle;

    FragmentTabHost tabHost;
    ViewPager pager;
    SddarPagerAdapater pagerAdapater;

    NavigationView navView;

    ArrayList<LocationItem> items;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //확인용///
        items = G.locationItems;
        Toast.makeText(this, "대여소 갯수 : "+items.size(), Toast.LENGTH_SHORT).show();
        Log.i("MAIN_ACTIVITY" , "대여소 갯수 : "+items.size());
        ////////////

        drawer = findViewById(R.id.drawerlayout_main);
        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        tvTitle = findViewById(R.id.tv_title_main);

        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("map").setIndicator("맵"), DummyFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("timer").setIndicator("타이머"), DummyFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("talk").setIndicator("이야기"), DummyFragment.class, null);

        pager = findViewById(R.id.pager_main);
        pagerAdapater = new SddarPagerAdapater(getSupportFragmentManager());
        pager.setAdapter(pagerAdapater);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tag) {
                if(tag.equals("map")){
                    pager.setCurrentItem(0, true);
                    tvTitle.setText("MAP");
                }else if(tag.equals("timer")){
                    pager.setCurrentItem(1, true);
                    tvTitle.setText("TIMER");
                }else if(tag.equals("talk")){
                    pager.setCurrentItem(2, true);
                    tvTitle.setText("TALK");
                }
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {            }

            @Override
            public void onPageSelected(int position) {
                tabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {            }
        });

        navView = findViewById(R.id.naviview_main);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.pub_app_navi_item:
                        Intent goPubApp = getPackageManager().getLaunchIntentForPackage("com.dki.spb_android");
                        if(goPubApp != null){
                            startActivity(goPubApp);
                        }else{
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dki.spb_android")));
                            } catch (android.content.ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.dki.spb_android")));
                            }
                        }
                        break;
                    case R.id.how_to_use_navi_item:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bikeseoul.com/app/use/moveUseInfomation.do")));
                        break;
                    /*
                    case R.id.evaluate_navi_item:
                        //TODO::이건 다른걸로 고치자
                        break;
                    */
                }
                return false;
            }
        });

    }//onCreate()...

    public void onBackPressed() {

        if(drawer.isDrawerOpen(navView)) drawer.closeDrawer(navView, true);

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed();
        }else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }//onBackPressed()...

    public void clickNaviMenu(View v){
        drawer.openDrawer(navView, true);
    }

}
