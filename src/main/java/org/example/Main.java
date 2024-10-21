package org.example;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)  {
        Scanner sc =  new Scanner(System.in);
        double threshold = -1.0;
        System.out.println("Weather Monitor Scheduler demo\n");
        System.out.println("Enter pincode of your area: ");
        String pincode  = sc.nextLine();
        System.out.println("""
                Select your unit
                1. Celsius
                2. Fahrenheit
                3. Kelvin""");
        int unit = sc.nextInt();
        sc.nextLine();
        System.out.println("Do you want to set an alert ? (Y/N) ");
        String opt = sc.nextLine();
        if(Objects.equals(opt.toLowerCase(), "y")) {
            System.out.println("Enter the threshold temperature (in celcius): ");
            threshold = sc.nextDouble();
        }
        WeatherRetriever wr = new WeatherRetriever(pincode , threshold, unit);
        wr.start();
    }

    public static void questions(){
        System.out.println("Select the Unit of temperature (K/C/F): ");
    }
}























































/*
    public static String[] getLatLon(String pincode){
        String url = "https://apihub.latlong.ai/v4/pincode.json?pincode="+pincode;
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).header("X-Authorization-Token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJUb2tlbklEIjoiYzJiYTgyMDItOWEzZS00YTEyLThjZGEtNTk2OWI4ZThjNWI2IiwiQ2xpZW50SUQiOiJjNGU1ZmFmOC1hZTA2LTQzODItOGQyZC0zZGVhMGFiOGYxNDUiLCJCdW5pdElEIjoxMDc2MCwiQXBwTmFtZSI6IlNhaSgyMTIxMWEwNW45QGJ2cml0LmFjLmluKSAtIFNpZ24gVXAiLCJBcHBJRCI6MTIxMTMsIlRpbWVTdGFtcCI6IjIwMjQtMTAtMjAgMDc6NDk6NDYiLCJleHAiOjE3MzIwMDI1ODZ9.Sxh7MfPxX5dJM3Hrp5ZKb7EIThoItwRl-7ZC-NgByG0").build();
        HttpClient client = HttpClient.newBuilder().build();
        String[] ans = new String[3];
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode data = parseAndSummarize(response.body());
            ans[0] = data.get("data").get(0).get("latitude").toString();
            ans[1] = data.get("data").get(0).get("longitude").toString();
            ans[2] = data.get("data").get(0).get("district").toString();

        }
        catch (Exception e){
            System.out.println("Invalid pincode. Please enter correct pincode." + e.getMessage());
        }

        return ans;


    }
*/

/*    public static void scheduler(String lat, String lon){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ArrayList<String> arr = new ArrayList<>();

        Runnable fetchWeatherTask = () -> {    // -> thread
            try {
                JsonNode data = parseAndSummarize(fetch(lat,lon).body());
                arr.add(data.get("weather").get(0).get("main").toString());
                Double temp = Double.parseDouble(data.get("main").get("feels_like").toString()) - 273.15;
                System.out.println("It currently feels like " + temp + "C.");
                System.out.println(arr);
            }
            catch (Exception e){
                System.out.println("Error in fetching weather: "+ e.getMessage());
            }
        };

        scheduler.scheduleAtFixedRate(fetchWeatherTask, 0, 15, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            System.out.println("Time limit reached. Shutting down.");
            scheduler.shutdown();
        } , 1 , TimeUnit.MINUTES);

    }*/

/*    public static JsonNode parseAndSummarize(String jsonData) throws Exception {
        ObjectMapper objectMapperr = new ObjectMapper();
        JsonNode data = objectMapperr.readTree(jsonData);

        *//*for (Iterator<JsonNode> it = data.elements(); it.hasNext(); ) {
            System.out.println(String.valueOf(it.next()));
        }*//*

        return data;

    }*/

/*    public static HttpResponse<String> fetch(String lat, String lon) throws IOException, InterruptedException {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=b4ac6705a2367030bd5692536bd56900";

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();

        HttpClient client = HttpClient.newBuilder().build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());

    }*/
