package io.wurmatron.petfeeder;

public class Config {

    public boolean debug;
    public int port;

    public Database Database;

    public Config() {
        this.debug = false;
        this.port = 8080;
        this.Database = new Database();
    }

    public Config(boolean debug, int port, Config.Database database) {
        this.debug = debug;
        this.port = port;
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
