package org.example;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Weather {
    private String weatherText;
    private String APIkey = "2891e40ad2ab97a21154001dcb6211b7";

    public String getUrlContent(String urlAddress) {
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(urlAddress);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Город не найден!");
        }

        return content.toString();
    }

    public String getWeather(String city) {
        String output = getUrlContent(
                "https://api.openweathermap.org/data/2.5/weather?q="
                        + city
                        + "&appid=" + APIkey + "&units=metric"
        );


        if (!output.isEmpty()) {
            JSONObject object = new JSONObject(output);
            weatherText = "Погода в городе " + city + ":"
                    + "\n\nТемпература: " + object.getJSONObject("main").getDouble("temp") + "°C"
                    + "\nОщущается: " + object.getJSONObject("main").getDouble("feels_like") + "°C"
                    + "\nВлажность: " + object.getJSONObject("main").getDouble("humidity") + "%"
                    + "\nДавление: " + object.getJSONObject("main").getDouble("pressure") + " hPa";
        }
        return weatherText;
    }
}
