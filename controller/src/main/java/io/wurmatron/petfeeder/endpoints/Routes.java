package io.wurmatron.petfeeder.endpoints;

import io.javalin.Javalin;

public class Routes {

    public static void register(Javalin app) {
        // Dispense
        app.post("/dispense",DispenseController.dispenseFood);
        // History
        app.get("/history/dispense",HistoryController.getDispenseHistory);
        app.get("/history/consume",HistoryController.getConsumeHistory);
        // Schedule
        app.get("/schedules",ScheduleController.schedules);
        app.get("/schedule/:id",ScheduleController.getSchedule);
        app.post("/schedule",ScheduleController.createSchedule);
        app.put("/schedule",ScheduleController.updateSchedule);
        app.delete("/schedule",ScheduleController.deleteSchedule);
        // Sensor
        app.post("/sensor/led", SensorController.blinkLED);
        app.post("/sensor/level", SensorController.testLevelSensor);
        app.get("/sensor/weight", SensorController.getLoadCellWeight);
        app.post("/sensor/calibrate", SensorController.calibrateLoadCell);
    }
}
