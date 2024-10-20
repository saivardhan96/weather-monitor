package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherRetriever {


    private final HttpClient client;
    private String pincode;
    private final String[] geoLocations;
    final double[] tempStats ;
    final double threshold;
    DataRepository dr ;


    public WeatherRetriever(String pincode, double threshold) {
        this.geoLocations = new String[3];
        this.client = HttpClient.newHttpClient();
        this.pincode = pincode;
        this.threshold = threshold;
        this.tempStats = new double[]{0.0, 1032.0, 0.0, 0.0};
        try{
            getCordinates(this.pincode);
            this.dr = new DataRepository();
        }
        catch(Exception e){
            System.out.println("Error in connecting to database.");
        }
    }

    public void start(){
        if(geoLocations[0]!=null) scheduler();
        else System.out.println("Network error.");
    }

    private void alert(double temp){
        System.out.println("Temperature, "+temp+", above the limit was recorded at "+timeFormatter()+".");
    }

    private void scheduler(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        System.out.println("Currently showing weather report for "+ geoLocations[2] + " District.");
        HashMap<String, Integer> condition = new HashMap<>();
        int[] alertTimer = {0};
        final double[] tempStats = {0.0 ,1032.0, 0.0, 0.0};

        Runnable fetchWeather = () -> {
            try {
                HttpResponse<String> response = fetchData();
                if(response == null) throw new Exception();
                JsonNode data = parseAndSummarize(response.body());
                String main = data.get("weather").get(0).get("main").toString();
                condition.put(main, condition.getOrDefault(main,0)+1);
                double temp = Double.parseDouble(data.get("main").get("feels_like").toString());
                temp-=273.15;
                temp = Double.parseDouble(new DecimalFormat("###.00").format(temp));
                if(temp >= threshold && threshold!=-1){
                    alertTimer[0]++;
                    if(alertTimer[0] > 3) {
                        alert(temp);
                        alertTimer[0] = 0;
                    }
                }
                else alertTimer[0]=0;
                tempStats[2]+=temp;
                tempStats[3]+=1.0;
                tempStats[0] = Math.max(tempStats[0], temp);
                tempStats[1] = Math.min(tempStats[1], temp);// imp
                System.out.println(temp + "`C at "+timeFormatter().substring(0,8));

            }
            catch (Exception fe){
                System.out.println("Network Error.. more info: "+ fe.getMessage());
            }
        };


        scheduler.scheduleAtFixedRate(fetchWeather, 0, 5, TimeUnit.SECONDS);

        scheduler.schedule(() ->{
            System.out.println("\nTime's up.System is shutting down. But hold on...");
            scheduler.shutdown();
            try {
                System.out.println("Data is being uploaded to database.");
                dr.saveInfo(tempStats, geoLocations, maxCondition(condition));
            } catch (SQLException e) {
                System.out.println("Failed to upload the data to database :( ! Cause:  " + e.getMessage() );
            }
            System.out.println("\nThank you!!");
        },30,TimeUnit.SECONDS);


    }


    private String maxCondition(HashMap<String, Integer> h){
        String ans = "";
        int f = 0;
        for(String s: h.keySet()){
            if(h.get(s) >= f){
                ans = s;
                f = h.get(s);
            }
        }
        return ans;
    }

    private void getCordinates(String pincode) throws Exception {
        String url = "https://apihub.latlong.ai/v4/pincode.json?pincode="+pincode;
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).header("X-Authorization-Token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJUb2tlbklEIjoiYzJiYTgyMDItOWEzZS00YTEyLThjZGEtNTk2OWI4ZThjNWI2IiwiQ2xpZW50SUQiOiJjNGU1ZmFmOC1hZTA2LTQzODItOGQyZC0zZGVhMGFiOGYxNDUiLCJCdW5pdElEIjoxMDc2MCwiQXBwTmFtZSI6IlNhaSgyMTIxMWEwNW45QGJ2cml0LmFjLmluKSAtIFNpZ24gVXAiLCJBcHBJRCI6MTIxMTMsIlRpbWVTdGFtcCI6IjIwMjQtMTAtMjAgMDc6NDk6NDYiLCJleHAiOjE3MzIwMDI1ODZ9.Sxh7MfPxX5dJM3Hrp5ZKb7EIThoItwRl-7ZC-NgByG0").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode data = parseAndSummarize(response.body());
        geoLocations[0] = data.get("data").get(0).get("latitude").toString();
        geoLocations[1] = data.get("data").get(0).get("longitude").toString();
        geoLocations[2] = data.get("data").get(0).get("district").toString();
    }


    private JsonNode parseAndSummarize(String jsonDate) throws Exception{
        JsonNode data = new ObjectMapper().readTree(jsonDate);
        return data;
    }

    private HttpResponse<String> fetchData() throws IOException, InterruptedException {
        String lat = geoLocations[0].substring(0,5);
        String lon = geoLocations[1].substring(0,5);
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=b4ac6705a2367030bd5692536bd56900";
        HttpRequest request = HttpRequest.newBuilder().GET().timeout(Duration.ofSeconds(5)).uri(URI.create(url)).build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = null;
        try{
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (HttpTimeoutException e){
            System.out.println("Its taking too long to fetch data. Process aborted after 5 seconds.");
        }
        return response;
    }

    private String timeFormatter(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss , dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

}
