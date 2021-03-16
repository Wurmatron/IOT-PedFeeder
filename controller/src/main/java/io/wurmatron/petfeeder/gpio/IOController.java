package io.wurmatron.petfeeder.gpio;

import com.pi4j.io.gpio.*;

public class IOController {

    public static GpioController controller;

    // Pins
    public static GpioPinDigitalOutput led;
    public static GpioPinInput photo;
    public static GpioPinPwmOutput servo;

    public static void setup() {
        controller = GpioFactory.getInstance();
        led = controller.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        photo = controller.provisionDigitalInputPin(RaspiPin.GPIO_01,"photo");
    }

    public static void led(boolean bool) {

    }

    public static int photo() {
        return -1;
    }

    public static void shutdown() {
        controller.shutdown();
    }
}
