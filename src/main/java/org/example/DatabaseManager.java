package org.example;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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


            String createBooksTable = """
            CREATE TABLE IF NOT EXISTS books (
                id VARCHAR(50) PRIMARY KEY,
                title VARCHAR(255),
                authors VARCHAR(255),
                publisher VARCHAR(255),
                description TEXT,
                page_count INT,
                average_rating DOUBLE
            )
        """;

            String createUserBooksTable = """
            CREATE TABLE IF NOT EXISTS user_books (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT,
                book_id VARCHAR(50),
                status VARCHAR(50) DEFAULT 'owned',
                is_favorite BOOLEAN DEFAULT FALSE,
                date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id),
                UNIQUE(user_id, book_id)
            )
        """;

            st.execute(createBooksTable);
            st.execute(createUserBooksTable);

            st.execute(createTable);

            createDefaultUser(conn); // user demo

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // salveaza cartea in baza de date
    public boolean saveBook(Book book) {
        String sql = "MERGE INTO books(id, title, authors, publisher, description, page_count, average_rating) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthors());
            ps.setString(4, book.getPublisher());
            ps.setString(5, book.getDescription());
            ps.setInt(6, book.getPageCount());
            ps.setDouble(7, book.getAverageRating());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // adauga cartea la biblioteca utilizatorului cu is_fav = false
    public boolean addBookToUserLibrary(long user_id, String book_id, boolean is_favorite) {
        String sql = """
        MERGE INTO user_books(user_id, book_id, is_favorite, status) 
        KEY(user_id, book_id) 
        VALUES (?, ?, ?, 'owned')
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, user_id);
            ps.setString(2, book_id);
            ps.setBoolean(3, is_favorite);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBookFromUserLibrary(long user_id, String book_id) {
        String sql = "DELETE FROM user_books WHERE user_id = ? AND book_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, user_id);
            ps.setString(2, book_id);

            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0; // Returnează true dacă s-a șters măcar o înregistrare

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // metoda pentru a adauga la favorite o carte
    public boolean updateBookFavoriteStatus(long user_id, String book_id, boolean is_favorite) {
        String sql = "UPDATE user_books SET is_favorite = ? WHERE user_id = ? AND book_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, is_favorite);
            ps.setLong(2, user_id);
            ps.setString(3, book_id);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    // verif daca cartea e deja in biblioteca utilizatorului
    public boolean isBookInUserLibrary(long user_id, String book_id) {
        String sql = "SELECT 1 FROM user_books WHERE user_id = ? AND book_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, user_id);
            ps.setString(2, book_id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();

            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // obtine id-ul userului
    public long getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return -1;
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


    // toate cartile din lista utilizatorului
    public List<Book> getUserBooks(long user_id) {
            List<Book> books = new ArrayList<>();
            String sql = """
            SELECT b.* FROM books b 
            JOIN user_books ub ON b.id = ub.book_id 
            WHERE ub.user_id = ?
        """;

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, user_id);
                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        Book book = new Book();
                        book.setAuthors(rs.getString("authors"));
                        book.setTitle(rs.getString("title"));
                        book.setDescription(rs.getString("description"));
                        book.setPublisher(rs.getString("publisher"));
                        book.setId(rs.getString("id"));
                        book.setAverageRating(rs.getDouble("average_rating"));
                        book.setPageCount(rs.getInt("page_count"));
                        books.add(book);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();

            }
            return books;
    }

    // toate cartile favorite din lista utilizatorului
    public List<Book> getUserFavoriteBooks(long user_id) {
        List<Book> books = new ArrayList<>();
        String sql = """
        SELECT b.* FROM books b 
        JOIN user_books ub ON b.id = ub.book_id 
        WHERE ub.user_id = ? AND ub.is_favorite = true
    """;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, user_id);
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Book book = new Book();
                    book.setAuthors(rs.getString("authors"));
                    book.setTitle(rs.getString("title"));
                    book.setDescription(rs.getString("description"));
                    book.setPublisher(rs.getString("publisher"));
                    book.setId(rs.getString("id"));
                    book.setAverageRating(rs.getDouble("average_rating"));
                    book.setPageCount(rs.getInt("page_count"));
                    books.add(book);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return books;
    }


    // Verifică dacă cartea este marcată ca favorită
    public boolean isBookFavorite(long userId, String bookId) {
        String sql = "SELECT is_favorite FROM user_books WHERE user_id = ? AND book_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_favorite");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Șterge cartea de la favorite (setează is_favorite = false)
    public boolean removeFromFavorites(long userId, String bookId) {
        String sql = "UPDATE user_books SET is_favorite = false WHERE user_id = ? AND book_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, false);
            ps.setLong(2, userId);
            ps.setString(3, bookId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




}

