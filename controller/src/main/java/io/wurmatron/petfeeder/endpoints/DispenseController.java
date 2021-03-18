package io.wurmatron.petfeeder.endpoints;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.petfeeder.models.Dispense;

public class DispenseController {

    @OpenApi(
            summary = "Dispense the requested amount of food",
            description = "Dispense the requested amount of food",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Dispense.class), description = "Dispense Instructions"),
            responses = {
                    @OpenApiResponse(status = "200", description = "Food has been scheduled to be dispensed", content = @OpenApiContent(from = Dispense.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
                    @OpenApiResponse(status = "409", description = "Low Food Level, may not dispense full amount of food!"),
            },
            tags = {"Dispense"}
    )
    // TODO Implement
    public static Handler dispenseFood = ctx -> {
        ctx.contentType("application/json").status(501);
    };
}
