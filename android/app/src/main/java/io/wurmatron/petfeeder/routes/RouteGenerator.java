package io.wurmatron.petfeeder.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RouteGenerator {

    // TODO Add setup page
    public static final String BASE_URL = "http://xxx.xxx.xxx.xxx:8080/";
    public static final String token = "testToken";
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

}
