package studies.kinkuro.sddargoogle.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import studies.kinkuro.sddargoogle.G;

/**
 * Created by alfo6-2 on 2018-05-17.
 */

public class RentalsParser {

    Context context;
    AssetManager assetManager;
    ArrayList<LocationItem> items = new ArrayList<>();

    public RentalsParser(Context context) {
        this.context = context;
        assetManager = context.getAssets();
    }

    public void arraylistFromJson(){
        synchronized (this){
            try {
                InputStream is = assetManager.open("jsons/rental_office_data.json");
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(isr);

                StringBuffer buffer = new StringBuffer();
                String line = reader.readLine();

                while(line != null){
                    buffer.append(line);
                    line = reader.readLine();
                }

                String json = buffer.toString();
                JSONObject src = new JSONObject(json);

                JSONArray datas = src.getJSONArray("DATA");
                for(int i = 0; i < datas.length(); i++){
                    JSONObject data = datas.getJSONObject(i);

                    String address = data.getString("new_addr");
                    int contentId = Integer.parseInt(data.getString("content_id"));
                    String district = data.getString("addr_gu");
                    double latitude = Double.parseDouble(data.getString("latitude"));
                    double longitude = Double.parseDouble(data.getString("longitude"));
                    String name = data.getString("content_nm");
                    int numHolder = Integer.parseInt(data.getString("cradle_count"));

                    items.add(new LocationItem(address, contentId, district, latitude, longitude, name, numHolder));
                }

                G.locationItems = items;

                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "따릉이 대여소 내역을 불러왔습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }//arraylistFromJson()...

}
