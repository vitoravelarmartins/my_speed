package com.example.power_backgrond;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;


public class MainActivity extends FlutterActivity implements LocationListener {

    private Intent forService;
    protected LocationManager locationManager;
    public LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this.getFlutterEngine());

        forService = new Intent(MainActivity.this, MyService.class);


        new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), "com.powerback.message")
                .setMethodCallHandler(new MethodChannel.MethodCallHandler() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
                        if (call.method.equals("startService")) {
                            startService();
                            result.success("Serviço Iniciado");
                        }
                        if (call.method.equals("stopService")) {
                            stopService();
                            result.success("Serviço Parado");
                        }
                    }
                });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        startService(forService);
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        startService(forService);
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startService() {

        startForegroundService(forService);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//locationManager.requestLocationUpdates();

    }

    private void stopService() {
        stopService(forService);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        String latitude = "" + location.getLatitude();
        String speed = "" + location.getSpeed();
        String longitude = "" + location.getLongitude();
        String altitude = "" + location.getAltitude();
        String time = "" + location.getTime();

        String url = "https://appbestaa.herokuapp.com/v";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(MainActivity.this, "Sucess", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(MainActivity.this, "Erro", Toast.LENGTH_SHORT).show()) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Speed", speed);
                params.put("Latitude", latitude);
                params.put("Logintude", longitude);
                params.put("Altitude", altitude);
                params.put("time", time);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


}
