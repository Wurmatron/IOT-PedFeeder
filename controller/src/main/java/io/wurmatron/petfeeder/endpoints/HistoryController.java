package io.wurmatron.petfeeder.endpoints;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.petfeeder.models.Consume;
import io.wurmatron.petfeeder.models.Dispense;

public class HistoryController {

    @OpenApi(
            summary = "History of dispensing",
            description = "History of dispensing",
            queryParams = {@OpenApiParam(name = "start", type = Integer.class, description = "Starting point for history"),
                    @OpenApiParam(name = "end", type = Integer.class, description = "Ending point for history")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Specific history is returned", content = @OpenApiContent(from = Dispense[].class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"History"}
    )
    // TODO Implement
    public static Handler getDispenseHistory = ctx -> {
        ctx.contentType("application/json").status(501);
    };

    @OpenApi(
            summary = "History of consumption",
            description = "History of consumption",
            pathParams = {@OpenApiParam(name = "start", type = Integer.class, description = "Starting point for history"),
                    @OpenApiParam(name = "end", type = Integer.class, description = "Ending point for history")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Specific history is returned", content = @OpenApiContent(from = Consume[].class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"History"}
    )
    // TODO Implement
    public static Handler getConsumeHistory = ctx -> {
        ctx.contentType("application/json").status(501);
    };
}
