package com.example.myweather

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //获得当前时间
        val myTime: TextView = findViewById(R.id.time)
        //获取当前温度
        val myTemper: TextView = findViewById(R.id.temperature)
        //获取当前湿度
        val myHumidity: TextView = findViewById(R.id.humidity)
        //获得天气状况
        val myClimate: TextView = findViewById(R.id.climate)
        //获得风力
        val myWind: TextView = findViewById(R.id.wind)
        //获得图片
        val myImage: ImageView = findViewById(R.id.weather_img)
        //获得城市
        val myCity: TextView = findViewById(R.id.city)

        val client = OkHttpClient()

        var currentCity: String = "北京"
        var currentCityCode: String = "101010100"

        // 获取SharedPreferences对象
        val preferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        // 读取保存的城市名称和代码，默认值为北京
        currentCity = preferences.getString("cityname", "北京") ?: "北京"
        currentCityCode = preferences.getString("citycode", "101010100") ?: "101010100"


        data class DailyInfo(
            var fxDate: String,
            var sunrise: String,
            var sunset: String,
            var moonrise: String,
            var moonset: String,
            var moonPhase: String,
            var moonPhaseIcon: String,
            var tempMax: String,
            var tempMin: String,
            var iconDay: String,
            var textDay: String,
            var iconNight: String,
            var textNight: String,
            var wind360Day: String,
            var windDirDay: String,
            var windScaleDay: String,
            var windSpeedDay: String,
            var wind360Night: String,
            var windDirNight: String,
            var windScaleNight: String,
            var windSpeedNight: String,
            var humidity: String,
            var precip: String,
            var pressure: String,
            var vis: String,
            var cloud: String,
            var uvIndex: String
        )

        data class ReferInfo(
            var sources: List<String>,
            var license: List<String>
        )

        data class ResponseVoDaily(
            var code: String,
            var updateTime: String,
            var fxLink: String,
            var daily: List<DailyInfo>,
            var refer: ReferInfo
        )

        data class NowVo(
            var temp: String,
            var feelsLike: String,
            var icon: String,
            var text: String,
            var windDir: String,
            var windScale: String,
            var humidity: String,
            var precip: String,
            var pressure: String,
            var vis: String,
            var cloud: String,
            var dew: String
        )

        data class ResponseVoWeather(
            var code: String,
            var updateTime: String,
            var fxLink: String,
            var now: NowVo
        )

        fun updateWeatherIcon(iconCode: String, imageView: ImageView) {
            val drawableResId = when (iconCode) {
                "100" -> R.drawable.biz_plugin_weather_qing
                "101" -> R.drawable.biz_plugin_weather_duoyun
                "104" -> R.drawable.biz_plugin_weather_yin
                "150" -> R.drawable.biz_plugin_weather_qing
                "151" -> R.drawable.biz_plugin_weather_duoyun
                "300", "301" -> R.drawable.biz_plugin_weather_zhenyu
                "302", "303", "304" -> R.drawable.biz_plugin_weather_leizhenyu
                "305", "306", "307", "308", "309", "310", "311", "312", "313", "314", "315", "316", "317", "318" -> R.drawable.biz_plugin_weather_dayu
                "350", "351" -> R.drawable.biz_plugin_weather_zhenyu
                "400", "401", "402", "403", "404", "405", "406", "407", "408", "409", "410" -> R.drawable.biz_plugin_weather_xiaoxue
                "456", "457" -> R.drawable.biz_plugin_weather_xiaoxue
                "500", "501", "502", "503", "504", "507", "508", "509", "510", "511", "512", "513", "514", "515" -> R.drawable.biz_plugin_weather_wu
                else -> R.drawable.biz_plugin_weather_qing // 默认天气图标
            }
            imageView.setImageResource(drawableResId)
        }

        // 封装获取当前天气的网络请求函数
        fun requestCurrentWeather(location: String, callback: (weather: ResponseVoWeather) -> Unit) {
            val url = "https://devapi.qweather.com/v7/weather/now?location=$location&key=709bba7014d84d9687848bd5ef65d988"
            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 请求失败的处理
                    Log.e("网络请求", "请求失败：${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    // 请求成功的处理
                    val jsonString = response.body?.string()
                    Log.i("网络请求", "请求成功：$jsonString")

                    val gson = Gson()
                    val weather = gson.fromJson(jsonString, ResponseVoWeather::class.java)
                    callback(weather)
                }
            })
        }

        // 封装获取未来七日天气的网络请求函数
        fun requestDailyWeather(location: String, callback: (dailyWeather: ResponseVoDaily) -> Unit) {
            val url = "https://devapi.qweather.com/v7/weather/7d?location=$location&key=709bba7014d84d9687848bd5ef65d988"
            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 请求失败的处理
                    Log.e("网络请求", "请求失败：${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    // 请求成功的处理
                    val jsonString = response.body?.string()
                    Log.i("网络请求", "请求成功：$jsonString")

                    val gson = Gson()
                    val dailyWeather = gson.fromJson(jsonString, ResponseVoDaily::class.java)
                    callback(dailyWeather)
                }
            })
        }

        // 获取当前天气
        requestCurrentWeather("101010100") { weather ->
            runOnUiThread {

                // 解析时间
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                val date = dateFormat.parse(weather.updateTime)
                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

                // 更新 TextView
                myTime.text =  getString(R.string.time) + "："+ formattedTime
                myClimate.text = getString(R.string.weather) + "："+weather.now.text
                myHumidity.text =  getString(R.string.humidity) + "："+weather.now.humidity
                myTemper.text = getString(R.string.temperature) + "：" + weather.now.temp
                myWind.text = getString(R.string.wind_speed) + "：" + weather.now.windScale
                myCity.text = currentCity

                //更新图片
                val iconCode = weather.now.icon
                updateWeatherIcon(iconCode,myImage)
            }
        }

        // 获取未来七日天气
        requestDailyWeather("101010100") { dailyWeather ->
            // 在这里处理未来七日天气数据
            runOnUiThread {
                // 在这里更新 UI，显示每日的日期和温度范围
                val dailyList = dailyWeather.daily

                for (i in 0 until minOf(dailyList.size, 6)) {
                    val dailyInfo = dailyList[i]
                    val dateTextView:TextView = findViewById(resources.getIdentifier("date_${i + 1}", "id", packageName))
                    val weatherIconImageView = findViewById<ImageView>(resources.getIdentifier("weather_icon_${i + 1}", "id", packageName))
                    val temperatureTextView = findViewById<TextView>(resources.getIdentifier("temperature_${i + 1}", "id", packageName))

                    // 在 for 循环外定义 SimpleDateFormat 对象，避免重复创建
                    val dateFormat = SimpleDateFormat("M.d", Locale.getDefault())
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dailyInfo.fxDate)
                    val formattedDate = dateFormat.format(date)

                    //更新图片
                    val iconCode = dailyInfo.iconDay
                    updateWeatherIcon(iconCode,weatherIconImageView)
                    dateTextView.text = formattedDate
                    temperatureTextView.text = "${dailyInfo.tempMin} ~ ${dailyInfo.tempMax} °C"
                }
            }
        }


        val someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //处理返回结果
            if (result.resultCode == Activity.RESULT_OK) {
                // 获取SharedPreferences对象
                val preferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                // 读取城市编码，默认为北京的编码
                val resultStr = preferences.getString("citycode", "101010100")
                val city = preferences.getString("cityname", "北京")

                if (city != null) {
                    currentCity = city
                }
                if (resultStr != null) {
                    currentCityCode = resultStr
                }

                // 获取当前天气
                if (resultStr != null) {
                    requestCurrentWeather(resultStr) { weather ->
                        runOnUiThread {
                            // 解析时间
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                            val date = dateFormat.parse(weather.updateTime)
                            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

                            // 更新 TextView
                            myTime.text =  getString(R.string.time) + "："+ formattedTime
                            myClimate.text = getString(R.string.weather) + "："+weather.now.text
                            myHumidity.text =  getString(R.string.humidity) + "："+weather.now.humidity
                            myTemper.text = getString(R.string.temperature) + "：" + weather.now.temp
                            myWind.text = getString(R.string.wind_speed) + "：" + weather.now.windScale
                            myCity.text = currentCity

                            //更新图片
                            val iconCode = weather.now.icon
                            updateWeatherIcon(iconCode,myImage)
                        }
                    }
                }

                // 获取未来七日天气
                if (resultStr != null) {
                    requestDailyWeather(resultStr) { dailyWeather ->
                        // 在这里处理未来七日天气数据
                        runOnUiThread {
                            // 在这里更新 UI，显示每日的日期和温度范围
                            val dailyList = dailyWeather.daily

                            for (i in 0 until minOf(dailyList.size, 6)) {
                                val dailyInfo = dailyList[i]
                                val dateTextView:TextView = findViewById(resources.getIdentifier("date_${i + 1}", "id", packageName))
                                val weatherIconImageView = findViewById<ImageView>(resources.getIdentifier("weather_icon_${i + 1}", "id", packageName))
                                val temperatureTextView = findViewById<TextView>(resources.getIdentifier("temperature_${i + 1}", "id", packageName))

                                // 在 for 循环外定义 SimpleDateFormat 对象，避免重复创建
                                val dateFormat = SimpleDateFormat("M.d", Locale.getDefault())
                                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dailyInfo.fxDate)
                                val formattedDate = dateFormat.format(date)

                                //更新图片
                                val iconCode = dailyInfo.iconDay
                                updateWeatherIcon(iconCode,weatherIconImageView)
                                dateTextView.text = formattedDate
                                temperatureTextView.text = "${dailyInfo.tempMin} ~ ${dailyInfo.tempMax} °C"
                            }
                        }
                    }
                }
            }
        }

        val cityManagerIcon = findViewById<ImageView>(R.id.title_city_manager)
        cityManagerIcon.setOnClickListener {
            val intent = Intent(this, SelectCity::class.java)
            //startActivity(intent)
            someActivityResultLauncher.launch(intent)
        }

        val mUpdateBtn = findViewById<ImageView>(R.id.title_update_btn)
        val mProgressBar = findViewById<ProgressBar>(R.id.title_update_progress)

        mUpdateBtn.setOnClickListener {
            mUpdateBtn.visibility = View.INVISIBLE
            mProgressBar.visibility = View.VISIBLE
            Thread {
                // 获取当前天气
                requestCurrentWeather(currentCityCode) { weather ->
                    runOnUiThread {

                        // 解析时间
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                        val date = dateFormat.parse(weather.updateTime)
                        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

                        // 更新 TextView
                        myTime.text =  getString(R.string.time) + "："+ formattedTime
                        myClimate.text = getString(R.string.weather) + "："+weather.now.text
                        myHumidity.text =  getString(R.string.humidity) + "："+weather.now.humidity
                        myTemper.text = getString(R.string.temperature) + "：" + weather.now.temp
                        myWind.text = getString(R.string.wind_speed) + "：" + weather.now.windScale
                        myCity.text = currentCity

                        //更新图片
                        val iconCode = weather.now.icon
                        updateWeatherIcon(iconCode,myImage)
                    }
                }

                // 获取未来七日天气
                requestDailyWeather(currentCityCode) { dailyWeather ->
                    // 在这里处理未来七日天气数据
                    runOnUiThread {
                        // 在这里更新 UI，显示每日的日期和温度范围
                        val dailyList = dailyWeather.daily

                        for (i in 0 until minOf(dailyList.size, 6)) {
                            val dailyInfo = dailyList[i]
                            val dateTextView:TextView = findViewById(resources.getIdentifier("date_${i + 1}", "id", packageName))
                            val weatherIconImageView = findViewById<ImageView>(resources.getIdentifier("weather_icon_${i + 1}", "id", packageName))
                            val temperatureTextView = findViewById<TextView>(resources.getIdentifier("temperature_${i + 1}", "id", packageName))

                            // 在 for 循环外定义 SimpleDateFormat 对象，避免重复创建
                            val dateFormat = SimpleDateFormat("M.d", Locale.getDefault())
                            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dailyInfo.fxDate)
                            val formattedDate = dateFormat.format(date)

                            //更新图片
                            val iconCode = dailyInfo.iconDay
                            updateWeatherIcon(iconCode,weatherIconImageView)
                            dateTextView.text = formattedDate
                            temperatureTextView.text = "${dailyInfo.tempMin} ~ ${dailyInfo.tempMax} °C"
                        }
                    }
                }

                Thread.sleep(1000) // 模拟更新过程
                runOnUiThread {
                    // 更新UI
                    mUpdateBtn.visibility = View.VISIBLE
                    mProgressBar.visibility = View.GONE
                }
            }.start()
        }

        val viewPager = findViewById<ViewPager>(R.id.next_week_weather)
        val pagerAdapter = MyPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        val indicator1 = findViewById<ImageView>(R.id.page_indicator_1)
        val indicator2 = findViewById<ImageView>(R.id.page_indicator_2)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // 此方法无需实现
            }

            override fun onPageSelected(position: Int) {
                indicator1.setImageResource(if (position == 0) R.drawable.page_indicator_focused else R.drawable.page_indicator_unfocused)
                indicator2.setImageResource(if (position == 0) R.drawable.page_indicator_unfocused else R.drawable.page_indicator_focused)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // 此方法无需实现
            }
        })
    }
}
