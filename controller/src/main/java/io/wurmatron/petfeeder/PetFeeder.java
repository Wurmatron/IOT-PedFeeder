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
import joptsimple.internal.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PetFeeder {

    // Global Instances
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // Program Vars
    public static Javalin server;
    public static Config config;

    public static void main(String[] args) {
        config = loadConfig();
        server = Javalin.create(conf -> {
            conf.registerPlugin(new OpenApiPlugin(new OpenApiOptions(new Info().version("1.0.0").description("PetFeeder Rest API")).path("/swagger-docs").swagger(new SwaggerOptions("/swagger").title("PetFeeder Swagger"))));
            conf.registerPlugin(new RedirectToLowercasePathPlugin());
            conf.enableCorsForAllOrigins();
        });
        server.start(config.port);
        server.get("/", ctx -> ctx.result("I'm a Pet Feeder"));
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
                Files.write(file.toPath(),GSON.toJson(config).getBytes());
            } catch (IOException e) {
                System.out.println("Unable to save config to '" + file.getAbsolutePath() + "'");
                System.exit(1);
            }
        }
        return config;
    }
}
