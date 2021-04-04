package io.wurmatron.petfeeder.endpoints;

import com.google.gson.JsonParseException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.petfeeder.PetFeeder;
import io.wurmatron.petfeeder.gpio.IOController;
import io.wurmatron.petfeeder.models.Dispense;

import java.time.Instant;

public class DispenseController {

    @OpenApi(
            summary = "Dispense the requested amount of food",
            description = "Dispense the requested amount of food",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Dispense.class), description = "Dispense Instructions"),
            responses = {
                    @OpenApiResponse(status = "202", description = "Food has been scheduled to be dispensed", content = @OpenApiContent(from = Dispense.class)),
                    @OpenApiResponse(status = "400", description = "Invalid Json"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
                    @OpenApiResponse(status = "409", description = "Low Food Level, may not dispense full amount of food!"),
            },
            tags = {"Dispense"}
    )
    public static Handler dispenseFood = ctx -> {
        if (!IOController.photo()) { // No Low Food Warning
            try {
                Dispense dispense = PetFeeder.GSON.fromJson(ctx.body(), Dispense.class);
                if (dispense.amount <= 0) {
                    ctx.status(400).json("{\"message\": \"Amount must be greater than 0\"}");
                    return;
                }
                dispense.before = IOController.getLoadCellWeight();
                dispense.timestamp = Instant.now().getEpochSecond();
                dispense(dispense);
                ctx.status(202).json(PetFeeder.GSON.toJson(dispense));
            } catch (JsonParseException e) {
                ctx.status(400).json("{\"message\": \"Invalid Json\"}");
            }
        } else {
            ctx.status(409).json("{\"message\": \"Feeder food is low\"}");
        }
    };

    // TODO Implement
    public static void dispense(Dispense dispense) {

    }
}
