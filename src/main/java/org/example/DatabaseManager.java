package org.example;
import org.example.User;
import java.sql.*;
import java.security.MessageDigest;
import java.util.Base64;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./biblioteca;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)){
            String createTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(100),
                    city VARCHAR(100)
                )
            """;
            conn.createStatement().execute(createTable);

            // Adaugă utilizator demo dacă nu există
            createDefaultUser(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultUser(Connection conn) throws SQLException {


    }
}
