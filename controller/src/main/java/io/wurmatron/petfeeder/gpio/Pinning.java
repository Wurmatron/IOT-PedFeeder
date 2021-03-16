package io.wurmatron.petfeeder.gpio;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class Pinning {

    public static final int load_cell_vcc = 2;
    public static final int load_cell_gnd = 6;
    public static final Pin load_cell_sck = RaspiPin.GPIO_23;
    public static final Pin load_cell_dt = RaspiPin.GPIO_24;

    public static final int servo_pos = 4;
    public static final int servo_neg = 14;
    public static final Pin servo_pwm = RaspiPin.GPIO_27;

    public static final int photo_vcc = 1;
    public static final int photo_gnd = 9;
    public static final Pin photo_D0 = RaspiPin.GPIO_17;

    public static final Pin led_pos = RaspiPin.GPIO_21;
    public static final int led_neg = 39;

}