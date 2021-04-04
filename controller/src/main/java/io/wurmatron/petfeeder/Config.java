package io.wurmatron.petfeeder;

public class Config {

    public boolean debug;
    public int port;
    public int motorPWM;
    public boolean testing;
    public int schedulePollInterval;

    public Database Database;

    public Config() {
        this.debug = false;
        this.port = 8080;
        this.Database = new Database();
        this.motorPWM = 50;
        this.testing = false;
        schedulePollInterval = 60;
    }

    public Config(boolean debug, int port, int motorPWM, boolean testing, int schedulePollInterval, Config.Database database) {
        this.debug = debug;
        this.port = port;
        this.motorPWM = motorPWM;
        this.testing = testing;
        this.schedulePollInterval = schedulePollInterval;
        Database = database;
    }

    public class Database {
        public String address;
        public String database_name;
        public int port;
        public String username;
        public String password;

        public Database(String address, String database_name, int port, String username, String password) {
            this.address = address;
            this.database_name = database_name;
            this.port = port;
            this.username = username;
            this.password = password;
        }

        public Database() {
            this.address = "localhost";
            this.database_name = "pet-feeder";
            this.username = "root";
            this.password = "";
            this.port = 3306;
        }
    }
}
