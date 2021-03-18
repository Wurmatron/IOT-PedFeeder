package io.wurmatron.petfeeder.endpoints;

import com.google.gson.JsonParseException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.petfeeder.PetFeeder;
import io.wurmatron.petfeeder.gpio.IOController;
import io.wurmatron.petfeeder.models.Calibration;
import io.wurmatron.petfeeder.models.CalibrationLoadCell;
import jdk.internal.joptsimple.internal.Strings;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static io.wurmatron.petfeeder.PetFeeder.GSON;
import static io.wurmatron.petfeeder.gpio.IOController.*;

public class SensorController {

    public static final int COUNT_FOR_AVERAGE_CALIBRATION = 100;
    public static final File calibrationFile = new File("calibration.json");

    public static double diff;
    public static boolean isCalibrated = false;


    @OpenApi(
            summary = "Run the servo motor",
            description = "Run the servo for a given amount of time.",
            responses = {
                    @OpenApiResponse(status = "200", description = "Servo will spin for the given amount of time (ms)"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            queryParams = @OpenApiParam(name = "time", type = Integer.class, description = "Amount of time the servo will be active for"),
            tags = {"Test"}
    )
    public static Handler runServo = ctx -> {
        int inputTime;
        try {
            inputTime = ctx.queryParam("time", Integer.class).getOrNull();
        } catch (Exception e) {
            inputTime = 200;  // Default
        }
        int time = inputTime;
        IOController.servo(50, time);
        ctx.contentType("application/json").status(200);
    };

    @OpenApi(
            summary = "Test the led",
            description = "Test run the led to see if it can blink",
            responses = {
                    @OpenApiResponse(status = "200", description = "LED will blink a few times"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            queryParams = @OpenApiParam(name = "count", type = Integer.class, description = "Amount of times the LED will blink"),
            tags = {"Test"}
    )
    public static Handler blinkLED = ctx -> {
        int inputCount;
        try {
            inputCount = ctx.queryParam("count", Integer.class).getOrNull();
        } catch (Exception e) {
            inputCount = 5;  // Default
        }
        int count = inputCount;
        PetFeeder.SCHEDULE.schedule(() -> {
            for (int c = 0; c < count; c++) {
                led(true);
                sleep(250);
                led(false);
                sleep(250);
            }
        }, 0, TimeUnit.SECONDS);
        ctx.contentType("application/json").status(200);
    };

    @OpenApi(
            summary = "Test the photo resistor",
            description = "Test run the photo resistor, will be used in combination with LED, (Best to use in dark room without food)",
            responses = {
                    @OpenApiResponse(status = "200", description = "LED will blink a few times along with testing the photo resistor", content = @OpenApiContent(from = Boolean.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"Test"}
    )
    public static Handler testLevelSensor = ctx -> {
        led(true);
        ctx.contentType("application/json").status(200).result("{\"result\": " + IOController.photo() + "}");
        led(false);
    };

    @OpenApi(
            summary = "Get Load-Cell weight",
            description = "Get the load cells current weight",
            responses = {
                    @OpenApiResponse(status = "200", description = "Weight of the load cell", content = @OpenApiContent(from = Double.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
                    @OpenApiResponse(status = "409", description = "Load Cell not Calibrated"),
            },
            tags = {"Test"}
    )
    public static Handler getLoadCellWeight = ctx -> {
        if (isCalibrated) {
            loadCell.read();
            double aboveDefault = loadCell.value - loadCell.emptyValue;
            double weight = aboveDefault / diff;
            ctx.contentType("application/json").status(200).result("{ \"weight\": " + weight + " }");
        } else {
            ctx.contentType("application/json").status(409);
        }
    };

    @OpenApi(
            summary = "Calibrate Load-Cell weight",
            description = "Calibrate the load cells current weight",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = CalibrationLoadCell.class), description = "Staged calibration one 'without' any added weight and one 'with' added known weight"),
            responses = {
                    @OpenApiResponse(status = "200", description = "Weight of the load cell", content = @OpenApiContent(from = Double.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
                    @OpenApiResponse(status = "422", description = "Invalid Json / Calibration Type"),
            },
            tags = {"Test"}
    )
    public static Handler calibrateLoadCell = ctx -> {
        try {
            CalibrationLoadCell calibration = GSON.fromJson(ctx.body(), CalibrationLoadCell.class);
            if (calibration.stage.equals(CalibrationLoadCell.Stage.WITHOUT)) {
                long avg = 0;
                for (int x = 0; x < COUNT_FOR_AVERAGE_CALIBRATION; x++) {
                    loadCell.read();
                    avg += loadCell.value;
                }
                avg /= COUNT_FOR_AVERAGE_CALIBRATION;
                loadCell.emptyValue = avg;
                loadCell.emptyWeight = calibration.weight;
                ctx.contentType("application/json").status(200);
            } else if (calibration.stage.equals(CalibrationLoadCell.Stage.WITH)) {
                loadCell.read();
                long avg = 0;
                for (int x = 0; x < COUNT_FOR_AVERAGE_CALIBRATION; x++) {
                    loadCell.read();
                    avg += loadCell.value;
                }
                loadCell.calibrationWeight = calibration.weight;
                avg /= COUNT_FOR_AVERAGE_CALIBRATION;
                diff = (avg - loadCell.emptyValue) / calibration.weight;
                isCalibrated = true;
                saveCalibration();
                ctx.contentType("application/json").status(200);
            } else {
                ctx.contentType("application/json").status(422);
            }
        } catch (JsonParseException e) {
            ctx.contentType("application/json").status(422);
        }
    };

    private static void saveCalibration() {
        if (!calibrationFile.exists()) {
            try {
                calibrationFile.createNewFile();
            } catch (Exception e) {
                System.out.println("Failed to save calibration file!");
                e.printStackTrace();
            }
        }
        try {
            Calibration cal = new Calibration(loadCell.emptyValue, loadCell.emptyWeight, loadCell.calibrationValue, loadCell.calibrationWeight, diff);
            Files.write(calibrationFile.toPath(), GSON.toJson(cal).getBytes());
        } catch (Exception e) {
            System.out.println("Failed to save calibration file!");
            e.printStackTrace();
        }
    }

    public static void loadCalibration() {
        try {
            if (calibrationFile.exists()) {
                Calibration calibration = GSON.fromJson(Strings.join(Files.readAllLines(calibrationFile.toPath()).toArray(new String[0]), "\n"), Calibration.class);
                loadCell.emptyValue = calibration.emptyVal;
                loadCell.emptyWeight = calibration.emptyWeight;
                loadCell.calibrationValue = calibration.calibratedVal;
                loadCell.calibrationWeight = calibration.calibratedWeight;
                diff = calibration.diff;
                isCalibrated = true;
            }
        } catch (Exception e) {
            System.out.println("Failed to load calibration file!, Deleting...");
            calibrationFile.delete();
        }
    }
}
