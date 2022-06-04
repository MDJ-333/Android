package com.example.a679_712_735_771a52posicionamientoyseguridad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    EditText lat, lon, dir;
    ImageView visor;
    Button obtener, btncamara;
    ProgressBar progressBar;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btncamara = findViewById(R.id.btncamara);
        visor = findViewById(R.id.iv_visor);

        lat = findViewById(R.id.edtlatitud);
        lon = findViewById(R.id.edtlongitud);

        obtener = findViewById(R.id.btnObtenerCoordenda);

        progressBar = findViewById(R.id.progressBar);

    }

    //solicita permisos para acceder ala camara del disositivo
    public void ftcamara(View view) {
        int permisoscamara = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permisoscamara == PackageManager.PERMISSION_GRANTED){
            //mostar mensaje
            Toast.makeText(this, "Permiso camara concedido", Toast.LENGTH_SHORT).show();
            camara();

        }else{
            requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CODE);
            camara();

        }
    }
    private void camara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            visor.setImageBitmap(imgBitmap);
        }
    }



    public void ObtenerCoordendasActual(View view) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        } else {

            getCoordenada();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCoordenada();
            } else {
                Toast.makeText(this, "Permiso Denegado ..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCoordenada() {

        try {
            progressBar.setVisibility(View.VISIBLE);
            //almacena los parametros de solicitudes en ep proveedor de ubicacion
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        double latitud = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        lat.setText(String.valueOf(latitud));
                        lon.setText(String.valueOf(longitude));
                    }
                    progressBar.setVisibility(View.GONE);
                }

            }, Looper.myLooper());

        }catch (Exception ex){
            System.out.println("Error es :" + ex);
        }

    }
    public  void Exit(View view){
        this.finish();
    }


}