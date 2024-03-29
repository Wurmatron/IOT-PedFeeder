package io.wurmatron.petfeeder.sql;

import io.wurmatron.petfeeder.Config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseManager {

    private Config.Database config;
    private Connection connection;

    public DatabaseManager(Config.Database config) {
        this.config = config;
        connect();
    }

    public boolean connect() {
        String url = "jdbc:mysql://" + config.address + ":" + config.port + "/" + config.database_name + "?useSSL=false";
        try {
            connection = DriverManager.getConnection(url, config.username, config.password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public final Connection getConnection() {
        try {
            if (connection.isClosed()) {
                connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
