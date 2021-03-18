package io.wurmatron.petfeeder.endpoints;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.petfeeder.models.CalibrationLoadCell;
import io.wurmatron.petfeeder.models.Dispense;

public class SensorController {

    @OpenApi(
            summary = "Run the servo motor",
            description = "Run the servo for a given amount of time.",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Dispense.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "Servo will spin for the given amount of time"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"Test"}
    )
    // TODO Implement
    public static Handler runServo = ctx -> {
        ctx.contentType("application/json").status(501);
    };

    @OpenApi(
            summary = "Test the led",
            description = "Test run the led to see if it can blink",
            responses = {
                    @OpenApiResponse(status = "200", description = "LED will blink a few times"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"Test"}
    )
    // TODO Implement
    public static Handler blinkLED = ctx -> {
        ctx.contentType("application/json").status(501);
    };

    @OpenApi(
            summary = "Test the photo resistor",
            description = "Test run the photo resistor, will be used in combination with LED, (Best to use in dark room without food)",
            responses = {
                    @OpenApiResponse(status = "200", description = "LED will blink a few times along with testing the photo resistor", content = @OpenApiContent(from = Boolean[].class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"Test"}
    )
    // TODO Implement
    public static Handler testLevelSensor = ctx -> {
        ctx.contentType("application/json").status(501);
    };

    @OpenApi(
            summary = "Get Load-Cell weight",
            description = "Get the load cells current weight",
            responses = {
                    @OpenApiResponse(status = "200", description = "Weight of the load cell", content = @OpenApiContent(from = Double.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
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
