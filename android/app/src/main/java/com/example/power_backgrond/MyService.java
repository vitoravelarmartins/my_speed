package com.example.power_backgrond;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class MyService extends Service {
   //     implements LocationListener {
    protected LocationManager locationManager;
    public LocationListener locationListener;
    public static MyService gpsLocationListener;


    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

       // locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

      //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "messages")
                .setContentText("Informando Geocalização Background")
                .setContentTitle("Geolocalização Background")
                .setSmallIcon(R.mipmap.localizacao);

        startForeground(101, builder.build());


        String url = "https://appbestaa.herokuapp.com/v";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(MyService.this, "Sucess", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(MyService.this, "Erro", Toast.LENGTH_LONG).show()) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("task", "magic");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}


