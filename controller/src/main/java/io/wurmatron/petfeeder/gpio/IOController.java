package io.wurmatron.petfeeder.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.wiringpi.GpioUtil;
import io.wurmatron.petfeeder.PetFeeder;
import io.wurmatron.petfeeder.endpoints.SensorController;
import kiosk.HX711;

import java.util.concurrent.TimeUnit;

public class IOController {

    public static GpioController controller;

    // Pins
    public static GpioPinDigitalOutput led;
    public static GpioPinInput photo;
    public static GpioPinPwmOutput servo;
    // HX711
    public static GpioPinDigitalInput pinDT;
    public static GpioPinDigitalOutput pinSCK;
    public static HX711 loadCell;

    // Cache
    private static boolean photoState;

    public static void setup() {
        controller = GpioFactory.getInstance();
        led = controller.provisionDigitalOutputPin(Pinning.led_pos, PinState.LOW);
        led.setShutdownOptions(true, PinState.LOW);
        photo = controller.provisionDigitalInputPin(Pinning.photo_D0, "photo");
        photo.addListener((GpioPinListenerDigital) e -> {
            if (e.getState().isHigh()) {
                photoState = true;
            } else if (e.getState().isLow()) {
                photoState = false;
            }
        });
        servo = controller.provisionSoftPwmOutputPin(Pinning.servo_pwm);
        servo.setPwmRange(100);
        servo.setPwm(0);
        GpioUtil.enableNonPrivilegedAccess();
        pinDT = controller.provisionDigitalInputPin(Pinning.load_cell_dt, "HX_DT", PinPullResistance.OFF);
        pinSCK = controller.provisionDigitalOutputPin(Pinning.load_cell_sck, "HX_SLK", PinState.LOW);
        loadCell = new HX711(pinDT, pinSCK, 128);
        SensorController.loadCalibration();
    }

    public static void led(boolean state) {
        led.setState(state);
    }

    public static boolean photo() {
        return photoState;
    }

    public static void shutdown() {
        controller.shutdown();
    }

    public static void servo(int pwm,long timeMS) {
        PetFeeder.SCHEDULE.schedule(() -> {
            try {
                servo.setPwm(pwm);
                Thread.sleep(timeMS);
                servo.setPwm(0);
            } catch (InterruptedException e) {
            }
        }, 0, TimeUnit.SECONDS);
    }

    public static double getLoadCellWeight() {
        loadCell.read();
        return loadCell.weight;
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {}
    }
}
