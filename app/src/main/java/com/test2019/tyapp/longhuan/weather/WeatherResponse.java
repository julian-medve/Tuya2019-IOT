package com.test2019.tyapp.longhuan.weather;

import com.google.gson.annotations.SerializedName;
import com.test2019.tyapp.longhuan.weather.objects.Clouds;
import com.test2019.tyapp.longhuan.weather.objects.Coord;
import com.test2019.tyapp.longhuan.weather.objects.Main;
import com.test2019.tyapp.longhuan.weather.objects.Rain;
import com.test2019.tyapp.longhuan.weather.objects.Sys;
import com.test2019.tyapp.longhuan.weather.objects.Weather;
import com.test2019.tyapp.longhuan.weather.objects.Wind;

import java.util.ArrayList;

public class WeatherResponse {

    @SerializedName("coord")
    public Coord coord;
    @SerializedName("sys")
    public Sys sys;
    @SerializedName("weather")
    public ArrayList<Weather> weather = new ArrayList<Weather>();
    @SerializedName("main")
    public Main main;
    @SerializedName("wind")
    public Wind wind;
    @SerializedName("rain")
    public Rain rain;
    @SerializedName("clouds")
    public Clouds clouds;
    @SerializedName("dt")
    public float dt;
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("cod")
    public float cod;
}












