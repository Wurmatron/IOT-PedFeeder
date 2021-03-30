package io.wurmatron.petfeeder.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RouteGenerator {

    // TODO Add setup page
    public static String BASE_URL = "http://xxx.xxx.xxx.xxx:8080/";
    public static String token = "CB902EB";
    public static ScheduledExecutorService EXECUTORS = Executors.newScheduledThreadPool(1);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static void send(String type, String url, Object data) {
        EXECUTORS.schedule(() -> {
            try {
                URL sendURL = new URL(BASE_URL + url);
                URLConnection connection = sendURL.openConnection();
                HttpURLConnection http = (HttpURLConnection) connection;
                http.setRequestMethod(type.toUpperCase());
                http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                http.setDoOutput(true);
                http.setRequestProperty("token", token);
                String json = GSON.toJson(data).replaceAll("\n", "");
                connection.setRequestProperty("Content-Length", String.valueOf(json.length()));
                connection.getOutputStream().write(json.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.SECONDS);
    }

    public static void post(String url, Object data) {
        send("post", url, data);
    }

    public static void postQuery(String url, String query) {
        send("post", url + query, null);
    }

    public static <T extends Object> T postResults(String url,String type, Class<T> resultJsonData) {
        if (!url.isEmpty()) {
            try {
                URL obj = new URL(BASE_URL + url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod(type);
                con.setRequestProperty("token", token);
                con.setReadTimeout(300000);
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK
                        || responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return GSON.fromJson(response.toString(), resultJsonData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
