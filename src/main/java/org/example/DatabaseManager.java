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
                    city VARCHAR(100),
                    admin_user VARCHAR(100)
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
                            "INSERT INTO users(username, password, email, city, admin_user) VALUES (?,?,?,?,?)")) {
                        ins.setString(1, "demo");
                        ins.setString(2, hash("demo")); // parola demo
                        ins.setString(3, "demo@example.com");
                        ins.setString(4, "Bucuresti");
                        ins.setString(5, "Admin"); // sau "Utilizator"
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

    public String createUser(String username, String plainPassword, String email, String city, String admin_user) {
        // Verifică dacă username-ul există deja
        if (userExists(username)) {
            return "USERNAME_EXISTS";
        }

        // Verifică dacă email-ul există deja
        if (emailExists(email)) {
            return "EMAIL_EXISTS";
        }

        String sql = "INSERT INTO users(username, password, email, city, admin_user) VALUES (?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("Încerc să creez user: " + username + " cu statut: " + admin_user);

            ps.setString(1, username);
            ps.setString(2, hash(plainPassword));
            ps.setString(3, email);
            ps.setString(4, city);
            ps.setString(5, admin_user);
            ps.executeUpdate();

            System.out.println("User creat cu succes!");

            // Returnează mesaj în funcție de tipul utilizatorului
            if ("Admin".equals(admin_user)) {
                return "ADMIN_CREATED";
            } else {
                return "USER_CREATED";
            }

        } catch (SQLException e) {
            System.out.println("Eroare la crearea user-ului: " + e.getMessage());
            e.printStackTrace();
            return "ERROR";
        }
    }

    // Metodă pentru verificarea username-ului
    private boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodă pentru verificarea email-ului
    private boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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


    public void displayAllUsers() {
        String sql = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== TOȚI UTILIZATORII DIN BAZA DE DATE ===");
            System.out.println("ID | Username | Email | City | Admin_User");
            System.out.println("----------------------------------------");

            while (rs.next()) {
                long id = rs.getLong("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String city = rs.getString("city");
                String adminUser = rs.getString("admin_user");

                System.out.println(id + " | " + username + " | " + email + " | " + city + " | " + adminUser);
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișarea utilizatorilor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

