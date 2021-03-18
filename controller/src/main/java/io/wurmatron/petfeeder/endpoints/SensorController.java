package io.wurmatron.petfeeder.endpoints;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.petfeeder.PetFeeder;
import io.wurmatron.petfeeder.gpio.IOController;
import io.wurmatron.petfeeder.models.CalibrationLoadCell;

import java.util.concurrent.TimeUnit;

import static io.wurmatron.petfeeder.gpio.IOController.led;
import static io.wurmatron.petfeeder.gpio.IOController.sleep;

public class SensorController {

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
    // TODO Implement
    public static Handler getLoadCellWeight = ctx -> {
        ctx.contentType("application/json").status(501);
    };

    @OpenApi(
            summary = "Calibrate Load-Cell weight",
            description = "Calibrate the load cells current weight",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = CalibrationLoadCell.class), description = "Staged calibration one 'without' any added weight and one 'with' added known weight"),
            responses = {
                    @OpenApiResponse(status = "200", description = "Weight of the load cell", content = @OpenApiContent(from = Double.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"Test"}
    )
    // TODO Implement
    public static Handler calibrateLoadCell = ctx -> {
        ctx.contentType("application/json").status(501);
    };
}
