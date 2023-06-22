package com.tugassensor2023akbif310120094.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tugassensor2023akbif310120094.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NearbyPlacesActivity extends AppCompatActivity {
    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_nearby_places);

        client = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nearby);

        getCurrentLocation();

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NearbyPlacesActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions currentLocationMarker = new MarkerOptions().position(currentLocation).title("CURRENT LOCATION");
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
                            googleMap.addMarker(currentLocationMarker);

                            // Mendapatkan daftar tempat makanan terdekat
                            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                                    "location=" + location.getLatitude() + "," + location.getLongitude() +
                                    "&radius=500&type=restaurant&key=YOUR_API_KEY";

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray results = response.getJSONArray("results");
                                        int count = Math.min(results.length(), 5); // Ambil 5 lokasi terdekat saja

                                        for (int i = 0; i < count; i++) {
                                            JSONObject place = results.getJSONObject(i);
                                            JSONObject geometry = place.getJSONObject("geometry");
                                            JSONObject location = geometry.getJSONObject("location");
                                            double lat = location.getDouble("lat");
                                            double lng = location.getDouble("lng");
                                            String name = place.getString("name");

                                            LatLng restaurantLocation = new LatLng(lat, lng);
                                            MarkerOptions restaurantMarker = new MarkerOptions().position(restaurantLocation).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                            googleMap.addMarker(restaurantMarker);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });

                            // Tambahkan request ke antrian
                            RequestQueue queue = Volley.newRequestQueue(NearbyPlacesActivity.this);
                            queue.add(request);
                        }
                    });
                }
            }
        });
    }



}

// 10120094
// Tiara Trisanti Ramadhani
// IF3