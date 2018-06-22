package studies.kinkuro.sddargoogle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import studies.kinkuro.sddargoogle.Map.RentalsParser;

public class IntroActivity extends AppCompatActivity {

    private final int REQ_PERMISSION = 123;

    RentalsParser parser;

    Thread parsingThread;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        parser = new RentalsParser(this);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.INTERNET,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.CHANGE_WIFI_STATE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.VIBRATE};
                requestPermissions(permissions, REQ_PERMISSION);
            }else{
                parsingJsonAndGoMainActivity();
            }
        }else{
            parsingJsonAndGoMainActivity();

        }

    }//onCreate()...

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQ_PERMISSION:

                if(grantResults[0] == PackageManager.PERMISSION_DENIED ||
                        grantResults[1] == PackageManager.PERMISSION_DENIED ||
                        grantResults[2] == PackageManager.PERMISSION_DENIED ||
                        grantResults[3] == PackageManager.PERMISSION_DENIED ||
                        grantResults[4] == PackageManager.PERMISSION_DENIED){
                    new AlertDialog.Builder(this).setTitle("경고").setMessage("쓰자! 따릉이! 앱을 사용할 수 없습니다.\n앱을 종료합니다.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
                }else{
                    parsingJsonAndGoMainActivity();
                }
                break;
        }
    }//onRequestPermissionsResult()...

    public void parsingJsonAndGoMainActivity(){
        parsingThread = new Thread(){
            @Override
            public void run() {
                parser.arraylistFromJson();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(IntroActivity.this, "대여소 갯수 : "+G.locationItems.size(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(IntroActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        };

        parsingThread.start();

    }//parsingJsonAndGoMainActivity()...
}
