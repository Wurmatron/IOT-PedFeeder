package io.wurmatron.petfeeder.gpio;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class Pinning {
    // https://pinout.xyz/pinout/wiringpi
    public static final Pin load_cell_sck = RaspiPin.GPIO_04;
    public static final Pin load_cell_dt = RaspiPin.GPIO_05;
    public static final Pin servo_pwm = RaspiPin.GPIO_02;
    public static final Pin photo_D0 = RaspiPin.GPIO_00;
    public static final Pin led_pos = RaspiPin.GPIO_29;
}
