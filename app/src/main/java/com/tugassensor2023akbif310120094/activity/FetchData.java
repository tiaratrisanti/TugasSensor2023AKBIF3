package com.tugassensor2023akbif310120094.activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FetchData extends AsyncTask<Object, String, String>{

    private static final String TAG = "";
    String googleNearPlaceData, url;
    GoogleMap googleMap;

    @Override
    protected String doInBackground(Object... objects) {
        try {
            googleMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googleNearPlaceData = downloadUrl.retriveUrl(url);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return googleNearPlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("local_results");


            for (int i = 0; i < 5; i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                JSONObject getLocation = jsonObject1.getJSONObject("geometry").getJSONObject("location");
//
//                String lat = getLocation.getString("lat");
//                String lng = getLocation.getString("lng");
//
//                JSONObject getName = jsonArray.getJSONObject(i);
//                String name = getName.getString("name");

                JSONObject getLocation = jsonObject1.getJSONObject("gps_coordinates");
                String name = jsonObject1.getString("title");
                String lat = getLocation.getString("latitude");
                String lng = getLocation.getString("longitude");


                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name);
                markerOptions.position(latLng);
                googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            }
            Log.d(TAG, "onPostExecute: " + jsonObject);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

}

// 10120094
// Tiara Trisanti Ramadhani
// IF3
