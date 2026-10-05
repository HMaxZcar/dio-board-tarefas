package br.com.dio.board.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionConfig {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/board?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "board";
    private static final String DEFAULT_PASSWORD = "board";

    private ConnectionConfig() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

    public static String getUrl() {
        return envOrDefault("DB_URL", DEFAULT_URL);
    }

    public static String getUser() {
        return envOrDefault("DB_USER", DEFAULT_USER);
    }

    public static String getPassword() {
        return envOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
