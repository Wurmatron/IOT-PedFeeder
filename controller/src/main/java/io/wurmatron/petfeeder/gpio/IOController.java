package io.wurmatron.petfeeder.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import io.wurmatron.petfeeder.PetFeeder;

import java.util.concurrent.TimeUnit;

public class IOController {

    public static GpioController controller;

    // Pins
    public static GpioPinDigitalOutput led;
    public static GpioPinInput photo;
    public static GpioPinPwmOutput servo;

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
}
