package io.wurmatron.petfeeder.endpoints;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.petfeeder.models.Consume;
import io.wurmatron.petfeeder.models.Dispense;
import io.wurmatron.petfeeder.sql.SQLCache;

import java.util.ArrayList;
import java.util.List;

public class HistoryController {

    public static final int MAX_HISTORY_PER_REQUEST = 200;

    @OpenApi(
            summary = "History of dispensing",
            description = "History of dispensing",
            queryParams = {@OpenApiParam(name = "count", type = Integer.class, description = "Amount of history entries to display")},
            pathParams = {@OpenApiParam(name = "startingPoint", type = Long.class, description = "Timestamp to start time with")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Specific history is returned", content = @OpenApiContent(from = Dispense[].class)),
                    @OpenApiResponse(status = "400", description = "Invalid startingPoint Timestamp"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"History"}
    )
    public static Handler getDispenseHistory = ctx -> {
        try {
            // Validate inputs
            long startingPoint = Long.parseLong(ctx.pathParam("startingPoint"));
            if(startingPoint <= 0) {
                ctx.status(400).json("{\"message\": \"invalid startingPoint!\"}");
                return;
            }
            int count = MAX_HISTORY_PER_REQUEST;
            if(ctx.queryParam("count") != null && !ctx.queryParam("count").isEmpty()) {
                try {
                    count = Integer.parseInt(ctx.queryParam("count"));
                    if(count <= 0) {
                        ctx.status(400).json("{\"message\": \"count must be a number greater than 0\"}");
                        return;
                    }
                } catch (NumberFormatException e) {
                    ctx.status(400).json("{\"message\": \"count must be a number\"}");
                    return;
                }
            }
            // Create History
            Dispense[] history = SQLCache.getDispenseHistory();
            List<Dispense> requestedHistory = new ArrayList<>();
            for(int x = 0; x < (Math.min(count, history.length)); x++) {
                requestedHistory.add(history[x]);
            }
            ctx.json(200).json(requestedHistory.toArray(new Dispense[0]));
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"message\": \"invalid startingPoint!\"}");
        }
    };

    @OpenApi(
            summary = "History of consumption",
            description = "History of consumption",
            queryParams = {@OpenApiParam(name = "count", type = Integer.class, description = "Amount of history entries to display")},
            pathParams = {@OpenApiParam(name = "startingPoint", type = Long.class, description = "Timestamp to start time with")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Specific history is returned", content = @OpenApiContent(from = Consume[].class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"History"}
    )
    public static Handler getConsumeHistory = ctx -> {
        try {
            // Validate inputs
            long startingPoint = Long.parseLong(ctx.pathParam("startingPoint"));
            if(startingPoint <= 0) {
                ctx.status(400).json("{\"message\": \"invalid startingPoint!\"}");
                return;
            }
            int count = MAX_HISTORY_PER_REQUEST;
            if(ctx.queryParam("count") != null && !ctx.queryParam("count").isEmpty()) {
                try {
                    count = Integer.parseInt(ctx.queryParam("count"));
                    if(count <= 0) {
                        ctx.status(400).json("{\"message\": \"count must be a number greater than 0\"}");
                        return;
                    }
                } catch (NumberFormatException e) {
                    ctx.status(400).json("{\"message\": \"count must be a number\"}");
                    return;
                }
            }
            // Create History
            Consume[] history = SQLCache.getConsumeHistory();
            List<Consume> requestedHistory = new ArrayList<>();
            for(int x = 0; x < (Math.min(count, history.length)); x++) {
                requestedHistory.add(history[x]);
            }
            ctx.json(200).json(requestedHistory.toArray(new Consume[0]));
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"message\": \"invalid startingPoint!\"}");
        }
    };
}
