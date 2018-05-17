package studies.kinkuro.sddargoogle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import studies.kinkuro.sddargoogle.Map.LocationItem;

public class MainActivity extends AppCompatActivity {

    ArrayList<LocationItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = G.locationItems;
        Toast.makeText(this, "대여소 갯수 : "+items.size(), Toast.LENGTH_SHORT).show();
        Log.i("MAIN_ACTIVITY" , "대여소 갯수 : "+items.size());
    }
}
