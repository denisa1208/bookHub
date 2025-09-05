package org.example;

import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;

public class DatabaseManager {
    // H2 file DB în folderul proiectului (persitentă pe disc)
    private static final String DB_URL = "jdbc:h2:file:./biblioteca;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement st = conn.createStatement()) {

            String createTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(100),
                    city VARCHAR(100)
                )
            """;
            st.execute(createTable);

            createDefaultUser(conn); // user demo

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultUser(Connection conn) throws SQLException {
        // Creează utilizatorul 'demo' / parola 'demo' dacă nu există
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM users WHERE username = ?")) {
            ps.setString(1, "demo");
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    try (PreparedStatement ins = conn.prepareStatement(
                            "INSERT INTO users(username, password, email, city) VALUES (?,?,?,?)")) {
                        ins.setString(1, "demo");
                        ins.setString(2, hash("demo")); // parola demo
                        ins.setString(3, "demo@example.com");
                        ins.setString(4, "Bucuresti");
                        ins.executeUpdate();
                    }
                }
            }
        }
    }

    public boolean authenticate(String username, String plainPassword) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString(1);
                    return storedHash.equals(hash(plainPassword));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createUser(String username, String plainPassword, String email, String city) {
        String sql = "INSERT INTO users(username, password, email, city) VALUES (?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash(plainPassword));
            ps.setString(3, email);
            ps.setString(4, city);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // de ex. UNIQUE violation pentru username
            return false;
        }
    }

    private static String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(md.digest(s.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

