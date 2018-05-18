package studies.kinkuro.sddargoogle.Talk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;

import studies.kinkuro.sddargoogle.MainActivity;
import studies.kinkuro.sddargoogle.R;

/**
 * Created by alfo6-2 on 2018-05-17.
 */

public class TalkFragment extends Fragment {

    SwipeRefreshLayout srLayout;
    RecyclerView recyclerView;
    FloatingActionButton fab;

    TalkAdapter talkAdapter;
    ArrayList<TalkItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talk, container, false);

        srLayout = view.findViewById(R.id.swipe_talk);
        recyclerView = view.findViewById(R.id.recycler_talk);
        fab = view.findViewById(R.id.fab_show_talk);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        talkAdapter = new TalkAdapter(getContext(), items);
        recyclerView.setAdapter(talkAdapter);

        items.clear();
        loadDB();

        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                loadDB();
                srLayout.setRefreshing(false);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TalkActivity.class));
            }
        });
    }//onActivityCreated()...

    public void loadDB(){
        new Thread(){
            @Override
            public void run() {
                String serverUrl = "http://kinkuro.dothome.co.kr/sddar/loadDB.php";

                try {
                    URL url = new URL(serverUrl);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setUseCaches(false);

                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);

                    StringBuffer buffer = new StringBuffer();
                    String line = reader.readLine();

                    while(true){
                        buffer.append(line);
                        line = reader.readLine();
                        if(line == null) break;
                        buffer.append("n");
                    }

                    String[] rows = buffer.toString().split(";;");
                    for(String row : rows){

                        String[] datas = row.split("%#%");

                        if(datas.length != 6) continue;

                        int no = Integer.parseInt(datas[0]);
                        String title = datas[1];
                        String name = datas[2];
                        String msg = datas[3];
                        String imgUrl = "http://kinkuro.dothome.co.kr/sddar/"+datas[4];
                        String date = datas[5];

                        items.add(0, new TalkItem(no, title, name, msg, imgUrl, date));

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                talkAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }//loadDB()...


}
