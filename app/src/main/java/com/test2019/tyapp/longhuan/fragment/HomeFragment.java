package com.test2019.tyapp.longhuan.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.utils.PreferenceUtils;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private volatile static HomeFragment mSelfFragment;

    private TextView txtUserName;

    private TextView txt_description, txt_temperature, txt_presure, txt_humidity;
    private TextView txt_temp_title, txt_presure_title, txt_humidity_title;
    private ImageView img_icon, img_getButton;


    public static Fragment newInstance() {
        if (mSelfFragment == null) {
            synchronized (HomeFragment.class) {
                if (mSelfFragment == null) {
                    mSelfFragment = new HomeFragment();
                }
            }
        }
        return mSelfFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);


//        txtUserName = (TextView) v.findViewById(R.id.main_name);
//
//        txt_description = (TextView)v.findViewById(R.id.txt_description);
//        txt_presure = (TextView)v.findViewById(R.id.txt_presure);
//        txt_humidity = (TextView)v.findViewById(R.id.txt_humidity);
//        txt_temperature = (TextView)v.findViewById(R.id.txt_temperature);
//        img_icon = (ImageView)v.findViewById(R.id.img_icon);
//
//        txt_temp_title = (TextView)v.findViewById(R.id.txt_temperature_title);
//        txt_humidity_title = (TextView)v.findViewById(R.id.txt_humidity_title);
//        txt_presure_title = (TextView)v.findViewById(R.id.txt_presure_title);


        //img_getButton = (ImageView)v.findViewById(R.id.img_getButton);


//        img_getButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getCurrentData();
//            }
//        });

        String name = null;
//        if (getArguments() != null) {
//             name = getArguments().getString("UserName");
//        }

        name = PreferenceUtils.getString(Objects.requireNonNull(this.getContext()), "username");

//        if (name != null)
//            txtUserName.setText(name);

//        getCurrentData();

        return v;
    }

//    public void setName(String name) {
//        if(name != null){
//            txtUserName.setText(name);
//        }
//    }

//    private void getCurrentData(){
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BaseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        GPSTracker gpsTracker = new GPSTracker(this.getContext());
//
//        WeatherService service = retrofit.create(WeatherService.class);
//        Call<WeatherResponse> call = service.getCurrentWeatherData(Double.toString(gpsTracker.getLatitude()), Double.toString(gpsTracker.getLongitude()), AppId);
//
//        ToastUtil.showToast(getContext(),Double.toString(gpsTracker.getLatitude()) + "  " + Double.toString(gpsTracker.getLongitude()) );
//        //Call<WeatherResponse> call = service.getCurrentWeatherData(lati, loni, AppId);
//
//        call.enqueue(new Callback<WeatherResponse>() {
//            @Override
//            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
//                if(response.code() == 200){
//                    WeatherResponse weatherResponse = response.body();
//                    assert weatherResponse != null;
//
//                    String icon = weatherResponse.weather.get(0).icon;
//                    String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";
//
//                    Picasso.get().load(iconUrl).into(img_icon);
//                    int temp = (int) weatherResponse.main.temp - 273;
//
//                    switch (icon){
//                        case "01d":
//                            txt_description.setText(R.string.clear_sky);
//                            break;
//                        case "01n":
//                            txt_description.setText(R.string.clear_sky);
//                            break;
//                        case "02d":
//                            txt_description.setText(R.string.few_clouds);
//                            break;
//                        case "02n":
//                            txt_description.setText(R.string.few_clouds);
//                            break;
//                        case "03d":
//                            txt_description.setText(R.string.scattered_clouds);
//                            break;
//                        case "03n":
//                            txt_description.setText(R.string.scattered_clouds);
//                            break;
//                        case "04d":
//                            txt_description.setText(R.string.broken_clouds);
//                            break;
//                        case "04n":
//                            txt_description.setText(R.string.broken_clouds);
//                            break;
//                        case "09d":
//                            txt_description.setText(R.string.shower_rain);
//                            break;
//                        case "09n":
//                            txt_description.setText(R.string.shower_rain);
//                            break;
//                        case "10d":
//                            txt_description.setText(R.string.rain);
//                            break;
//                        case "10n":
//                            txt_description.setText(R.string.rain);
//                            break;
//                        case "11d":
//                            txt_description.setText(R.string.thunderstorm);
//                            break;
//                        case "11n":
//                            txt_description.setText(R.string.thunderstorm);
//                            break;
//                        case "13d":
//                            txt_description.setText(R.string.snow);
//                            break;
//                        case "13n":
//                            txt_description.setText(R.string.snow);
//                            break;
//                        case "50d":
//                            txt_description.setText(R.string.mist);
//                            break;
//                        case "50n":
//                            txt_description.setText(R.string.mist);
//                            break;
//                        default:
//                            txt_description.setText(R.string.clear_sky);
//                    }
//
//
//                    txt_humidity.setText(Double.toString(weatherResponse.main.humidity) + "%");
//                    txt_presure.setText(Double.toString(weatherResponse.main.pressure) + "hPa");
//                    txt_temperature.setText(Integer.toString(temp) + " °C");
//
//                    txt_temp_title.setVisibility(View.VISIBLE);
//                    txt_humidity_title.setVisibility(View.VISIBLE);
//                    txt_presure_title.setVisibility(View.VISIBLE);
//
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<WeatherResponse> call, Throwable t) {
//                ToastUtil.showToast(getContext(), t.getMessage());
//            }
//        });
//
//    }
}
