package com.example.desk.locprovider;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {

    @POST("savegps")
    Call<Gps> createGps(@Body Gps gps);

    @POST("gpsinfo")
    Call<Carinfo> getCar(@Body Carinfo caringo);
}
