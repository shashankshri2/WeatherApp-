package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var address: TextView
    private lateinit var updatedAt: TextView
    private lateinit var status: TextView
    private lateinit var temp: TextView
    private lateinit var tempMin: TextView
    private lateinit var tempMax: TextView
    private lateinit var sunrise: TextView
    private lateinit var sunset: TextView
    private lateinit var wind: TextView
    private lateinit var pressure: TextView
    private lateinit var humidity: TextView
    private lateinit var about: TextView
    private lateinit var loader: ProgressBar
    private lateinit var mainContainer: RelativeLayout
    private lateinit var errorText: TextView
    private lateinit var searchView: SearchView

    private var CITY = "Bhopal"
    private val API = "b6b6350f8c8cc5bb0d9659438a623d68"  // Replace with your real OpenWeatherMap API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        address = findViewById(R.id.address)
        updatedAt = findViewById(R.id.updated_at)
        status = findViewById(R.id.status)
        temp = findViewById(R.id.temp)
        tempMin = findViewById(R.id.temp_min)
        tempMax = findViewById(R.id.temp_max)
        sunrise = findViewById(R.id.sunrise)
        sunset = findViewById(R.id.sunset)
        wind = findViewById(R.id.wind)
        pressure = findViewById(R.id.pressure)
        humidity = findViewById(R.id.humidity)
        about = findViewById(R.id.about)
        loader = findViewById(R.id.loader)
        mainContainer = findViewById(R.id.mainContainer)
        errorText = findViewById(R.id.errorText)
        searchView = findViewById(R.id.searchView)

        weatherTask().execute()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    CITY = query
                    weatherTask().execute()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    inner class weatherTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            loader.visibility = View.VISIBLE
            mainContainer.visibility = View.GONE
            errorText.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            val urlString =
                "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API"
            return try {
                URL(urlString).readText(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result!!)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val windObj = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAtText = jsonObj.getLong("dt")
                val updatedAtFormatted =
                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date(updatedAtText * 1000))

                val tempValue = main.getString("temp") + "°C"
                val tempMinValue = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMaxValue = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressureValue = main.getString("pressure") + " hPa"
                val humidityValue = main.getString("humidity") + "%"
                val sunriseTime = sys.getLong("sunrise") * 1000
                val sunsetTime = sys.getLong("sunset") * 1000
                val windSpeed = windObj.getString("speed") + " m/s"
                val weatherDescription = weather.getString("description").capitalize()
                val addressValue = jsonObj.getString("name") + ", " + sys.getString("country")

                // Populate UI
                address.text = addressValue
                updatedAt.text = updatedAtFormatted
                temp.text = tempValue
                tempMin.text = tempMinValue
                tempMax.text = tempMaxValue
                sunrise.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunriseTime))
                sunset.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunsetTime))
                wind.text = windSpeed
                pressure.text = pressureValue
                humidity.text = humidityValue
                about.text = "Shashank"

                // Show main container
                loader.visibility = View.GONE
                mainContainer.visibility = View.VISIBLE
            } catch (e: Exception) {
                loader.visibility = View.GONE
                errorText.visibility = View.VISIBLE
            }
        }
    }
}
