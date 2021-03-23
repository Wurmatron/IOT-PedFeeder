package io.wurmatron.petfeeder.endpoints;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.petfeeder.models.Schedule;

public class ScheduleController {

    @OpenApi(
            summary = "Get a list of all schedules",
            description = "List all the current schedules",
            responses = {
                    @OpenApiResponse(status = "200", description = "List of all the schedules in existence", content = @OpenApiContent(from = Schedule[].class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"Schedule"}
    )
    // TODO Implement
    public static Handler schedules = ctx -> {
        ctx.contentType("application/json").status(501);
    };

    @OpenApi(
            summary = "Get a schedule by its id",
            description = "Get a schedule by its id",
            pathParams = @OpenApiParam(name = "id", type = Integer.class, description = "ID of the schedule"),
            responses = {
                    @OpenApiResponse(status = "200", description = "Specific requested schedule is returned", content = @OpenApiContent(from = Schedule[].class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
                    @OpenApiResponse(status = "404", description = "Schedule does not exist"),
            },
            tags = {"Schedule"}
    )
    // TODO Implement
    public static Handler getSchedule = ctx -> {

    };

    @OpenApi(
            summary = "Create a new schedule",
            description = "Create a new schedule and add it to the DB",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Schedule.class), description = "Schedule to be added", required = true),
            responses = {
                    @OpenApiResponse(status = "200", description = "Schedule has been created", content = @OpenApiContent(from = Schedule.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
                    @OpenApiResponse(status = "409", description = "Schedule already exists"),
            },
            tags = {"Schedule"}
    )
    // TODO Implement
    public static Handler createSchedule = ctx -> {
        ctx.contentType("application/json").status(501);
    };

    @OpenApi(
            summary = "Update a existing schedule",
            description = "Update a existing schedule",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Schedule.class), description = "Schedule to be updated", required = true),
            responses = {
                    @OpenApiResponse(status = "200", description = "Schedule has been updated", content = @OpenApiContent(from = Schedule.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
                    @OpenApiResponse(status = "404", description = "Schedule does not exist"),
            },
            tags = {"Schedule"}
    )
    // TODO Implement
    public static Handler updateSchedule = ctx -> {
        ctx.contentType("application/json").status(501);
    };

    @OpenApi(
            summary = "Delete a given schedule",
            description = "Remove the given schedule from the DB",
            pathParams = @OpenApiParam(name = "id", type = Integer.class, description = "ID of the schedule"),
            responses = {
                    @OpenApiResponse(status = "200", description = "Schedule that has been deleted", content = @OpenApiContent(from = Schedule.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
                    @OpenApiResponse(status = "404", description = "Schedule does not exist"),
            },
            tags = {"Schedule"}
    )
    // TODO Implement
    public static Handler deleteSchedule = ctx -> {
        ctx.contentType("application/json").status(501);
    };
}
