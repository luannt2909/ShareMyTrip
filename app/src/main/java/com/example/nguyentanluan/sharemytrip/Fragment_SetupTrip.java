package com.example.nguyentanluan.sharemytrip;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_SetupTrip extends Fragment implements OnMapReadyCallback {
    MapView mapview;
    GoogleMap mMap;
    private ProgressDialog dialog;
    private Button btnstart, btnstop, btnresume, btnfinish;
    private ImageButton btnroad,btnhide;
    private TextView txtsetup;
    private FrameLayout layoutroad;
    private List<LatLng> listSetupRoadLocation;
    private boolean isStart;
    private LatLng mylocation;
    private Marker maker;

    public Fragment_SetupTrip() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_fragment__setup_trip, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapview=(MapView)view.findViewById(R.id.maptrip);
        mapview.onCreate(savedInstanceState);
        mapview.onResume();
        mapview.getMapAsync(this);
    }
    public void initView(View view){
        btnroad = (ImageButton) view.findViewById(R.id.imgbtnroad);
        btnhide = (ImageButton) view.findViewById(R.id.btnhide);
        btnstart = (Button) view.findViewById(R.id.btnstart);
        btnstop = (Button) view.findViewById(R.id.btnstop);
        btnresume = (Button) view.findViewById(R.id.btnresume);
        btnfinish = (Button) view.findViewById(R.id.btnfinish);
        layoutroad = (FrameLayout) view.findViewById(R.id.layoutRoad);
    }


    GoogleMap.OnMyLocationChangeListener mylocationchangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
//            if (marker != null) {
//                marker.remove();
//            }
            if (isStart) {
                LatLng lastLat = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLat, 16));
                isStart = false;
            }
            mylocation = new LatLng(location.getLatitude(), location.getLongitude());
        }

    };
    GoogleMap.OnMyLocationButtonClickListener mylocationbuttonclicklistenner = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            if (maker != null) {
                maker.remove();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    maker = mMap.addMarker(new MarkerOptions().position(mylocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    maker.setTag(0);
                }
            });
            return false;
        }
    };
    GoogleMap.OnMapLongClickListener mapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(final LatLng latLng) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    listSetupRoadLocation.add(latLng);
                }
            });
        }
    };
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //mMap.setOnMyLocationChangeListener(mylocationchangeListener);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.setOnMarkerClickListener(markerClickListener);
        //mMap.setOnMapLongClickListener(mapLongClickListener);
       // mMap.setOnMapClickListener(mapClickListener);
       // mMap.setOnMyLocationButtonClickListener(mylocationbuttonclicklistenner);
    }
    private void showDialogSetup() {
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(getActivity());
        dialogbuilder.setTitle("QUESTION?");
        dialogbuilder.setMessage("Khi chọn chức năng này bạn sẽ phải chọn các địa điểm trên Map" +
                " để thiết lập hành trình \n"+"Bạn có muốn bắt đầu không?");
        dialogbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Bắt đầu thiết lập hành trình.", Toast.LENGTH_SHORT).show();
                listSetupRoadLocation.clear();
                txtsetup.setVisibility(View.VISIBLE);
            }
        });
        dialogbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogbuilder.create();
        dialog.show();
    }
}
