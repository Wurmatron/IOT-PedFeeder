package io.wurmatron.petfeeder;

public class Config {

    public boolean debug;
    public int port;

    public Config() {
        this.debug = false;
        this.port = 8080;
    }

    public Config(boolean debug, int port) {
        this.debug = debug;
        this.port = port;
    }
}
