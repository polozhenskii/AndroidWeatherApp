package com.homebox.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // the toolbar initialization
    private Toolbar toolbar;

    // we'll make HTTP request to this URL to retrieve weather conditions
    String weatherWebserviceURL = "http://api.openweathermap.org/data/2.5/weather?id=498817&appid=edc18bed57e8ea5ea176c96d9a6c11aa&units=metric";
    // the loading Dialog
    ProgressDialog pDialog;
    // Textview to show temperature and description
    TextView temperature, description, windSpeed, windDirection, humidityValue, pressure, cloudiness;
    // background image
    ImageView weatherBackground;
    // JSON object that contains weather information
    JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);

        // link the XML layout to this JAVA class
        setContentView(R.layout.activity_main);

        InitToolbar();

        // link graphical items to variables
        cloudiness = findViewById(R.id.cloudiness);
        pressure = findViewById(R.id.pressure);
        humidityValue = findViewById(R.id.humidity_value);
        windSpeed = findViewById(R.id.wind_speed);
        windDirection = findViewById(R.id.wind_direction);
        temperature = findViewById(R.id.temperature);
        description = findViewById(R.id.description);
        weatherBackground = findViewById(R.id.weatherbackground);

        // prepare the loading Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait while retrieving the weather condition ...");
        pDialog.setCancelable(false);

        // Check if Internet is working
        if (!isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, "Please check your Internet connection", Toast.LENGTH_LONG).show();
        } else {

            pDialog.show();

            // make HTTP request to retrieve the weather
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    weatherWebserviceURL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Parsing json object response
                        // response will be a json object

                        jsonObj = (JSONObject) response.getJSONArray("weather").get(0);
                        // display weather description into the "description textview"
                        description.setText(jsonObj.getString("description"));
                        // display the temperature
                        temperature.setText(response.getJSONObject("main").getString("temp") + " °C");

                        // display the wind speed
                        windSpeed.setText(response.getJSONObject("wind").getString("speed") + " m/s");
                        // display the wind direction
                        windDirection.setText(response.getJSONObject("wind").getString("deg") + " °");

                        // display the humidity value
                        humidityValue.setText(response.getJSONObject("main").getString("humidity")+ " %");

                        // display the pressure
                        pressure.setText(response.getJSONObject("main").getString("pressure")+ " hPa");

                        // display the cloudiness
                        cloudiness.setText(response.getJSONObject("clouds").getString("all")+ " %");

                        String backgroundImage = "";

                        // choose the image to set as background according to weather condition
                        if (jsonObj.getString("main").equals("Clouds")) {
                            backgroundImage = "http://vchemraznica.ru/wp-content/uploads/2017/02/cl522.jpg";
                        } else if (jsonObj.getString("main").equals("Rain")) {
                            backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/rainy-wallpaper1.jpg";
                        } else if (jsonObj.getString("main").equals("Snow")) {
                            backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/snow-wallpaper1.jpg";
                        } else if (jsonObj.getString("main").equals("Clear")) {
                            backgroundImage = "https://om-saratov.ru/files/pages/61597/1527014478general_pages_23_may_2018_i61597_v_saratove_budet_teplaya_i_yasn.jpg";
                        } else if (jsonObj.getString("main").equals("Mist")) {
                            backgroundImage = "http://www.zocalopublicsquare.org/wp-content/uploads/2010/05/mist.jpg";
                        } else if (jsonObj.getString("main").equals("Thunderstorm")) {
                            backgroundImage = "https://villagemedia.blob.core.windows.net/files/tbnewswatch/images/goodmorning/june-2018/lightning.jpg";
                        } else if (jsonObj.getString("main").equals("Smoke")) {
                            backgroundImage = "https://s3.envato.com/files/234103912/smoke%20imprev.jpg";
                        }

                        // load image from link and display it on background
                        // We'll use the Glide library
                        Glide
                                .with(getApplicationContext())
                                .load(backgroundImage)
                                .centerCrop()
                                .crossFade()
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        System.out.println(e.toString());
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(weatherBackground);

                        // hide the loading Dialog
                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error , try again ! ", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();

                    }
                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
                    // hide the progress dialog
                    pDialog.dismiss();
                }
            });

            // Adding request to request queue
            AppController.getInstance(this).addToRequestQueue(jsonObjReq);
        }
    }

    // check internet connection

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void InitToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);
    }
}
