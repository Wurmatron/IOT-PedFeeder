package io.wurmatron.petfeeder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.javalin.Javalin;
import io.javalin.http.util.RedirectToLowercasePathPlugin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import io.wurmatron.petfeeder.endpoints.Routes;
import io.wurmatron.petfeeder.gpio.IOController;
import io.wurmatron.petfeeder.schedule.ConsumptionHandler;
import io.wurmatron.petfeeder.schedule.ScheduleScheduler;
import io.wurmatron.petfeeder.sql.DatabaseManager;
import joptsimple.internal.Strings;
import sun.misc.Signal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class PetFeeder {

    // Global Instances
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ScheduledExecutorService SCHEDULE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    // Program Vars
    public static Javalin server;
    public static Config config;
    public static DatabaseManager database;
    public static String authToken;

    public static void main(String[] args) {
        config = loadConfig();
        server = Javalin.create(conf -> {
            conf.registerPlugin(new OpenApiPlugin(new OpenApiOptions(new Info().version("1.0.0").description("PetFeeder Rest API")).path("/swagger-docs").swagger(new SwaggerOptions("/swagger").title("PetFeeder Swagger"))));
            conf.registerPlugin(new RedirectToLowercasePathPlugin());
            conf.enableCorsForAllOrigins();
        });
        if (!config.testing) {
            database = new DatabaseManager(config.Database);
            IOController.setup();
        }
        authToken = loadAuthToken();
        server.get("/", ctx -> ctx.result("I'm a Pet Feeder"));
        Routes.register(server);
        server.start(config.port);
        ScheduleScheduler.startup();
        ConsumptionHandler.startTracking();
        Signal.handle(new Signal("INT"), signal -> {
            System.out.println("Shutting down!");
            IOController.shutdown();
            server.stop();
            System.exit(0);
        });
    }

    public static Config loadConfig() {
        File file = new File("config.json");
        Config config = null;
        if (file.exists()) {
            try {
                config = GSON.fromJson(Strings.join(Files.readAllLines(file.toPath()).toArray(new String[0]), "\n"), Config.class);
            } catch (JsonParseException | IOException e) {
                System.out.println("Unable to parse config '" + file.getAbsolutePath() + "'");
                System.exit(1);
            }
        } else {
            System.out.println("Creating default configuration file! (" + file.getAbsolutePath() + ")");
            config = new Config();
            try {
                file.createNewFile();
                Files.write(file.toPath(), GSON.toJson(config).getBytes());
            } catch (IOException e) {
                System.out.println("Unable to save config to '" + file.getAbsolutePath() + "'");
                System.exit(1);
            }
        }
        return config;
    }

    public static String loadAuthToken() {
        File file = new File("auth.txt");
        if (file.exists()) {
            try {
                return Files.readAllLines(file.toPath()).get(0);
            } catch (IOException e) {
                System.out.println("Unable to read auth.txt");
                System.exit(1);
            }
        } else {
            try {
                String token = genRandomToken();
                Files.write(file.toPath(), token.getBytes());
                System.out.println("Your Auth Token is: " + token);
                return token;
            } catch (IOException e) {
                System.out.println("Failed to create auth.txt");
                System.exit(1);
            }
        }
        return null;
    }

    public static String genRandomToken() {
        return UUID.randomUUID().toString().substring(0,7).toUpperCase();
    }
}
