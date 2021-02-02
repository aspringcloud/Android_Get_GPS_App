package com.example.desk.locprovider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private LocationManager locationManager;
    private List<String > listProviders;
    private Location mLastlocation = null;
    private Button btnActivity;
    private EditText edt_DtgSize;
    private TextView  tvGpsLatitude, tvGpsLongitude;
    private TextView tvNetworkLatitude, tvNetworkLongitude, tvPassiveLatitude, tvPassivekLongitude,tvGetSpeed, tvCalSpeed, tvTime, tvLastTime, tvGpsEnable, tvTimeDif, tvDistDif;;
    private String TAG = "LocationProvider";
    private double speed;

    private  JsonPlaceHolderApi jsonPlaceHolderApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnActivity = (Button)findViewById(R.id.btn_activity);
        tvGpsLatitude = (TextView)findViewById(R.id.tvGpsLatitude);
        tvGpsLongitude = (TextView)findViewById(R.id.tvGpsLongitude);
        tvNetworkLatitude = (TextView)findViewById(R.id.tvNetworkLatitude);
        tvNetworkLongitude = (TextView)findViewById(R.id.tvNetworkLongitude);
        tvPassiveLatitude = (TextView)findViewById(R.id.tvPassiveLatitude);
        tvPassivekLongitude = (TextView)findViewById(R.id.tvPassivekLongitude);
        edt_DtgSize = (EditText)findViewById(R.id.edt_DtgSize);

        tvGetSpeed = (TextView) findViewById(R.id.tvGetSpeed);
        tvCalSpeed = (TextView)findViewById(R.id.tvCalSpeed);
        tvTime = (TextView)findViewById(R.id.tvTime);
        tvLastTime = (TextView)findViewById(R.id.tvLastTime);
        tvTimeDif = (TextView)findViewById(R.id.tvTimeDif);
        tvDistDif = (TextView)findViewById(R.id.tvDistDif);

        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // POST연걸 정보 등록.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.231.27.57:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Get Car info
        getCar();

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            // Get Location
            double lng = lastKnownLocation.getLongitude();
            double lat = lastKnownLocation.getLatitude();
            Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
            tvGpsLatitude.setText(":: " + Double.toString(lat ));
            tvGpsLongitude.setText((":: " + Double.toString(lng)));

            // Get Time
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String formatDate = sdf.format(new Date(lastKnownLocation.getTime()));
            tvTime.setText(": " + formatDate);  //Time
        }
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLongitude();
            double lat = lastKnownLocation.getLatitude();
            Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
            tvNetworkLatitude.setText(":: " + Double.toString(lat ));
            tvNetworkLongitude.setText((":: " + Double.toString(lng)));
        }

        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLongitude();
            double lat = lastKnownLocation.getLatitude();
            Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
            tvPassiveLatitude.setText(":: " + Double.toString(lat ));
            tvPassivekLongitude.setText((":: " + Double.toString(lng)));
        }

        // GET Location Check / TF
        listProviders = locationManager.getAllProviders();
        boolean [] isEnable = new boolean[3];
        for(int i=0; i<listProviders.size();i++) {
            if(listProviders.get(i).equals(LocationManager.GPS_PROVIDER)) {
                isEnable[0] = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // tvGpsEnable.setText(": " +  String.valueOf(isEnable[0]));

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else if(listProviders.get(i).equals(LocationManager.NETWORK_PROVIDER)) {
                isEnable[1] = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                // tvNetworkEnable.setText(": " +  String.valueOf(isEnable[1]));

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } else if(listProviders.get(i).equals(LocationManager.PASSIVE_PROVIDER)) {
                isEnable[2] = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                // tvPassiveEnable.setText(": " +  String.valueOf(isEnable[2]));

                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
            }

        }

        Log.d(TAG, listProviders.get(0) + '/' + String.valueOf(isEnable[0]));
        Log.d(TAG, listProviders.get(1) + '/' + String.valueOf(isEnable[1]));
        Log.d(TAG, listProviders.get(2) + '/' + String.valueOf(isEnable[2]));
    }

    public void activity_click(View view)
    {
        switch (view.getId()) {
            case R.id.btn_activity:
                if (btnActivity.getText().toString() == "START") {
                    btnActivity.setText("STOP");
                } else {
                    btnActivity.setText("START");
                }
                break;
        }
    }

    private void createGps(String lat, String lon ,String speed, String filesize) {
        Gps gps  = new Gps(lat,lon,speed,filesize,SaveSharedPreference.getCarNumber(this),"True");
        Call<Gps> call = jsonPlaceHolderApi.createGps(gps);

        call.enqueue(new Callback<Gps>() {
            @Override
            public void onResponse(Call<Gps> call, Response<Gps> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "API 연결 실패" + response.body());
                    return;
                }
                Log.d(TAG, "API 연결 성공" + response.body());
            }

            @Override
            public void onFailure(Call<Gps> call, Throwable t) {
                Log.d(TAG,t.toString());
            }
        });
    }

    private void getCar() {
        Carinfo carinfo = new Carinfo(SaveSharedPreference.getCarNumber(this));
        Call<Carinfo> call = jsonPlaceHolderApi.getCar(carinfo);

        call.enqueue(new Callback<Carinfo>() {
            @Override
            public void onResponse(Call<Carinfo> call, Response<Carinfo> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Car 정보가져오기 실패" + response.body());
                    return;
                }
                String car = response.body().getFilesize().toString();
                Log.d(TAG, "Car 정보 가져오기 성공 : " + car);
                edt_DtgSize.setText(car);
            }

            @Override
            public void onFailure(Call<Carinfo> call, Throwable t)  {
                Log.d(TAG,t.toString());
            }
        });
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
    }


    @Override
    public void onLocationChanged(Location location) {

        double latitude = 0.0;
        double longitude = 0.0;

        if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvGpsLatitude.setText(": " + Double.toString(latitude));
            tvGpsLongitude.setText((": " + Double.toString(longitude)));
            Log.d(TAG + " GPS : ", Double.toString(latitude)+ '/' + Double.toString(longitude));

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            double deltaTime = 0;

            //  getSpeed() 함수를 이용하여 속도를 계산
            double getSpeed = Double.parseDouble(String.format("%.3f", location.getSpeed()*3.6));
            tvGetSpeed.setText(": " + getSpeed);  //Get Speed
            String formatDate = sdf.format(new Date(location.getTime()));
            tvTime.setText(": " + formatDate);  //Time

            // 위치 변경이 두번째로 변경된 경우 계산에 의해 속도 계산
            if(mLastlocation != null) {
                //시간 간격
                deltaTime = (location.getTime() - mLastlocation.getTime()) / 1000.0;
                tvTimeDif.setText(": " + deltaTime + " sec");  // Time Difference
                tvDistDif.setText(": " + mLastlocation.distanceTo(location) + " m");  // Time Difference
                // 속도 계산
                speed = mLastlocation.distanceTo(location) / deltaTime;

                String formatLastDate = sdf.format(new Date(mLastlocation.getTime()));
                tvLastTime.setText(": " + formatLastDate);  //Last Time

                if(Double.isNaN(speed)) {
                    speed = 0;
                }
                double calSpeed = Double.parseDouble(String.format("%.3f", speed*3.6));
                tvCalSpeed.setText(": " + calSpeed);  //Cal Speed
            }
            // 현재위치를 지난 위치로 변경
            mLastlocation = location;
            if(speed != 0 &&  btnActivity.getText().toString() == "STOP") {
                createGps(Double.toString(latitude),Double.toString(longitude),String.format("%.3f", speed*3.6), edt_DtgSize.getText().toString());
            }
        }
        if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvNetworkLatitude.setText(": " + Double.toString(latitude ));
            tvNetworkLongitude.setText((": " + Double.toString(longitude)));
            Log.d(TAG + " NETWORK : ", Double.toString(latitude )+ '/' + Double.toString(longitude));
        }
        if(location.getProvider().equals(LocationManager.PASSIVE_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvPassiveLatitude.setText(": " + Double.toString(latitude ));
            tvPassivekLongitude.setText((": " + Double.toString(longitude)));
            Log.d(TAG + " PASSIVE : ", Double.toString(latitude )+ '/' + Double.toString(longitude));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
    }
}
