package com.example.nguyentanluan.sharemytrip;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Post;
import Modules.RoadFinder;
import Modules.RoadFinderListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_SetupTrip extends Fragment implements OnMapReadyCallback, RoadFinderListener, View.OnClickListener {
    MapView mapview;
    GoogleMap mMap;
    private ProgressDialog dialog;
    private Button btnstart, btnstop, btnresume, btnfinish;
    private ImageButton btnroad, btnhide, btnremovemaker;
    private TextView txtsetup;
    private FrameLayout layoutroad;
    private List<Polyline> polylinePath;
    private List<LatLng> listSetupRoadLocation;
    private List<LatLng> listRoadLocation;
    private boolean isStart = true, isSetupRoad;
    private LatLng mylocation;
    private Marker maker, markerStart, markerEnd, currentmaker;
    private Handler handler;
    private String urlToShare;
    private DatabaseReference mDatabase;
    private SharedPreferences pref;

    public Fragment_SetupTrip() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment__setup_trip, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapview = (MapView) view.findViewById(R.id.maptrip);
        mapview.onCreate(savedInstanceState);
        mapview.onResume();
        mapview.getMapAsync(this);
    }

    public void initView(View view) {
        btnroad = (ImageButton) view.findViewById(R.id.imgbtnroad);
        btnhide = (ImageButton) view.findViewById(R.id.btnhide);
        btnremovemaker = (ImageButton) view.findViewById(R.id.imgbtnremovemaker);
        btnstart = (Button) view.findViewById(R.id.btnstart);
        btnstop = (Button) view.findViewById(R.id.btnstop);
        btnresume = (Button) view.findViewById(R.id.btnresume);
        btnfinish = (Button) view.findViewById(R.id.btnfinish);
        txtsetup = (TextView) view.findViewById(R.id.txtnoticesetup);
        layoutroad = (FrameLayout) view.findViewById(R.id.layoutRoad);

        polylinePath = new ArrayList<>();
        btnroad.setOnClickListener(this);
        btnhide.setOnClickListener(this);
        btnremovemaker.setOnClickListener(this);
        btnstart.setOnClickListener(this);
        btnstop.setOnClickListener(this);
        btnresume.setOnClickListener(this);
        btnfinish.setOnClickListener(this);
        txtsetup.setOnClickListener(this);
        listRoadLocation = new ArrayList<LatLng>();
        listSetupRoadLocation = new ArrayList<>();
        urlToShare = new String();
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
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            btnremovemaker.setVisibility(View.VISIBLE);
            currentmaker = marker;
            return false;
        }
    };
    GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            btnremovemaker.setVisibility(View.GONE);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

        mMap.setOnMyLocationChangeListener(mylocationchangeListener);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(markerClickListener);
        mMap.setOnMapLongClickListener(mapLongClickListener);
        mMap.setOnMapClickListener(mapClickListener);
        mMap.setOnMyLocationButtonClickListener(mylocationbuttonclicklistenner);
    }

    private void showDialogSetup() {
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(getActivity());
        dialogbuilder.setTitle("QUESTION?");
        dialogbuilder.setMessage("Khi chọn chức năng này bạn sẽ phải chọn các địa điểm trên Map" +
                " để thiết lập hành trình \n" + "Bạn có muốn bắt đầu không?");
        dialogbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Bắt đầu thiết lập hành trình.", Toast.LENGTH_SHORT).show();
                listSetupRoadLocation.clear();
                txtsetup.setVisibility(View.VISIBLE);
                isSetupRoad = true;
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

    private void finishRoad() {
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(getActivity());
        dialogbuilder.setTitle("QUESTION?");
        dialogbuilder.setMessage("Bạn muốn kết thúc ghi dấu hành trình?");
        dialogbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                btnstart.setVisibility(View.VISIBLE);
                btnstop.setVisibility(View.GONE);
                btnresume.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Kết thúc ghi dấu hành trình.", Toast.LENGTH_SHORT).show();
                if (handler != null)
                    handler.removeCallbacks(runnable);
                sendRequestRoad();
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

    private void sendRequestRoad() {
        if (isSetupRoad) {
            if (listSetupRoadLocation.isEmpty()) {
                Toast.makeText(getActivity(), "Chưa thiết lập các địa điểm để ghi dấu.", Toast.LENGTH_SHORT).show();
                return;
            } else if (listSetupRoadLocation.size() > 0) {
                new RoadFinder(this, listSetupRoadLocation).excute();
            }
        } else if (listRoadLocation.isEmpty()) {
            Toast.makeText(getActivity(), "Hiện chưa có gì để ghi dấu, xin nhấn Start để bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        } else {
            new RoadFinder(this, listRoadLocation).excute();
        }

    }

    public void showDialogRoad() {
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(getActivity());
        dialogbuilder.setTitle("QUESTION?");
        dialogbuilder.setMessage("Bạn muốn bắt đầu ghi dấu hành trình?");
        dialogbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                btnstart.setVisibility(View.GONE);
                btnstop.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Bắt đầu ghi dấu hành trình.", Toast.LENGTH_SHORT).show();
                listRoadLocation.clear();
                getLocationOff();
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

    public synchronized void getLocationOff() {
        handler = new Handler();
        handler.postDelayed(runnable, 30000);

    }

    private Runnable runnable = new Runnable() {
        public void run() {
            if (mylocation != null) {
                if (listRoadLocation != null)
                    listRoadLocation.add(mylocation);
                Toast.makeText(getActivity(), mylocation.latitude + " - " + mylocation.longitude, Toast.LENGTH_SHORT).show();
            }
            handler.postDelayed(this, 30000);
        }
    };

    @Override
    public void onRoadFinderStart() {
        dialog = ProgressDialog.show(getActivity(), "Vui lòng đợi", "Hành trình của bạn đang được cập nhật.", true, true);
//        if (markerStart != null) {
//            markerStart.remove();
//        }
//        if (markerEnd != null) markerEnd.remove();
//        if (polylinePath != null) {
//            for (Polyline polyline : polylinePath) {
//                polyline.remove();
//            }
//        }
    }

    @Override
    public void onRoadFinderSuccess(List<LatLng> latLngs) {
        if (dialog != null)
            dialog.dismiss();
        PolylineOptions options = new PolylineOptions().geodesic(true).width(15).color(Color.GREEN);
        for (int i = 0; i < latLngs.size(); i++) {
            options.add(latLngs.get(i));
        }
        polylinePath.add(mMap.addPolyline(options));
        mMap.addCircle(new CircleOptions().center(latLngs.get(0)).radius(15).strokeWidth(10).strokeColor(Color.GREEN).fillColor(Color.GREEN));
        mMap.addCircle(new CircleOptions().center(latLngs.get(latLngs.size() - 1)).radius(15).strokeWidth(10).strokeColor(Color.RED).fillColor(Color.RED));
        PolylineOptions options1 = new PolylineOptions().geodesic(true).width(15).color(Color.RED);
        if (isSetupRoad) {
            for (int i = 0; i < listSetupRoadLocation.size(); i++)
                options1.add(listSetupRoadLocation.get(i));
            markerStart = mMap.addMarker(new MarkerOptions().title(findAddressMaker(latLngs.get(0)))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin))
                    .position(latLngs.get(0)));
            markerEnd = mMap.addMarker(new MarkerOptions().title(findAddressMaker(latLngs.get(latLngs.size() - 1)))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
                    .position(latLngs.get(latLngs.size() - 1)));
        } else {
            for (int i = 0; i < listRoadLocation.size(); i++) {
                options1.add(listRoadLocation.get(i));
            }
            markerStart = mMap.addMarker(new MarkerOptions().title(findAddressMaker(latLngs.get(0)))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin))
                    .position(latLngs.get(0)));
            markerEnd = mMap.addMarker(new MarkerOptions().title(findAddressMaker(latLngs.get(latLngs.size() - 1)))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
                    .position(latLngs.get(latLngs.size() - 1)));
        }
        polylinePath.add(mMap.addPolyline(options1));
        isSetupRoad = false;
        txtsetup.setVisibility(View.GONE);
        urlToShare = RoadFinder.createURL(latLngs);
    }

    public String findAddressMaker(final LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.isEmpty()) {
                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            } else if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2);
                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgbtnroad:
                layoutroad.setVisibility(View.VISIBLE);
                btnroad.setVisibility(View.GONE);
                break;
            case R.id.btnhide:
                layoutroad.setVisibility(View.GONE);
                btnroad.setVisibility(View.VISIBLE);
                break;
            case R.id.btnstart:
                btnfinish.setClickable(true);
                showDialogRoad();
                break;
            case R.id.btnstop:
                btnstop.setVisibility(View.GONE);
                btnresume.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Tạm dừng ghi dấu hành trình.", Toast.LENGTH_SHORT).show();
                if (handler != null)
                    handler.removeCallbacks(runnable);
                break;
            case R.id.btnresume:
                btnstop.setVisibility(View.VISIBLE);
                btnresume.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Tiếp tục ghi dấu hành trình.", Toast.LENGTH_SHORT).show();
                getLocationOff();
                break;
            case R.id.btnfinish:
                finishRoad();
                break;
            case R.id.imgbtnremovemaker:
                currentmaker.remove();
                if (isSetupRoad)
                    listRoadLocation.remove(currentmaker.getPosition());
                btnremovemaker.setVisibility(View.GONE);
                break;
            case R.id.txtnoticesetup:
                destroySetupRoad();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.item_search);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.setup:
                showDialogSetup();
                break;
            case R.id.style_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.style_hybird:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.style_sateline:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.item_clear:
                mMap.clear();
                break;
            case R.id.item_share:
                pushMyTrip();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pushMyTrip() {
        if (urlToShare.isEmpty()) {
            Toast.makeText(getActivity(), "Chưa có dữ liệu để share!", Toast.LENGTH_SHORT).show();
            return;
        }
        final EditText edtDescription = new EditText(getActivity());
        edtDescription.setHint("enter your message...");
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(getActivity());
        dialogbuilder.setTitle("QUESTION?");
        dialogbuilder.setMessage("Bạn muốn chia sẻ đoạn đường của mình với bạn bè?");
        dialogbuilder.setView(edtDescription);
        dialogbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String des = edtDescription.getText().toString();
                pushtoFirebase(des, urlToShare);
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

    private void pushtoFirebase(String des, String urlToShare) {
        Log.e("url", urlToShare);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pref = getActivity().getSharedPreferences(MainActivity.MYKEY, Context.MODE_PRIVATE);
        String key = pref.getString("key", "");
        String username = pref.getString("username", "");
        //String description = "mytrip";
        Post post = new Post(username, des, urlToShare);

        mDatabase.child("Posts").child(key).push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "share successfull", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getActivity(), "share failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void destroySetupRoad() {
        isSetupRoad = false;
        mMap.clear();
        listSetupRoadLocation.clear();
        txtsetup.setVisibility(View.GONE);
    }

    public void onResume() {
        super.onResume();
        isStart = true;
        if (Fragment_Friends.isPass) {
            String _url = getArguments().getString("url");
            new RoadFinder(this, _url).excute();
            Fragment_Friends.isPass = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isStart = true;
    }
}
