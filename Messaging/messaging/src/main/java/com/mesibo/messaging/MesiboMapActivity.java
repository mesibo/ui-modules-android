/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-present mesibo                                              *
* https://mesibo.com                                                          *
* All rights reserved.                                                        *
*                                                                             *
* Redistribution is not permitted. Use of this software is subject to the     *
* conditions specified at https://mesibo.com . When using the source code,    *
* maintain the copyright notice, conditions, disclaimer, and  links to mesibo * 
* website, documentation and the source code repository.                      *
*                                                                             *
* Do not use the name of mesibo or its contributors to endorse products from  *
* this software without prior written permission.                             *
*                                                                             *
* This software is provided "as is" without warranties. mesibo and its        *
* contributors are not liable for any damages arising from its use.           *
*                                                                             *
* Documentation: https://docs.mesibo.com/                                     *
*                                                                             *
* Source Code Repository: https://github.com/mesibo/                          *
*******************************************************************************/

package com.mesibo.messaging;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.mesibo.api.Mesibo;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/*
https://developers.google.com/maps/documentation/android-sdk/events
https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
https://stackoverflow.com/questions/47741624/restrict-google-place-autocomplete-to-within-a-city
https://stackoverflow.com/questions/1689096/calculating-bounding-box-a-certain-distance-away-from-a-lat-long-coordinate-in-j
https://stackoverflow.com/questions/30926743/android-places-autocomplete-set-lat-long-bounds
 */


public class MesiboMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static LocationInfo mLast = null;
    private LocationInfo mCurrent = null, mSelected = null;

    private GoogleMap mMap;
    private MapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder mGeoCoder = null;
    private boolean mCameraMoving = false;
    private Marker mMarker = null;
    private TextView mCurrentTextView;
    private AutocompleteSupportFragment mAutocompleteFragment;

    public static class LocationInfo {
        double lat = 0, lon = 0;
        String name = "";
        String address = "";
        Bitmap image = null; // only used for sending result in onActivity Result
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_address);

        mCurrentTextView = findViewById(R.id.sendCurrent);
        TextView stv = findViewById(R.id.sendselected);

        MesiboUiDefaults conf = MesiboUI.getUiDefaults();
        mCurrentTextView.setText(conf.sendCurrentLocation);
        stv.setText(conf.sendAnotherLocation);

        mCurrentTextView.setEnabled(false);

        mCurrentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(mCurrent);
            }
        });

        mGeoCoder = new Geocoder(this, Locale.getDefault()); // TBD, use locale also

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        // move my location button as it will be hidden by auto complete
        // https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st
        View locationButton =  mapFragment.getView().findViewWithTag("GoogleMapMyLocationButton");

        // This code also works but above code looks better
        //View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

        if(null != locationButton) {
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, 30);
        }

        mapFragment.getMapAsync(this);
        setupAutoCompleteFragment();
    }

    /**
     * Calculate the lat and len of a square around a point.
     * @return latMin, latMax, lngMin, lngMax
     */
    private void setAutoCompleteBounds(LocationInfo l) {
        double radius = 100; // put this in config
        double R = 6371;  // earth radius in km
        double latMin = l.lat - Math.toDegrees(radius/R);
        double latMax = l.lat + Math.toDegrees(radius/R);
        double lngMin = l.lon - Math.toDegrees(radius/R/Math.cos(Math.toRadians(l.lat)));
        double lngMax = l.lon + Math.toDegrees(radius/R/Math.cos(Math.toRadians(l.lat)));

        //LatLng northSide = SphericalUtil.computeOffset(center, 50000, 0);
        //LatLng southSide = SphericalUtil.computeOffset(center, 50000, 180);



        //mAutocompleteFragment.setLocationRestriction(rest);
       // mAutocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(latMin, lngMin), new LatLng(latMax, lngMax)));

      //  mAutocompleteFragment.setLocationBias()

    }

    private void setupAutoCompleteFragment() {

        mAutocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ICON_URL, Place.Field.LAT_LNG, Place.Field.VIEWPORT);
        mAutocompleteFragment.setPlaceFields(placeFields);

        mAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng l = place.getLatLng();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 16));
                mapFragment.getMapAsync(MesiboMapActivity.this);
                mSelected = new LocationInfo();
                mSelected.address = place.getAddress();
                mSelected.name = place.getName();
                mSelected.lat = l.latitude;
                mSelected.lon = l.longitude;
                setMarker(mSelected);

            }

            @Override
            public void onError(Status status) {
                if(null != status) {
                 //   Log.e("Error", status.getStatusMessage()); //crashing as getStatusMessage is null sometime
                }
            }
        });
    }

    private void setMarker(LocationInfo location) {
        LatLng l = new LatLng(location.lat, location.lon);

        if(null != mMarker) mMarker.remove();

        if(TextUtils.isEmpty(location.address))
            location.address = MesiboUI.getUiDefaults().unknownTitle;
        if(TextUtils.isEmpty(location.name))
            location.name = MesiboUI.getUiDefaults().unknownTitle;

        mMarker = mMap.addMarker(new MarkerOptions()
                .position(l)
                .title(location.name)
                .snippet(location.address)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMarker.showInfoWindow();
    }

    /* instead we can use

       https://maps.googleapis.com/maps/api/geocode/json?latlng=lat,lng&key=YOUR_API_KEY
     */
    private void getAddressFromGeocoder(LocationInfo location) {
        if(!TextUtils.isEmpty(location.address)) {
            setMarker(location);
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> addresses = mGeoCoder.getFromLocation(location.lat, location.lon, 1);
                    Address a = addresses.get(0);
                    if(null != a) {
                        location.address = a.getAddressLine(0);
                        location.name = a.getSubLocality();

                        // this will limit results from the country only
                        mAutocompleteFragment.setCountry(a.getCountryCode());

                        setMarker(location);
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void setCurrentLocationOnMap(LocationInfo location) {
        if (location == null) return;

        mCurrentTextView.setEnabled(true);

        // first set the camera so that map is updated
        LatLng ll = new LatLng(location.lat, location.lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));

        getAddressFromGeocoder(location);
        //setMarker(location.getLatitude(), location.getLongitude(), mSelectedName, mSelectedAddress);

    }

    private void setCurrentLocation() {
        if(null != mCurrent)
            return;

        if(null != mLast) {
            setCurrentLocationOnMap(mLast);
        }

        try {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(null != mCurrent) {
                                mCurrent = new LocationInfo();
                                mCurrent.lat = location.getLatitude();
                                mCurrent.lon = location.getLongitude();
                            }
                            setCurrentLocationOnMap(mCurrent);
                            mLast = mCurrent;
                        }
                    });

            fusedLocationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            mCurrent = new LocationInfo();
                            mCurrent.lat = location.getLatitude();
                            mCurrent.lon = location.getLongitude();
                            setCurrentLocationOnMap(mCurrent);
                            mLast = mCurrent;
                        }
                    });

        } catch (SecurityException e) {

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setCurrentLocation();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mSelected = new LocationInfo();
                mSelected.lat = latLng.latitude;
                mSelected.lon = latLng.longitude;
                getAddressFromGeocoder(mSelected);
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mCameraMoving = true;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(!mCameraMoving)
                    return;

                // let's not set on movement, user can long click
                if(true) return;

                CameraPosition position = mMap.getCameraPosition();
                mCameraMoving = false;

                mSelected = new LocationInfo();
                mSelected.lat = position.target.latitude;
                mSelected.lon = position.target.longitude;
                getAddressFromGeocoder(mSelected);

            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LocationInfo l = new LocationInfo();
                l.lat = marker.getPosition().latitude;
                l.lon = marker.getPosition().longitude;
                l.address = marker.getSnippet();
                l.name = marker.getTitle();
                setResult(l);
            }
        });



        //mGoogleMap.setOnInfoWindowClickListener(RegActivity.this);


        try {
            mMap.setMyLocationEnabled(true);

        } catch (SecurityException e) {

        }


       // mMap.getUiSettings().setMyLocationButtonEnabled(true);



        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 8.5f));
            /*
            mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title("mesibo.com")
                    .snippet("some snippet")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

             */
    }

    private void setResult(LocationInfo l) {

        // first move camera so that we can take snapshot
        LatLng ll = new LatLng(l.lat, l.lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));

        if(null != mMarker) {
            mMarker.remove();
        }

        // we can either use clear or remove title etc from the marker so that snapShot is clean
        //mMap.clear();

        // https://stackoverflow.com/questions/39016285/how-to-retrieve-snapshot-of-map-from-place-picker-activity

        /* For now mesibo api does not send image with location, we need to fix
            Uncomment this after API is fixed
            */
        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {



                byte[] byteArray = null;

                    try {
                        bitmap = SettingsScalingUtilities.createScaledBitmap(bitmap, 800, 600, SettingsScalingUtilities.ScalingLogic.CROP);
                        //MessagingFragment.onMapBitmap(bitmap); // commented after setIntentObject
                        //byteArray = Mesibo.bitmapToBuffer(bitmap, true, 100);
                        //bitmap.recycle();
                    } catch (Exception e) {

                    }

                l.image = bitmap;
                Intent data = new Intent();
                Mesibo.setIntentObject(data, l);

                /*
                data.putExtra("name", l.name);
                data.putExtra("address", l.address);
                data.putExtra("lat", l.lat);
                data.putExtra("lon", l.lon);
                if(null != byteArray)
                    data.putExtra("image", byteArray);

                 */

                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            //mMap.clear();
        }
    }
}