package io.wurmatron.petfeeder.endpoints;

import com.google.gson.JsonParseException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.petfeeder.PetFeeder;
import io.wurmatron.petfeeder.models.Schedule;
import io.wurmatron.petfeeder.sql.SQLCache;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduleController {

    public static final int MAX_PER_REQUEST = 25;

    // Cache
    public static NonBlockingHashMap<Integer, Schedule> scheduleCache = new NonBlockingHashMap<>();

    @OpenApi(
            summary = "Get a list of all schedules",
            description = "List all the current schedules",
            queryParams = {
                    @OpenApiParam(name = "start", type = Integer.class, description = "Start pos of schedules"),
                    @OpenApiParam(name = "count", type = Integer.class, description = "How many schedules to display (Up to max)")
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "List of all the schedules in existence", content = @OpenApiContent(from = Schedule[].class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            tags = {"Schedule"}
    )
    public static Handler schedules = ctx -> {
        Schedule[] schedules = SQLCache.getSchedules();
        int start = 0;
        int count = MAX_PER_REQUEST;
        // Set start if exists
        if (ctx.queryParam("start") != null && !ctx.queryParam("start").isBlank()) {
            try {
                start = Integer.parseInt(ctx.queryParam("start"));
            } catch (NumberFormatException e) {
                ctx.status(400).json("{\"message\": \"start must be a integer\"}");
                return;
            }
        }
        // Set count if exists
        if (ctx.queryParam("count") != null && !ctx.queryParam("count").isBlank()) {
            try {
                count = Integer.parseInt(ctx.queryParam("count"));
            } catch (NumberFormatException e) {
                ctx.status(400).json("{\"message\": \"start must be a integer\"}");
                return;
            }
        }
        // Copy and format for endpoint
        List<Schedule> copySchedules = new ArrayList<>();
        for (int x = start; x < (Math.min(schedules.length, (start + count))); x++) {
            copySchedules.add(schedules[x]);
        }
        ctx.status(copySchedules.size() > 0 ? 200 : 204).json(PetFeeder.GSON.toJson(copySchedules.toArray(new Schedule[0])));
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
    public static Handler getSchedule = ctx -> {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            // Check cache
            if (scheduleCache.containsKey(id)) {
                ctx.status(200).json(PetFeeder.GSON.toJson(scheduleCache.get(id)));
            } else { // Not in cache
                Schedule[] schedules = SQLCache.getSchedules();
                for (Schedule schedule : schedules) {
                    scheduleCache.put(schedule.scheduleID, schedule);
                    if (schedule.scheduleID == id) {
                        ctx.status(200).json(PetFeeder.GSON.toJson(schedule));
                        return;
                    }
                }
                // Does not exist
                ctx.status(404);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"message\": \"id must be a integer\"}");
        }
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
    public static Handler createSchedule = ctx -> {
        try {
            Schedule schedule = PetFeeder.GSON.fromJson(ctx.body(), Schedule.class);
            // Check for errors
            if (schedule.name.isEmpty())
                ctx.status(400).json("{\"message\": \"name must not be empty\"}");
            if (schedule.days.length > 0)
                ctx.status(400).json("{\"message\": \"days must be greater than 0\"}");
            if (schedule.time.length > 0)
                ctx.status(400).json("{\"message\": \"time must be greater than 0\"}");
            if (schedule.time.length != schedule.days.length) {
                ctx.status(400).json("{\"message\": \"time and days must be of the same count\"}");
            }
            if (schedule.amount <= 0) {
                ctx.status(400).json("{\"message\": \"amount must be greater than 0\"}");
            }
            // Check if schedule with name exists
            Schedule[] schedules = SQLCache.getSchedules();
            for (Schedule existing : schedules) {
                if (existing.name.equals(schedule.name)) {
                    ctx.status(409).json("{\"message\": \"Schedule with '" + existing.name + "' already exists\"}");
                    return;
                }
            }
            schedule.nextInterval = calculateNextInterval(schedule);
            SQLCache.add(schedule);
            // Update & Look for schedule in DB
            SQLCache.invalidateSchedules();
            schedules = SQLCache.getSchedules();
            for (Schedule existing : schedules) {
                if (existing.name.equals(schedule.name)) {
                    ctx.status(201).json(PetFeeder.GSON.toJson(existing));
                    break;
                }
            }
            ctx.status(500).json("{\"message\": \"Schedule failed to be added to DB\"}");
        } catch (JsonParseException e) {
            ctx.status(400).json("{\"message\": \"invalid json!\"}");
        }
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
    public static Handler updateSchedule = ctx -> {
        try {
            Schedule schedule = PetFeeder.GSON.fromJson(ctx.body(), Schedule.class);
            // Check if schedule is valid
            if (schedule.scheduleID < 0) {
                ctx.status(400).json("{\"message\": \"scheduleID must be greater or equal to 0\"}");
            }
            if (schedule.name != null && schedule.name.length() == 0) {
                ctx.status(400).json("{\"message\": \"name must not be empty\"}");
            }
            if (schedule.days != null && schedule.days.length > 0) {
                ctx.status(400).json("{\"message\": \"days must be greater than 0\"}");
            }
            if (schedule.time != null && schedule.time.length > 0) {
                ctx.status(400).json("{\"message\": \"time must be greater than 0\"}");
            }
            if (schedule.time != null && schedule.days != null && schedule.time.length != schedule.days.length) {
                ctx.status(400).json("{\"message\": \"time and days must be of the same count\"}");
            }
            if (schedule.amount <= 0) {
                ctx.status(400).json("{\"message\": \"amount must be greater than 0\"}");
            }
            // Find existing schedule
            Schedule[] schedules = SQLCache.getSchedules();
            for (Schedule existing : schedules) {
                if (existing.scheduleID == schedule.scheduleID) {
                    // Update existing if has update
                    if (!schedule.name.equals(existing.name)) {
                        existing.name = schedule.name;
                    }
                    if (schedule.nextInterval != existing.nextInterval && schedule.nextInterval > 0) {
                        existing.nextInterval = schedule.nextInterval;
                    }
                    if (!Arrays.equals(schedule.days, existing.days)) {
                        existing.days = schedule.days;
                    }
                    if (!Arrays.equals(schedule.time, existing.time)) {
                        existing.time = schedule.time;
                    }
                    if (schedule.amount != existing.amount && schedule.amount > 0) {
                        existing.amount = schedule.amount;
                    }
                    // Update existing schedule
                    SQLCache.updateSchedule(existing);
                    ctx.status(200).json(PetFeeder.GSON.toJson(existing));
                    return;
                }
            }
            ctx.status(404);
        } catch (JsonParseException e) {
            ctx.status(400).json("{\"message\": \"invalid json!\"}");
        }
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
    public static Handler deleteSchedule = ctx -> {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Schedule[] schedules = SQLCache.getSchedules();
            for (Schedule schedule : schedules) {
                if (schedule.scheduleID == id) {
                    SQLCache.deleteSchedule(id);
                    ctx.status(200).json(PetFeeder.GSON.toJson(schedule));
                    return;
                }
            }
            ctx.status(404);
        } catch (NumberFormatException e) {
            ctx.status(400).json("{\"message\": \"id must be a integer\"}");
        }
    };

    // TODO Implement
    public static long calculateNextInterval(Schedule schedule) {
        return 0;
    }
}
