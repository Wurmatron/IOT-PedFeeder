package io.wurmatron.petfeeder.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

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
}
