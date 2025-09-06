package org.example;

public class Book {
    private String id;
    private String title;
    private String authors;
    private String publisher;
    private String description;
    private int pageCount;
    private double averageRating;

    // Constructor implicit
    public Book() {}

    // Constructor cu parametri principali
    public Book(String id, String title, String authors, String publisher) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
    }

    // Getters și Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    @Override
    public String toString() {
        return title + " - " + authors;
    }

    // Metodă helper pentru afișare scurtă
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        if (authors != null && !authors.isEmpty()) {
            sb.append(" de ").append(authors);
        }
        if (publisher != null && !publisher.isEmpty()) {
            sb.append(" (" + publisher + ")");
        }
        return sb.toString();
    }
}