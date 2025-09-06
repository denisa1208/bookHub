package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksAPI {
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private final Gson gson = new Gson();

    /**
     * Caută cărți pe Google Books API
     * @param query termenul de căutare
     * @param maxResults numărul maxim de rezultate (implicit 20)
     * @return lista de cărți găsite
     */
    public List<Book> searchBooks(String query, int maxResults) {
        List<Book> books = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return books;
        }

        try {
            // Construiește URL-ul pentru API
            String encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?q=" + encodedQuery + "&maxResults=" + maxResults;

            System.out.println("API Request: " + urlString);

            // Efectuează request-ul HTTP
            String jsonResponse = makeHttpRequest(urlString);

            if (jsonResponse == null) {
                System.out.println("Nu s-a primit răspuns de la API");
                return books;
            }

            // Parse JSON response
            books = parseJsonResponse(jsonResponse);

        } catch (Exception e) {
            System.err.println("Eroare la căutarea cărților: " + e.getMessage());
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Caută cărți cu numărul implicit de rezultate (20)
     */
    public List<Book> searchBooks(String query) {
        return searchBooks(query, 20);
    }

    /**
     * Efectuează request HTTP și returnează răspunsul
     */
    private String makeHttpRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Setează headers
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "BookHub/1.0");
            connection.setConnectTimeout(10000); // 10 secunde
            connection.setReadTimeout(15000);    // 15 secunde

            // Verifică codul de răspuns
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.err.println("API Error: HTTP " + responseCode);
                return null;
            }

            // Citește răspunsul
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            return response.toString();

        } catch (Exception e) {
            System.err.println("Eroare la request HTTP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parse răspunsul JSON și creează lista de cărți
     */
    private List<Book> parseJsonResponse(String jsonResponse) {
        List<Book> books = new ArrayList<>();

        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            if (!jsonObject.has("items")) {
                System.out.println("Nu s-au găsit cărți pentru această căutare");
                return books;
            }

            JsonArray items = jsonObject.getAsJsonArray("items");

            for (JsonElement item : items) {
                Book book = parseBookFromJson(item.getAsJsonObject());
                if (book != null) {
                    books.add(book);
                }
            }

        } catch (Exception e) {
            System.err.println("Eroare la parsarea JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Creează un obiect Book din JSON
     */
    private Book parseBookFromJson(JsonObject bookJson) {
        try {
            Book book = new Book();

            // ID-ul cărții
            if (bookJson.has("id")) {
                book.setId(bookJson.get("id").getAsString());
            }

            // Informațiile volumului
            if (bookJson.has("volumeInfo")) {
                JsonObject volumeInfo = bookJson.getAsJsonObject("volumeInfo");

                // Titlu
                if (volumeInfo.has("title")) {
                    book.setTitle(volumeInfo.get("title").getAsString());
                }

                // Autori
                if (volumeInfo.has("authors")) {
                    JsonArray authors = volumeInfo.getAsJsonArray("authors");
                    StringBuilder authorsStr = new StringBuilder();
                    for (int i = 0; i < authors.size(); i++) {
                        if (i > 0) authorsStr.append(", ");
                        authorsStr.append(authors.get(i).getAsString());
                    }
                    book.setAuthors(authorsStr.toString());
                }

                // Publisher
                if (volumeInfo.has("publisher")) {
                    book.setPublisher(volumeInfo.get("publisher").getAsString());
                }



                // Descriere
                if (volumeInfo.has("description")) {
                    String description = volumeInfo.get("description").getAsString();
                    // Limitează descrierea la 500 de caractere
                    if (description.length() > 500) {
                        description = description.substring(0, 497) + "...";
                    }
                    book.setDescription(description);
                }

                // Numărul de pagini
                if (volumeInfo.has("pageCount")) {
                    book.setPageCount(volumeInfo.get("pageCount").getAsInt());
                }

                // Rating
                if (volumeInfo.has("averageRating")) {
                    book.setAverageRating(volumeInfo.get("averageRating").getAsDouble());
                }

            }

            // Returnează cartea doar dacă are titlu
            return (book.getTitle() != null && !book.getTitle().isEmpty()) ? book : null;

        } catch (Exception e) {
            System.err.println("Eroare la parsarea cărții: " + e.getMessage());
            return null;
        }
    }

    /**
     * Testează API-ul cu o căutare simplă
     */
    public static void main(String[] args) {
        GoogleBooksAPI api = new GoogleBooksAPI();
        List<Book> books = api.searchBooks("Harry Potter", 5);

        System.out.println("Găsite " + books.size() + " cărți:");
        for (Book book : books) {
            System.out.println("- " + book.getDisplayText());
        }
    }
}