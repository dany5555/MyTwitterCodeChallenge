package com.kevin.mytwittercodechallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

// Written by Kevin Gonzalez.

public class MainActivity extends AppCompatActivity {

    // Declare UI widgets.
    Button fetchDataButton;
    TextView currentTemperature, currentWindSpeed, stdev, day1Temp, day2Temp, day3Temp, day4Temp, day5Temp;
    ImageView cloudinessImageView, day1Icon, day2Icon, day3Icon, day4Icon, day5Icon;

    // Used to set temperature values precision to 2 decimals.
    DecimalFormat precision = new DecimalFormat("0.00");

    // Variables used to calculate standard deviation.
    double temp1, temp2, temp3, temp4, temp5;

    // Variables used to save when screen is rotated.
    int cloudiness1, cloudiness2, cloudiness3, cloudiness4, cloudiness5;

    // Declare a request queue.
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect UI variables to their corresponding widgets in the layout file.
        fetchDataButton = findViewById(R.id.get_next_5_days_button);
        currentTemperature = findViewById(R.id.current_temperature);
        currentWindSpeed = findViewById(R.id.wind_speed);
        cloudinessImageView = findViewById(R.id.cloudy_or_sunny_icon);
        stdev = findViewById(R.id.standard_deviation);

        day1Temp = findViewById(R.id.day_1_temp);
        day2Temp = findViewById(R.id.day_2_temp);
        day3Temp = findViewById(R.id.day_3_temp);
        day4Temp = findViewById(R.id.day_4_temp);
        day5Temp = findViewById(R.id.day_5_temp);

        day1Icon = findViewById(R.id.day_1_icon);
        day2Icon = findViewById(R.id.day_2_icon);
        day3Icon = findViewById(R.id.day_3_icon);
        day4Icon = findViewById(R.id.day_4_icon);
        day5Icon = findViewById(R.id.day_5_icon);

        // Define a new request queue.
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Function call.
        getCurrentWeather();

        // Button listener to fetch data from online JSON file.
        fetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Define a new request queue.
                requestQueue = Volley.newRequestQueue(getApplicationContext());

                // Function calls.
                getDay1();
                getDay2();
                getDay3();
                getDay4();
                getDay5();
                getStandardDeviation(temp1, temp2, temp3, temp4, temp5);
            }
        });

        // Checks if the saved data is null or not.
        if (savedInstanceState != null) {

            // Restore textviews with saved data.
            day1Temp.setText(savedInstanceState.getString("day1Temp"));
            day2Temp.setText(savedInstanceState.getString("day2Temp"));
            day3Temp.setText(savedInstanceState.getString("day3Temp"));
            day4Temp.setText(savedInstanceState.getString("day4Temp"));
            day5Temp.setText(savedInstanceState.getString("day5Temp"));

            stdev.setText(savedInstanceState.getString("stdev"));

            // Restore imageviews with saved data.
            if (savedInstanceState.getInt("cloud1") > 50) {
                day1Icon.setImageResource(R.drawable.cloudy_icon);
            } else {
                day1Icon.setImageResource(R.drawable.sunny_icon);
            }

            if (savedInstanceState.getInt("cloud2") > 50) {
                day2Icon.setImageResource(R.drawable.cloudy_icon);
            } else {
                day2Icon.setImageResource(R.drawable.sunny_icon);
            }

            if (savedInstanceState.getInt("cloud3") > 50) {
                day3Icon.setImageResource(R.drawable.cloudy_icon);
            } else {
                day3Icon.setImageResource(R.drawable.sunny_icon);
            }

            if (savedInstanceState.getInt("cloud4") > 50) {
                day4Icon.setImageResource(R.drawable.cloudy_icon);
            } else {
                day4Icon.setImageResource(R.drawable.sunny_icon);
            }

            if (savedInstanceState.getInt("cloud5") > 50) {
                day5Icon.setImageResource(R.drawable.cloudy_icon);
            } else {
                day5Icon.setImageResource(R.drawable.sunny_icon);
            }

        }
    }

    // This function gets the current weather from the online JSON file.
    private void getCurrentWeather() {

        // Url containing the JSON file.
        String url = "https://twitter-code-challenge.s3.amazonaws.com/current.json";

        // Request a JSON object from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    // Parse into the weather, wind, and clouds objects from the JSON object.
                    JSONObject weather = response.getJSONObject("weather");
                    JSONObject wind = response.getJSONObject("wind");
                    JSONObject clouds = response.getJSONObject("clouds");

                    // Extract the temperature, speed, and cloudiness keys and save their values in variables for later use.
                    double temperature = weather.getDouble("temp");
                    double windSpeed = wind.getDouble("speed");
                    int cloudiness = clouds.getInt("cloudiness");

                    // Set the textviews with obtained values from the JSON file.
                    currentTemperature.setText(temperature + " °C / " + TemperatureConverter.celsiusToFahrenheit(temperature) + " °F");
                    currentWindSpeed.setText(windSpeed + " m/s");

                    // Check if the cloudiness is greater than 50. If so, a cloudy icon will be displayed. If not, a sunny icon will be displayed.
                    if (cloudiness > 50) {
                        cloudinessImageView.setImageResource(R.drawable.cloudy_icon);
                    } else {
                        cloudinessImageView.setImageResource(R.drawable.sunny_icon);
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

        requestQueue.add(request);

    }

    // This function is in charge of pulling all of the JSON data pertaining to the first day of the future days.
    private void getDay1() {

            String url = "https://twitter-code-challenge.s3.amazonaws.com/future_1.json";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Parse into the weather, and clouds objects from the JSON object.
                        JSONObject weather = response.getJSONObject("weather");
                        JSONObject clouds = response.getJSONObject("clouds");

                        // Extract the temperature, and cloudiness keys and save their values in variables for later use.
                        double temperature = weather.getDouble("temp");
                        temp1 = temperature;
                        int cloudiness = clouds.getInt("cloudiness");
                        cloudiness1 = cloudiness;

                        // Set the textviews with obtained values from the JSON file.
                        day1Temp.setText(precision.format(temperature) + " ° C / " + precision.format(TemperatureConverter.celsiusToFahrenheit(temperature)) + " ° F");

                        // Check if the cloudiness is greater than 50. If so, a cloudy icon will be displayed. If not, a sunny icon will be displayed.
                        if (cloudiness > 50) {
                            day1Icon.setImageResource(R.drawable.cloudy_icon);
                        } else {
                            day1Icon.setImageResource(R.drawable.sunny_icon);
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
            requestQueue.add(request);
        }

    // Has the same functionality as the getDay1() function.
    private void getDay2() {

        String url = "https://twitter-code-challenge.s3.amazonaws.com/future_2.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject weather = response.getJSONObject("weather");
                    JSONObject clouds = response.getJSONObject("clouds");

                    double temperature = weather.getDouble("temp");
                    temp2 = temperature;
                    int cloudiness = clouds.getInt("cloudiness");
                    cloudiness2 = cloudiness;


                    day2Temp.setText(precision.format(temperature) + " ° C / " + precision.format(TemperatureConverter.celsiusToFahrenheit(temperature)) + " ° F");

                    if (cloudiness > 50) {
                        day2Icon.setImageResource(R.drawable.cloudy_icon);
                    } else {
                        day2Icon.setImageResource(R.drawable.sunny_icon);
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
        requestQueue.add(request);
    }

    // Has the same functionality as the getDay1() function.
    private void getDay3() {

        String url = "https://twitter-code-challenge.s3.amazonaws.com/future_3.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject weather = response.getJSONObject("weather");
                    JSONObject clouds = response.getJSONObject("clouds");

                    double temperature = weather.getDouble("temp");
                    temp3 = temperature;
                    int cloudiness = clouds.getInt("cloudiness");
                    cloudiness3 = cloudiness;

                    day3Temp.setText(precision.format(temperature) + " ° C / " + precision.format(TemperatureConverter.celsiusToFahrenheit(temperature)) + " ° F");

                    if (cloudiness > 50) {
                        day3Icon.setImageResource(R.drawable.cloudy_icon);
                    } else {
                        day3Icon.setImageResource(R.drawable.sunny_icon);
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
        requestQueue.add(request);
    }

    // Has the same functionality as the getDay1() function.
    private void getDay4() {

        String url = "https://twitter-code-challenge.s3.amazonaws.com/future_4.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject weather = response.getJSONObject("weather");
                    JSONObject clouds = response.getJSONObject("clouds");

                    double temperature = weather.getDouble("temp");
                    temp4 = temperature;
                    int cloudiness = clouds.getInt("cloudiness");
                    cloudiness4 = cloudiness;


                    day4Temp.setText(precision.format(temperature) + " ° C / " + precision.format(TemperatureConverter.celsiusToFahrenheit(temperature)) + " ° F");

                    if (cloudiness > 50) {
                        day4Icon.setImageResource(R.drawable.cloudy_icon);
                    } else {
                        day4Icon.setImageResource(R.drawable.sunny_icon);
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
        requestQueue.add(request);
    }

    // Has the same functionality as the getDay1() function.
    private void getDay5() {

        String url = "https://twitter-code-challenge.s3.amazonaws.com/future_5.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject weather = response.getJSONObject("weather");
                    JSONObject clouds = response.getJSONObject("clouds");

                    double temperature = weather.getDouble("temp");
                    temp5 = temperature;
                    int cloudiness = clouds.getInt("cloudiness");
                    cloudiness5 = cloudiness;

                    day5Temp.setText(precision.format(temperature) + " ° C / " + precision.format(TemperatureConverter.celsiusToFahrenheit(temperature)) + " ° F");

                    if (cloudiness > 50) {
                        day5Icon.setImageResource(R.drawable.cloudy_icon);
                    } else {
                        day5Icon.setImageResource(R.drawable.sunny_icon);
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
        requestQueue.add(request);
    }

    // This function is in charge of calculating the standard deviation of the temperatures for all 5 future days.
    private void getStandardDeviation (double temp1, double temp2, double temp3, double temp4, double temp5) {

        // Number of days.
        int n = 5;

        // Calculate the mean.
        double mean = (temp1 + temp2 + temp3 + temp4 + temp5) / n;

        // Calculate the difference of a temperature and the mean, and then square that value.
        double sqr1 = Math.pow(temp1 - mean, 2);
        double sqr2 = Math.pow(temp2 - mean, 2);
        double sqr3 = Math.pow(temp3 - mean, 2);
        double sqr4 = Math.pow(temp4 - mean, 2);
        double sqr5 = Math.pow(temp5 - mean, 2);

        // Add all the previous results, divide by n-1 and then take the square root of that value.
        double standardDeviation = Math.sqrt((sqr1 + sqr2 + sqr3 + sqr4 + sqr5) / (n - 1));

        // Set the textview with the standard deviation.
        stdev.setText("Standard Deviation: " + precision.format(standardDeviation));
    }

    // This function is used to save the values when the screen is rotated.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("day1Temp", day1Temp.getText().toString());
        outState.putString("day2Temp", day2Temp.getText().toString());
        outState.putString("day3Temp", day3Temp.getText().toString());
        outState.putString("day4Temp", day4Temp.getText().toString());
        outState.putString("day5Temp", day5Temp.getText().toString());

        outState.putInt("cloud1", cloudiness1);
        outState.putInt("cloud2", cloudiness2);
        outState.putInt("cloud3", cloudiness3);
        outState.putInt("cloud4", cloudiness4);
        outState.putInt("cloud5", cloudiness5);

        outState.putString("stdev", stdev.getText().toString());
    }
}
