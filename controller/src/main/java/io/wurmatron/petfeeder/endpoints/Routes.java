package io.wurmatron.petfeeder.endpoints;

import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.http.Context;
import io.wurmatron.petfeeder.PetFeeder;

import static io.javalin.core.security.SecurityUtil.roles;

public class Routes {

    public static void register(Javalin app) {
        accessManager(app);
        // Dispense
        app.post("/dispense", DispenseController.dispenseFood, roles(AuthRoles.USER, AuthRoles.ADMIN));
        // History
        app.get("/history/dispense/:startingPoint", HistoryController.getDispenseHistory, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.get("/history/consume/:startingPoint", HistoryController.getConsumeHistory, roles(AuthRoles.USER, AuthRoles.ADMIN));
        // Schedule
        app.get("/schedules", ScheduleController.schedules, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.get("/schedule/:id", ScheduleController.getSchedule, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.post("/schedule", ScheduleController.createSchedule, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.put("/schedule", ScheduleController.updateSchedule, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.delete("/schedule", ScheduleController.deleteSchedule, roles(AuthRoles.USER, AuthRoles.ADMIN));
        // Sensor
        app.post("/sensor/led", SensorController.blinkLED, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.post("/sensor/servo", SensorController.runServo, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.post("/sensor/level", SensorController.testLevelSensor, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.get("/sensor/weight", SensorController.getLoadCellWeight, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.post("/sensor/calibrate", SensorController.calibrateLoadCell, roles(AuthRoles.USER, AuthRoles.ADMIN));
    }

    public static void accessManager(Javalin app) {
        app.config.accessManager((handler, ctx, permittedRoles) -> {
            AuthRoles userRole = getUserRole(ctx);
            if (userRole.equals(AuthRoles.ADMIN) || permittedRoles.contains(userRole)) {
                handler.handle(ctx);
            } else {
                ctx.contentType("application/json").status(401).result("Unauthorized");
            }
        });
    }

    public static AuthRoles getUserRole(Context ctx) {
        if(PetFeeder.config.debug) {
            return AuthRoles.ADMIN;
        }
        String sessionToken = ctx.header("token");
        if (sessionToken != null && PetFeeder.loadAuthToken().equals(sessionToken)) {
            return AuthRoles.USER;
        }
        return AuthRoles.ANONYMOUS;
    }

    public enum AuthRoles implements Role {
        USER, ANONYMOUS, ADMIN
    }
}
