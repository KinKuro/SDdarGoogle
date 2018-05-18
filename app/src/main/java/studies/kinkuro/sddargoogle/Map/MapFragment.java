package studies.kinkuro.sddargoogle.Map;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import studies.kinkuro.sddargoogle.G;
import studies.kinkuro.sddargoogle.R;

/**
 * Created by alfo6-2 on 2018-05-17.
 */

public class MapFragment extends Fragment {

    public final int REQ_AUTOCOMPLETE_PLACE = 100;
    public final int REQ_AUTOCOMPLETE_START = 200;
    public final int REQ_AUTOCOMPLETE_DEST = 300;

    FloatingActionButton fabRoot;
    FloatingActionButton[] fabMinis = new FloatingActionButton[3];
    Animation animFabRoot, animFab1, animFab2, animFab3;
    boolean isRootClicked = false;

    ArrayList<LocationItem> items;

    LocationManager locationManager;
    Location location;
    Place place, strPlace, dstPlace;
    String title = "";
    String address = "";
    boolean isDest = false;

    Geocoder geocoder;
    GoogleMap gMap;

    SearchRouteDialog dialog;

    boolean isSearchMyLocation = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fabRoot = view.findViewById(R.id.fab_root_map);
        fabRoot.setOnClickListener(fabListener);
        for (int i = 0; i < fabMinis.length; i++) {
            fabMinis[i] = view.findViewById(R.id.fab_1_map + i);
            fabMinis[i].setOnClickListener(fabListener);
        }
        return view;
    }//onCreateView()...

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        items = G.locationItems;
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        initMap();

    }//onActivityCreated()...

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQ_AUTOCOMPLETE_PLACE:
                if(resultCode == getActivity().RESULT_OK){
                    place = PlaceAutocomplete.getPlace(getContext(), data);
                    location = new Location("search");
                    location.setLatitude(place.getLatLng().latitude);
                    location.setLongitude(place.getLatLng().longitude);
                    title = place.getName().toString();
                    searchLocation();
                }
                break;

            case REQ_AUTOCOMPLETE_START:
                if(resultCode == getActivity().RESULT_OK){
                    strPlace = PlaceAutocomplete.getPlace(getContext(), data);
                    String name = strPlace.getName().toString();
                    dialog.setTvStart(name);
                }
                break;
            case REQ_AUTOCOMPLETE_DEST:
                if(resultCode == getActivity().RESULT_OK){
                    dstPlace = PlaceAutocomplete.getPlace(getContext(), data);
                    String name = dstPlace.getName().toString();
                    dialog.setTvDest(name);
                }
                break;
        }
    }//onActivityResult()...

    @Override
    public void onPause() {
        if(isRootClicked) setFabs();
        super.onPause();
    }

    public void initMap() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        final double latitude, longitude;
        String title = null;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            title = "현재 위치";
        } else {
            latitude = 37.5662952;
            longitude = 126.9779451;    //서울시청 좌표
            title = "서울시청";
        }
        final String finalTitle = title;

        geocoder = new Geocoder(getContext(), Locale.KOREAN);

        FragmentManager fManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fManager.findFragmentById(R.id.map_map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;

                UiSettings settings = gMap.getUiSettings();
                settings.setZoomControlsEnabled(true);
                settings.setMapToolbarEnabled(false);

                LatLng latLng = new LatLng(latitude, longitude);
                gMap.addMarker(new MarkerOptions().position(latLng).title(finalTitle));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                drawCircle(latLng);

                searchRentals(latitude, longitude);
            }
        });
    }//initMap()...

    public void searchLocation() {
        gMap.clear();

        if (isSearchMyLocation) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null) location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            title = "현재 위치";
            if(location == null){
                Toast.makeText(getContext(), "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try {
            List<Address> addrs = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address addr = addrs.get(0);
            address = addr.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        gMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(address));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        drawCircle(latLng);
        searchRentals(location.getLatitude(), location.getLongitude());

        Toast.makeText(getContext(), "위치검색을 완료했습니다.", Toast.LENGTH_SHORT).show();
    }//searchLocation()...

    public void searchRoute(){
        if(strPlace != null && dstPlace != null){
            try{
                gMap.clear();

                LatLng strLatLng = strPlace.getLatLng();
                String strAddr = strPlace.getAddress().toString();
                List<Address> strAddrs = geocoder.getFromLocationName(strAddr, 3);
                double sLatitude = strAddrs.get(0).getLatitude();
                double sLongitude = strAddrs.get(0).getLongitude();
                searchRentals(sLatitude, sLongitude);
                gMap.addMarker(new MarkerOptions().position(strLatLng).title(strPlace.getName().toString()));
                drawCircle(strLatLng);

                LatLng dstLatLng = dstPlace.getLatLng();
                String dstAddr = dstPlace.getAddress().toString();
                List<Address> dstAddrs = geocoder.getFromLocationName(dstAddr, 3);
                double dLatitude = dstAddrs.get(0).getLatitude();
                double dLongitude = dstAddrs.get(0).getLongitude();
                isDest = true;
                searchRentals(dLatitude, dLongitude);
                gMap.addMarker(new MarkerOptions().position(dstLatLng).title(dstPlace.getName().toString()));
                drawCircle(dstLatLng);
                isDest = false;


                LatLng nwLatLng = new LatLng( (sLatitude-dLatitude < 0 ? sLatitude : dLatitude),
                        (sLongitude-dLongitude < 0 ? sLongitude : dLongitude));     //남서쪽 좌표 만들어주기
                LatLng seLatLng = new LatLng( (sLatitude-dLatitude > 0 ? sLatitude : dLatitude),
                        (sLongitude-dLongitude > 0 ? sLongitude : dLongitude));     //북동쪽 좌표 만들어주기
                LatLngBounds bounds = new LatLngBounds(nwLatLng, seLatLng);

                int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
                gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width/10));

                double cLatitude = (sLatitude + dLatitude) / 2;
                double cLongitude = (sLongitude + dLongitude) / 2;
                LatLng cntLatLng = new LatLng(cLatitude, cLongitude);
                PolylineOptions options = new PolylineOptions().add(strLatLng, cntLatLng);
                options.color(Color.argb(64, 0, 0, 255));
                gMap.addPolyline(options);
                options = new PolylineOptions().add(cntLatLng, dstLatLng);
                options.color(Color.argb(64, 255, 0, 0));
                gMap.addPolyline(options);


            }catch (IOException e){            }

        }
    }//searchRoute()...

    public void drawCircle(LatLng latLng){
        CircleOptions circleOptions = new CircleOptions();
        if(!isDest){
            circleOptions.center(latLng).radius(250).strokeWidth(2).strokeColor(Color.argb(64, 0, 0, 255)).fillColor(Color.argb(64, 0, 0, 255));
        } else{
            circleOptions.center(latLng).radius(250).strokeWidth(2).strokeColor(Color.argb(64, 255, 0, 0)).fillColor(Color.argb(64, 255, 0, 0));
        }
        gMap.addCircle(circleOptions);
    }

    public void searchRentals(final double latitude, final double longitude){
        new Thread(){
            @Override
            public void run() {
                for(int i = 0; i < items.size(); i++){
                    final LocationItem item = items.get(i);
                    double rLatitude = item.getLatitude();
                    double rLongitude = item.getLongitude();

                    if(calcDistance(latitude, longitude, rLatitude, rLongitude)){
                        final LatLng rLatLng = new LatLng(rLatitude, rLongitude);
                        Log.i("그려질 정류소", ""+isDest);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO:: flag 값으로 대여소 구분하기가 제대로 작동을 안해서 하나로 고정
                                gMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_start_n))
                                            .position(rLatLng).title(item.getName()).snippet(item.getAddress()));
                            }
                        });
                    }
                }

            }//run()...

            public boolean calcDistance(double pLatitude, double pLongitude, double rLatitude, double rLongitude){
                Location pLocation = new Location("point");
                pLocation.setLatitude(pLatitude);
                pLocation.setLongitude(pLongitude);
                Location rLocation = new Location("point");
                rLocation.setLatitude(rLatitude);
                rLocation.setLongitude(rLongitude);

                float distance = pLocation.distanceTo(rLocation);

                return distance <= 500;
            }//calcDistance()...
        }.start();
    }

    ////FABs//////
    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setFabs();
            switch(view.getId()){
                case R.id.fab_root_map:
                    break;
                case R.id.fab_1_map:
                    isSearchMyLocation = true;
                    searchLocation();
                    break;
                case R.id.fab_2_map:
                    isSearchMyLocation = false;
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setBoundsBias(new LatLngBounds(new LatLng(37.420644, 127.166697), new LatLng(37.695529, 126.762949)))
                                .build(getActivity());
                        startActivityForResult(intent, REQ_AUTOCOMPLETE_PLACE);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.fab_3_map:
                    dialog = new SearchRouteDialog(getContext());
                    dialog.show();
                    break;
            }
        }
    };//fabListener...

    public void setFabs(){

        RelativeLayout.LayoutParams layoutParams;

        if(!isRootClicked){
            animFabRoot = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_fab_clicked);
            animFab1 = AnimationUtils.loadAnimation(getContext(), R.anim.show_fabmini1);
            animFab2 = AnimationUtils.loadAnimation(getContext(), R.anim.show_fabmini2);
            animFab3 = AnimationUtils.loadAnimation(getContext(), R.anim.show_fabmini3);

            for(FloatingActionButton fab : fabMinis){
                fab.setVisibility(View.VISIBLE);
            }

            fabRoot.setAnimation(animFabRoot);

            layoutParams = (RelativeLayout.LayoutParams) fabMinis[0].getLayoutParams();
            layoutParams.rightMargin += (int) (fabMinis[0].getWidth() * 1.7);
            layoutParams.topMargin += (int) (fabMinis[0].getHeight() * 0.25);
            fabMinis[0].setLayoutParams(layoutParams);
            fabMinis[0].setAnimation(animFab1);
            fabMinis[0].setClickable(true);

            layoutParams = (RelativeLayout.LayoutParams) fabMinis[1].getLayoutParams();
            layoutParams.rightMargin += (int) (fabMinis[1].getWidth() * 1.5);
            layoutParams.topMargin += (int) (fabMinis[1].getHeight() * 1.5);
            fabMinis[1].setLayoutParams(layoutParams);
            fabMinis[1].setAnimation(animFab2);
            fabMinis[1].setClickable(true);

            layoutParams = (RelativeLayout.LayoutParams) fabMinis[2].getLayoutParams();
            layoutParams.rightMargin += (int) (fabMinis[2].getWidth() * 0.25);
            layoutParams.topMargin += (int) (fabMinis[2].getHeight() * 1.7);
            fabMinis[2].setLayoutParams(layoutParams);
            fabMinis[2].setAnimation(animFab3);
            fabMinis[2].setClickable(true);
        }else{
            animFabRoot = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_fab_no_clicked);
            animFab1 = AnimationUtils.loadAnimation(getContext(), R.anim.hide_fabmini1);
            animFab2 = AnimationUtils.loadAnimation(getContext(), R.anim.hide_fabmini2);
            animFab3 = AnimationUtils.loadAnimation(getContext(), R.anim.hide_fabmini3);

            fabRoot.setAnimation(animFabRoot);

            layoutParams = (RelativeLayout.LayoutParams) fabMinis[0].getLayoutParams();
            layoutParams.rightMargin -= (int) (fabMinis[0].getWidth() * 1.7);
            layoutParams.topMargin -= (int) (fabMinis[0].getHeight() * 0.25);
            fabMinis[0].setLayoutParams(layoutParams);
            fabMinis[0].setAnimation(animFab1);
            fabMinis[0].setClickable(false);

            layoutParams = (RelativeLayout.LayoutParams) fabMinis[1].getLayoutParams();
            layoutParams.rightMargin -= (int) (fabMinis[1].getWidth() * 1.5);
            layoutParams.topMargin -= (int) (fabMinis[1].getHeight() * 1.5);
            fabMinis[1].setLayoutParams(layoutParams);
            fabMinis[1].setAnimation(animFab2);
            fabMinis[1].setClickable(false);

            layoutParams = (RelativeLayout.LayoutParams) fabMinis[2].getLayoutParams();
            layoutParams.rightMargin -= (int) (fabMinis[2].getWidth() * 0.25);
            layoutParams.topMargin -= (int) (fabMinis[2].getHeight() * 1.7);
            fabMinis[2].setLayoutParams(layoutParams);
            fabMinis[2].setAnimation(animFab3);
            fabMinis[2].setClickable(false);

            for(FloatingActionButton fab : fabMinis){
                fab.setVisibility(View.INVISIBLE);
            }
        }
        isRootClicked = !isRootClicked;
    }//animFabs()...

    ////FABs...//////

    /////Dialogs/////
    public class SearchRouteDialog extends Dialog implements View.OnClickListener{

        TextView tvStart, tvDest;
        ImageView btnSubmit, btnCancel;

        public SearchRouteDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_map);

            tvStart = findViewById(R.id.tv_start_dialog_map);
            tvDest = findViewById(R.id.tv_destination_dialog_map);
            btnSubmit = findViewById(R.id.btn_ok_dialog_map);
            btnCancel = findViewById(R.id.btn_cancel_dialog_map);

            tvStart.setOnClickListener(this);
            tvDest.setOnClickListener(this);
            btnSubmit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_start_dialog_map:
                    Intent intent = null;
                    try {
                        intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setBoundsBias(new LatLngBounds(new LatLng(37.420644, 127.166697), new LatLng(37.695529, 126.762949)))
                                .build(getActivity());
                        startActivityForResult(intent, REQ_AUTOCOMPLETE_START);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.tv_destination_dialog_map:
                    try {
                        intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setBoundsBias(new LatLngBounds(new LatLng(37.420644, 127.166697), new LatLng(37.695529, 126.762949)))
                                .build(getActivity());
                        startActivityForResult(intent, REQ_AUTOCOMPLETE_DEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_ok_dialog_map:
                    searchRoute();
                    dismiss();
                    break;
                case R.id.btn_cancel_dialog_map:
                    dismiss();
                    break;
            }
        }

        public void setTvStart(String name){
            tvStart.setText(name);
        }
        public void setTvDest(String name){
            tvDest.setText(name);
        }

    }//SearchTwoLocationDialog class...

}
