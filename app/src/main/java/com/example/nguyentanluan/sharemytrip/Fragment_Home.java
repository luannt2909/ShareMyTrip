package com.example.nguyentanluan.sharemytrip;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Model.User;
import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Home extends Fragment implements OnMapReadyCallback, View.OnClickListener, DirectionFinderListener {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int STARTPLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private static final int ENDPLACE_AUTOCOMPLETE_REQUEST_CODE = 3;
    private static final int LOCATION_REQUEST_CODE = 101;

    private ProgressDialog dialog;
    private GoogleMap mMap;
    private MapView mapView;
    private Marker marker, markerStart, markerEnd, currentmaker;
    private List<Polyline> polylinePath;

    private LatLng mylocation = null;
    private boolean isStart = true, isFromMyLocation = false;
    private ImageButton btnfindmap, btnfind, btnroute, btnremovemaker, btnclose;
    private LinearLayout lyfindmap;
    private TextView txtstart, txtend, txtdistance, txtduration;
    private String origin, destination, makerdestination;
    private DatabaseReference mDatabase;
    private static String userId;
    private Map<String, User> map;
    private Map<String, Marker> marker_user;

    public Fragment_Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dtn : dataSnapshot.getChildren()) {
                    final User user = dtn.getValue(User.class);
                    map.put(dtn.getKey(), user);
                    Log.e("marker", marker_user.size() + "");
                    Log.e("User", user.getUserName() + "" + dtn.getKey());
                    Log.e("map", map.size() + "");
                }
                if (marker_user.isEmpty()) {
                    for (DataSnapshot dtn : dataSnapshot.getChildren()) {
                        final User user = dtn.getValue(User.class);
                        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(user.getLatitude(), user.getLongitude()))
                                .title(user.getUserName()));
                        marker_user.put(dtn.getKey(), m);

                    }
                    marker_user.get(userId).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    for (DataSnapshot dtn : dataSnapshot.getChildren()) {
                        final User user = dtn.getValue(User.class);
                        marker_user.get(dtn.getKey()).remove();
                        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(user.getLatitude(), user.getLongitude()))
                                .title(user.getUserName()));
                        marker_user.put(dtn.getKey(), m);

                    }
                    marker_user.get(userId).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Log.e("newChild", dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String keyremove = dataSnapshot.getKey().toString();
                marker_user.get(keyremove).remove();
                marker_user.remove(keyremove);
                map.remove(keyremove);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
        requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_REQUEST_CODE);
        userId = getArguments().getString("key");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
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
            mDatabase.child("Users").child(userId).child("longitude").setValue(location.getLongitude());
            mDatabase.child("Users").child(userId).child("latitude").setValue(location.getLatitude());
        }

    };
    GoogleMap.OnMyLocationButtonClickListener mylocationbuttonclicklistenner = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            if (marker != null) {
                marker.remove();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mylocation != null) {
                        marker = mMap.addMarker(new MarkerOptions().position(mylocation).title(findAddressMaker(mylocation))
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.avatar))));
                        marker.setTag(0);
                    } else return;
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
                    mMap.addMarker(new MarkerOptions().position(latLng).title(findAddressMaker(latLng))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                }
            });
        }
    };
    GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            btnroute.setVisibility(View.GONE);
            btnremovemaker.setVisibility(View.GONE);
        }
    };
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (btnfindmap.getVisibility() == View.VISIBLE)
                btnroute.setVisibility(View.VISIBLE);
            if (isMarkerUser(marker))
                btnremovemaker.setVisibility(View.GONE);
            else
                btnremovemaker.setVisibility(View.VISIBLE);
            currentmaker = marker;
            makerdestination = String.valueOf(marker.getPosition().latitude) + ", " + String.valueOf(marker.getPosition().longitude);
            return false;
        }
    };

    public void initView(View view) {
        btnfindmap = (ImageButton) view.findViewById(R.id.imgbtnmap);
        lyfindmap = (LinearLayout) view.findViewById(R.id.layoutfindmap);
        txtstart = (TextView) view.findViewById(R.id.txtstartlocation);
        txtend = (TextView) view.findViewById(R.id.txtendlocation);
        btnfind = (ImageButton) view.findViewById(R.id.btnFindPath);
        txtdistance = (TextView) view.findViewById(R.id.txtdistance);
        txtduration = (TextView) view.findViewById(R.id.txtduration);
        btnroute = (ImageButton) view.findViewById(R.id.btnroute);
        btnclose = (ImageButton) view.findViewById(R.id.imgbtnclose);
        btnremovemaker = (ImageButton) view.findViewById(R.id.imgbtnremovemaker);
        polylinePath = new ArrayList<>();
        btnfindmap.setOnClickListener(this);
        btnfind.setOnClickListener(this);
        btnroute.setOnClickListener(this);
        txtstart.setOnClickListener(this);
        txtend.setOnClickListener(this);
        btnclose.setOnClickListener(this);
        btnremovemaker.setOnClickListener(this);
        map = new HashMap<>();
        marker_user = new HashMap<>();
    }

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

    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(getActivity(),
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{permissionType}, requestCode
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Unable to show location - permission required", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.item_share);
        item.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.setup);
        item1.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_search:
                searchPlace(PLACE_AUTOCOMPLETE_REQUEST_CODE);
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
            case android.R.id.home:
                lyfindmap.setVisibility(View.GONE);
                btnfindmap.setVisibility(View.VISIBLE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
            case R.id.item_clear:
                mMap.clear();
                lyfindmap.setVisibility(View.GONE);
                btnfindmap.setVisibility(View.VISIBLE);
                //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchPlace(int requestcode) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent, requestcode);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
        if (requestCode == STARTPLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                txtstart.setText(place.getName().toString());
                origin = String.valueOf(place.getLatLng().latitude) + ", " + String.valueOf(place.getLatLng().longitude);
                markerStart = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
        if (requestCode == ENDPLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                txtend.setText(place.getName().toString());
                destination = String.valueOf(place.getLatLng().latitude) + ", " + String.valueOf(place.getLatLng().longitude);
                markerEnd = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgbtnmap:
                lyfindmap.setVisibility(View.VISIBLE);
                btnfindmap.setVisibility(View.GONE);
                //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.btnFindPath:
                sendRequest();
                break;
            case R.id.txtstartlocation:
                searchPlace(STARTPLACE_AUTOCOMPLETE_REQUEST_CODE);
                break;
            case R.id.txtendlocation:
                searchPlace(ENDPLACE_AUTOCOMPLETE_REQUEST_CODE);
                break;
            case R.id.btnroute:
                findMyRoute();
                btnroute.setVisibility(View.GONE);
                break;
            case R.id.imgbtnremovemaker:
                currentmaker.remove();
                btnremovemaker.setVisibility(View.GONE);
                btnroute.setVisibility(View.GONE);
                break;
            case R.id.imgbtnclose:
                lyfindmap.setVisibility(View.GONE);
                btnfindmap.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void findMyRoute() {
        isFromMyLocation = true;
        origin = String.valueOf(mylocation.latitude) + ", " + String.valueOf(mylocation.longitude);
        if (origin.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter origin address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (makerdestination.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter destination address", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder(this, origin, makerdestination).excute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void sendRequest() {
        if (txtstart.getText().equals("")) {
            Toast.makeText(getActivity(), "Please enter origin address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtend.getText().equals("")) {
            Toast.makeText(getActivity(), "Please enter destination address", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder(this, origin, destination).excute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDirectionFinderStart() {
        dialog = ProgressDialog.show(getActivity(), "Please wait.", "Finding direction...!", true, true);
        if (markerStart != null) {
            markerStart.remove();
        }
        if (markerEnd != null) markerEnd.remove();
        if (polylinePath != null) {
            for (Polyline polyline : polylinePath) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        if (dialog != null)
            dialog.dismiss();
        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLat, 17));
            txtdistance.setText(route.distance.text);
            txtduration.setText(route.duration.text);
            if (isFromMyLocation) {
                txtstart.setText(route.startAddress);
                txtend.setText(route.endAddress);
                isFromMyLocation = false;
            }
            markerStart = mMap.addMarker(new MarkerOptions().title(route.startAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin))
                    .position(route.startLat));
            markerEnd = mMap.addMarker(new MarkerOptions().title(route.endAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
                    .position(route.endLat));
            PolylineOptions options = new PolylineOptions().geodesic(true).width(15).color(Color.BLUE);
            for (int i = 0; i < route.points.size(); i++) {
                options.add(route.points.get(i));
            }
            polylinePath.add(mMap.addPolyline(options));
        }
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

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View view = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private boolean isMarkerUser(Marker marker) {
        if (marker_user.isEmpty())
            return false;
        for (Marker m : marker_user.values()) {
            if (m.equals(marker))
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isStart = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isStart = true;
    }
}

